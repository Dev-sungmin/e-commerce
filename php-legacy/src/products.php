<?php
// GET /products, GET /products/{id}
require_once __DIR__ . '/db.php';

header('Content-Type: application/json');

// TODO: 쿼리 파라미터로 단건/목록 구분
// GET /products       -> 전체 목록 조회
// GET /products/{id}  -> 단건 조회, 없으면 404 { "error": "PRODUCT_NOT_FOUND" }

echo json_encode(['message' => 'TODO: implement products endpoint']);