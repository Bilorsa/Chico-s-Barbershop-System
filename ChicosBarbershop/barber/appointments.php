<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
$barber = require_barber($pdo);
$barberId = (int) $barber['id'];
$pageTitle = 'My appointments';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $appointmentId = filter_input(INPUT_POST, 'appointment_id', FILTER_VALIDATE_INT) ?: 0;
    $newStatus = (string) ($_POST['status'] ?? '');
    $allowed = ['Confirmed', 'Completed', 'Cancelled'];
    if ($appointmentId < 1 || !in_array($newStatus, $allowed, true)) {
        set_flash('error', 'Choose a valid appointment and status.');
    } else {
        $stmt = $pdo->prepare('SELECT status, appointment_date, start_time FROM appointments WHERE id = ? AND barber_id = ? LIMIT 1');
        $stmt->execute([$appointmentId, $barberId]);
        $appointment = $stmt->fetch();
        $isFuture = $appointment && (new DateTime($appointment['appointment_date'] . ' ' . $appointment['start_time'])) > new DateTime();
        $validTransition = $appointment && (
            ($appointment['status'] === 'Pending' && $isFuture && in_array($newStatus, ['Confirmed', 'Cancelled'], true)) ||
            ($appointment['status'] === 'Confirmed' && (($newStatus === 'Completed' && !$isFuture) || ($newStatus === 'Cancelled' && $isFuture)))
        );
        if (!$validTransition) {
            set_flash('error', 'That appointment cannot be updated to the selected status.');
        } else {
            $update = $pdo->prepare('UPDATE appointments SET status = ? WHERE id = ? AND barber_id = ?');
            $update->execute([$newStatus, $appointmentId, $barberId]);
            set_flash('success', 'Appointment updated to ' . $newStatus . '.');
        }
    }
    redirect('appointments.php');
}

$dateFilter = trim((string) ($_GET['date'] ?? ''));
$statusFilter = trim((string) ($_GET['status'] ?? ''));
$where = ['a.barber_id = ?'];
$params = [$barberId];
if ($dateFilter !== '' && valid_date($dateFilter)) { $where[] = 'a.appointment_date = ?'; $params[] = $dateFilter; }
if (in_array($statusFilter, ['Pending', 'Confirmed', 'Completed', 'Cancelled'], true)) { $where[] = 'a.status = ?'; $params[] = $statusFilter; }
$sql = "SELECT a.*, u.first_name, u.last_name, u.email, u.phone, s.name AS service_name, s.duration, s.price
    FROM appointments a JOIN users u ON u.id = a.user_id JOIN services s ON s.id = a.service_id
    WHERE " . implode(' AND ', $where) . " ORDER BY a.appointment_date DESC, a.start_time DESC LIMIT 150";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);
$appointments = $stmt->fetchAll();
require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">BARBER WORKSPACE / SCHEDULE</span><h1>My <em>appointments.</em></h1><p>Review your assigned bookings, contact details and appointment status.</p></div></section>
<section class="section section-light"><div class="container admin-content">
    <div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">PERSONAL SCHEDULE</span><h2><?= e($barber['first_name']) ?>'s bookings</h2></div><a class="back-link" href="index.php">← Barber dashboard</a></div>
    <form method="get" class="barber-filter-bar"><div class="field"><label for="date">Appointment date</label><input type="date" id="date" name="date" value="<?= e($dateFilter) ?>"></div><div class="field"><label for="status">Status</label><select id="status" name="status"><option value="">All statuses</option><?php foreach (['Pending','Confirmed','Completed','Cancelled'] as $status): ?><option value="<?= e($status) ?>" <?= $statusFilter === $status ? 'selected' : '' ?>><?= e($status) ?></option><?php endforeach; ?></select></div><button class="btn btn-dark" type="submit">Filter appointments</button><a class="back-link" href="appointments.php">Clear</a></form>
    <div class="table-card"><div class="table-scroll"><table><thead><tr><th>Customer</th><th>Service</th><th>Date & time</th><th>Price</th><th>Status</th><th>Update</th></tr></thead><tbody>
    <?php foreach ($appointments as $a): ?><tr><td><strong><?= e($a['first_name'] . ' ' . $a['last_name']) ?></strong><small class="table-sub"><?= e($a['phone']) ?> · <?= e($a['email']) ?></small></td><td><?= e($a['service_name']) ?><small class="table-sub"><?= (int) $a['duration'] ?> minutes</small></td><td><?= date('d M Y', strtotime($a['appointment_date'])) ?><small class="table-sub"><?= date('g:i A', strtotime($a['start_time'])) ?>–<?= date('g:i A', strtotime($a['end_time'])) ?></small></td><td><?= money($a['price']) ?></td><td><span class="status status-<?= strtolower($a['status']) ?>"><?= e($a['status']) ?></span></td><td>
        <?php if ($a['status'] === 'Pending' || $a['status'] === 'Confirmed'): ?><form method="post" class="barber-status-form"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="appointment_id" value="<?= (int) $a['id'] ?>"><select name="status" aria-label="Update appointment <?= (int) $a['id'] ?>"><?php if ($a['status'] === 'Pending'): ?><option value="Confirmed">Confirm</option><option value="Cancelled">Cancel</option><?php else: ?><option value="Completed">Mark completed</option><option value="Cancelled">Cancel</option><?php endif; ?></select><button class="mini-btn" type="submit">Save</button></form><?php else: ?><span class="table-sub">No action available</span><?php endif; ?>
    </td></tr><?php endforeach; ?>
    <?php if (!$appointments): ?><tr><td colspan="6" class="table-empty">No appointments match these filters.</td></tr><?php endif; ?>
    </tbody></table></div></div>
    <p class="barber-permission-note">You can only view and update appointments assigned to your barber profile. Contact the administrator if a booking is assigned incorrectly.</p>
</div></section>
<?php require '../includes/footer.php'; ?>
