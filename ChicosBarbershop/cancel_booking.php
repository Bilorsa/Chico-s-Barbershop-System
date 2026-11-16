<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
require_customer();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') redirect('dashboard.php');
verify_csrf();
$id = filter_input(INPUT_POST, 'appointment_id', FILTER_VALIDATE_INT) ?: 0;
$stmt = $pdo->prepare("UPDATE appointments SET status = 'Cancelled' WHERE id = ? AND user_id = ? AND status IN ('Pending','Confirmed') AND TIMESTAMP(appointment_date, start_time) > NOW()");
$stmt->execute([$id, (int) current_user()['id']]);
if ($stmt->rowCount()) set_flash('success', 'Your appointment has been cancelled.');
else set_flash('error', 'This appointment could not be cancelled. It may already have started or been updated.');
redirect('dashboard.php');
