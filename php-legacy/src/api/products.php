<?php
// 외부 공개 API - GET /api/products (목록, 페이지네이션/검색/가격필터), GET /api/products?id=1 (단건)
require_once __DIR__ . '/../db.php';
/** @var PDO $pdo */

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['error' => 'METHOD_NOT_ALLOWED']);
    exit;
}

$id = $_GET['id'] ?? null;

if ($id !== null) {
    fetchSingleProduct($pdo, (int)$id);
} else {
    fetchProductList($pdo);
}

function fetchSingleProduct(PDO $pdo, int $id): void
{
    $stmt = $pdo->prepare("SELECT id, name, price, stock_quantity, image_url FROM inventory WHERE id = ?");
    $stmt->execute([$id]);
    $product = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$product) {
        http_response_code(404);
        echo json_encode(['error' => 'PRODUCT_NOT_FOUND']);
        return;
    }

    echo json_encode(toCamelCase($product), JSON_UNESCAPED_UNICODE);
}

function fetchProductList(PDO $pdo): void
{
    $page = max(1, (int)($_GET['page'] ?? 1));
    $size = min(100, max(1, (int)($_GET['size'] ?? 20)));
    $offset = ($page - 1) * $size;

    $keyword = $_GET['keyword'] ?? null;
    $minPrice = isset($_GET['minPrice']) ? (int)$_GET['minPrice'] : null;
    $maxPrice = isset($_GET['maxPrice']) ? (int)$_GET['maxPrice'] : null;
    $idsParam = $_GET['ids'] ?? null;

    $where = [];
    $params = [];

    if ($keyword !== null && $keyword !== '') {
        $where[] = "name LIKE ?";
        $params[] = "%{$keyword}%";
    }
    if ($minPrice !== null) {
        $where[] = "price >= ?";
        $params[] = $minPrice;
    }
    if ($maxPrice !== null) {
        $where[] = "price <= ?";
        $params[] = $maxPrice;
    }
    if ($idsParam !== null) {
        $ids = array_values(array_filter(array_map('intval', explode(',', $idsParam))));
        if (count($ids) > 0) {
            $placeholders = implode(',', array_fill(0, count($ids), '?'));
            $where[] = "id IN ({$placeholders})";
            $params = array_merge($params, $ids);
        }
    }

    $whereClause = count($where) > 0 ? "WHERE " . implode(" AND ", $where) : "";

    $countStmt = $pdo->prepare("SELECT COUNT(*) FROM inventory {$whereClause}");
    $countStmt->execute($params);
    $total = (int)$countStmt->fetchColumn();

    $stmt = $pdo->prepare(
        "SELECT id, name, price, stock_quantity, image_url FROM inventory {$whereClause} ORDER BY id LIMIT ? OFFSET ?"
    );
    $paramIndex = 1;
    foreach ($params as $param) {
        $stmt->bindValue($paramIndex++, $param);
    }
    $stmt->bindValue($paramIndex++, $size, PDO::PARAM_INT);
    $stmt->bindValue($paramIndex++, $offset, PDO::PARAM_INT);
    $stmt->execute();
    $products = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'products' => array_map('toCamelCase', $products),
        'page' => $page,
        'size' => $size,
        'total' => $total,
        'totalPages' => (int)ceil($total / $size),
    ], JSON_UNESCAPED_UNICODE);
}

function toCamelCase(array $row): array
{
    return [
        'id' => (int)$row['id'],
        'name' => $row['name'],
        'price' => (int)$row['price'],
        'stockQuantity' => (int)$row['stock_quantity'],
        'imageUrl' => $row['image_url'],
    ];
}