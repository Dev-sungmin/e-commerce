<?php
// 내부 전용 API - Order Service만 호출. Gateway 라우팅 대상 아님.
// 외부 직접 노출 금지 (Gateway 완성 후 보안그룹에서 PHP 8080 포트 접근 제한 예정)
require_once __DIR__ . '/../db.php';
/** @var PDO $pdo */

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'METHOD_NOT_ALLOWED']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$orderId = $input['orderId'] ?? null;
$productId = $input['productId'] ?? null;
$quantity = $input['quantity'] ?? null;

if (!$orderId || !$productId || !$quantity || $quantity <= 0) {
    http_response_code(400);
    echo json_encode(['error' => 'INVALID_REQUEST']);
    exit;
}

$action = $_GET['action'] ?? null;

if ($action === 'deduct') {
    handleDeduct($pdo, $orderId, (int)$productId, (int)$quantity);
} elseif ($action === 'restore') {
    handleRestore($pdo, $orderId, (int)$productId, (int)$quantity);
} else {
    http_response_code(400);
    echo json_encode(['error' => 'INVALID_ACTION']);
}

function handleDeduct(PDO $pdo, string $orderId, int $productId, int $quantity): void
{
    $stmt = $pdo->prepare(
        "UPDATE inventory SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?"
    );
    $stmt->execute([$quantity, $productId, $quantity]);

    if ($stmt->rowCount() === 0) {
        $checkStmt = $pdo->prepare("SELECT stock_quantity FROM inventory WHERE id = ?");
        $checkStmt->execute([$productId]);
        $current = $checkStmt->fetchColumn();

        if ($current === false) {
            http_response_code(404);
            echo json_encode(['success' => false, 'error' => 'PRODUCT_NOT_FOUND']);
            return;
        }

        http_response_code(409);
        echo json_encode(['success' => false, 'error' => 'INSUFFICIENT_STOCK', 'remainingStock' => (int)$current]);
        return;
    }

    $logStmt = $pdo->prepare(
        "INSERT INTO inventory_log (product_id, order_id, change_amount, reason, created_at) VALUES (?, ?, ?, 'DEDUCT', NOW())"
    );
    $logStmt->execute([$productId, $orderId, -$quantity]);

    // name, price도 같이 조회 (Order Service가 주문 스냅샷을 만들 때 씀)
    $remainingStmt = $pdo->prepare("SELECT name, price, stock_quantity FROM inventory WHERE id = ?");
    $remainingStmt->execute([$productId]);
    $product = $remainingStmt->fetch(PDO::FETCH_ASSOC);

    echo json_encode([
        'success' => true,
        'remainingStock' => (int)$product['stock_quantity'],
        'productName' => $product['name'],
        'price' => (int)$product['price'],
    ]);
}

function handleRestore(PDO $pdo, string $orderId, int $productId, int $quantity): void
{
    // 멱등성 체크: 같은 order_id로 이미 RESTORE 처리된 적 있으면 재처리하지 않음
    $checkStmt = $pdo->prepare(
        "SELECT COUNT(*) FROM inventory_log WHERE order_id = ? AND product_id = ? AND reason = 'RESTORE'"
    );
    $checkStmt->execute([$orderId, $productId]);

    if ((int)$checkStmt->fetchColumn() > 0) {
        echo json_encode(['success' => true, 'note' => 'ALREADY_RESTORED']);
        return;
    }

    $stmt = $pdo->prepare("UPDATE inventory SET stock_quantity = stock_quantity + ? WHERE id = ?");
    $stmt->execute([$quantity, $productId]);

    if ($stmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode(['success' => false, 'error' => 'PRODUCT_NOT_FOUND']);
        return;
    }

    $logStmt = $pdo->prepare(
        "INSERT INTO inventory_log (product_id, order_id, change_amount, reason, created_at) VALUES (?, ?, ?, 'RESTORE', NOW())"
    );
    $logStmt->execute([$productId, $orderId, $quantity]);

    echo json_encode(['success' => true]);
}