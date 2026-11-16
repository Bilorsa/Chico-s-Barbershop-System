<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
require_customer();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') redirect('book.php');
verify_csrf();
$serviceId = filter_input(INPUT_POST, 'service_id', FILTER_VALIDATE_INT) ?: 0;
$barberId = filter_input(INPUT_POST, 'barber_id', FILTER_VALIDATE_INT) ?: 0;
$date = trim((string) ($_POST['appointment_date'] ?? ''));
$time = trim((string) ($_POST['start_time'] ?? ''));
$note = trim((string) ($_POST['customer_note'] ?? ''));

if ($serviceId < 1 || $barberId < 1 || !valid_date($date) || !preg_match('/^(?:[01]\d|2[0-3]):[0-5]\d$/', $time) || strlen($note) > 500) {
    set_flash('error', 'Please complete the appointment form with valid details.');
    redirect('book.php');
}

try {
    $pdo->beginTransaction();
    // Lock the barber row so two simultaneous requests cannot reserve overlapping times.
    $lock = $pdo->prepare('SELECT id FROM barbers WHERE id = ? AND is_active = 1 FOR UPDATE');
    $lock->execute([$barberId]);
    if (!$lock->fetch()) throw new RuntimeException('That barber is no longer available. Please choose another barber.');

    $serviceStmt = $pdo->prepare('SELECT id, duration FROM services WHERE id = ? AND is_active = 1');
    $serviceStmt->execute([$serviceId]);
    $service = $serviceStmt->fetch();
    if (!$service) throw new RuntimeException('That service is no longer available. Please choose another service.');

    $slots = available_slots($pdo, $barberId, $serviceId, $date);
    if (!in_array($time, $slots, true)) throw new RuntimeException('That time is no longer available. Please choose another slot.');

    $start = new DateTime($date . ' ' . $time);
    $end = (clone $start)->modify('+' . (int) $service['duration'] . ' minutes');
    $insert = $pdo->prepare("INSERT INTO appointments (user_id, barber_id, service_id, appointment_date, start_time, end_time, status, customer_note) VALUES (?, ?, ?, ?, ?, ?, 'Pending', ?)");
    $insert->execute([(int) current_user()['id'], $barberId, $serviceId, $date, $start->format('H:i:s'), $end->format('H:i:s'), $note !== '' ? $note : null]);
    $pdo->commit();
    set_flash('success', 'Your appointment request has been received. The shop will confirm it shortly.');
    redirect('dashboard.php');
} catch (Throwable $exception) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    if ($exception instanceof RuntimeException) $message = $exception->getMessage();
    else {
        error_log('Booking error: ' . $exception->getMessage());
        $message = 'We could not complete your booking. Please try again.';
    }
    set_flash('error', $message);
    redirect('book.php');
}
