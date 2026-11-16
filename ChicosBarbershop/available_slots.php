<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
$barberId = filter_input(INPUT_GET, 'barber_id', FILTER_VALIDATE_INT) ?: 0;
$serviceId = filter_input(INPUT_GET, 'service_id', FILTER_VALIDATE_INT) ?: 0;
$date = trim((string) ($_GET['date'] ?? ''));
echo json_encode(['slots' => available_slots($pdo, $barberId, $serviceId, $date)]);
