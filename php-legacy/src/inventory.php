<?php
// POST /inventory/deduct, POST /inventory/restore
require_once __DIR__ . '/db.php';

header('Content-Type: application/json');

$input = json_decode(file_get_contents('php://input'), true);
$orderId = $input['order_id'] ?? null;
$productId = $input['product_id'] ?? null;
$quantity = $input['quantity'] ?? null;

if (!$orderId || !$productId || !$quantity) {
    http_response_code(400);
    echo json_encode(['error' => 'INVALID_REQUEST']);
    exit;
}

// TODO: deduct
//   UPDATE inventory SET stock_quantity = stock_quantity - ?
//   WHERE id = ? AND stock_quantity >= ?
//   -> affected rows 0이면 409 INSUFFICIENT_STOCK
//   -> 성공 시 inventory_log에 { product_id, order_id, change_amount: -quantity, reason: 'DEDUCT' } 기록

// TODO: restore (멱등성 체크 포함)
//   inventory_log에서 order_id + reason='RESTORE' 존재 여부 먼저 확인
//   없으면 stock_quantity 원복 + inventory_log 기록

echo json_encode(['message' => 'TODO: implement inventory deduct/restore']);