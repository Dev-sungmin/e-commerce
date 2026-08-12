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

    $result = toCamelCase($product);
    $summary = fetchReviewSummary($id);
    $result['averageRating'] = $summary['averageRating'];
    $result['reviewCount'] = $summary['reviewCount'];

    echo json_encode($result, JSON_UNESCAPED_UNICODE);
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

    $productList = array_map('toCamelCase', $products);
    $productIds = array_column($productList, 'id');
    $summaries = fetchReviewSummaries($productIds);

    foreach ($productList as &$product) {
        $summary = $summaries[$product['id']] ?? ['averageRating' => 0, 'reviewCount' => 0];
        $product['averageRating'] = $summary['averageRating'];
        $product['reviewCount'] = $summary['reviewCount'];
    }
    unset($product);

    echo json_encode([
        'products' => $productList,
        'page' => $page,
        'size' => $size,
        'total' => $total,
        'totalPages' => (int)ceil($total / $size),
    ], JSON_UNESCAPED_UNICODE);
}

function fetchReviewSummary(int $productId): array
{
    $url = getReviewServiceBaseUrl() . "/api/reviews/summary?productId={$productId}";
    $response = callReviewService($url);

    if ($response === null) {
        return ['averageRating' => 0, 'reviewCount' => 0];
    }
    return $response;
}

function fetchReviewSummaries(array $productIds): array
{
    if (empty($productIds)) {
        return [];
    }

    $idsParam = implode(',', $productIds);
    $url = getReviewServiceBaseUrl() . "/api/reviews/summary/batch?productIds={$idsParam}";
    $response = callReviewService($url);

    if ($response === null) {
        return [];
    }

    $summaries = [];
    foreach ($response as $item) {
        $summaries[$item['productId']] = [
            'averageRating' => $item['averageRating'],
            'reviewCount' => $item['reviewCount'],
        ];
    }
    return $summaries;
}

function callReviewService(string $url): ?array
{
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 1);
    curl_setopt($ch, CURLOPT_TIMEOUT, 2);

    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    curl_close($ch);

    if ($error || $httpCode !== 200) {
        error_log("Review Service 호출 실패: {$url}, error={$error}, httpCode={$httpCode}");
        return null;
    }

    return json_decode($response, true);
}

function getReviewServiceBaseUrl(): string
{
    return getenv('REVIEW_SERVICE_BASE_URL') ?: 'http://review-service:8084';
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