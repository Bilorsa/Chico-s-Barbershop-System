<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
require_admin();
$pageTitle = 'Manage appointments';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $id = filter_input(INPUT_POST, 'appointment_id', FILTER_VALIDATE_INT) ?: 0;
    $status = (string) ($_POST['status'] ?? '');
    if ($id > 0 && in_array($status, ['Pending', 'Confirmed', 'Completed', 'Cancelled'], true)) {
        $stmt = $pdo->prepare('UPDATE appointments SET status = ? WHERE id = ?');
        $stmt->execute([$status, $id]);
        set_flash('success', 'Appointment status updated.');
    } else set_flash('error', 'Please choose a valid appointment status.');
    redirect('appointments.php');
}
$appointments = $pdo->query("SELECT a.*, u.first_name, u.last_name, u.email, u.phone, s.name AS service_name, s.price, b.first_name AS barber_first, b.last_name AS barber_last
    FROM appointments a JOIN users u ON u.id=a.user_id JOIN services s ON s.id=a.service_id JOIN barbers b ON b.id=a.barber_id
    ORDER BY a.appointment_date DESC, a.start_time DESC")->fetchAll();
require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">MANAGEMENT / SCHEDULE</span><h1>Appointments <em>desk.</em></h1><p>Review booking requests and keep the shop schedule up to date.</p></div></section>
<section class="section section-light"><div class="container admin-content"><div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">BOOKING MANAGEMENT</span><h2>All appointments</h2></div><a class="back-link" href="index.php">← Dashboard</a></div><div class="table-card"><div class="table-scroll"><table><thead><tr><th>Customer</th><th>Service / barber</th><th>Appointment</th><th>Price</th><th>Status</th><th>Update</th></tr></thead><tbody>
<?php foreach ($appointments as $a): ?><tr><td><strong><?= e($a['first_name'] . ' ' . $a['last_name']) ?></strong><small class="table-sub"><?= e($a['email']) ?> · <?= e($a['phone']) ?></small></td><td><?= e($a['service_name']) ?><small class="table-sub">with <?= e($a['barber_first'] . ' ' . $a['barber_last']) ?></small></td><td><?= date('d M Y', strtotime($a['appointment_date'])) ?><small class="table-sub"><?= date('g:i A', strtotime($a['start_time'])) ?>–<?= date('g:i A', strtotime($a['end_time'])) ?></small></td><td><?= money($a['price']) ?></td><td><span class="status status-<?= strtolower($a['status']) ?>"><?= e($a['status']) ?></span></td><td><form method="post" class="inline-status"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="appointment_id" value="<?= (int) $a['id'] ?>"><select name="status" aria-label="Update status for appointment <?= (int) $a['id'] ?>"><option <?= $a['status']==='Pending'?'selected':'' ?>>Pending</option><option <?= $a['status']==='Confirmed'?'selected':'' ?>>Confirmed</option><option <?= $a['status']==='Completed'?'selected':'' ?>>Completed</option><option <?= $a['status']==='Cancelled'?'selected':'' ?>>Cancelled</option></select><button class="mini-btn" type="submit">Save</button></form></td></tr><?php endforeach; ?><?php if (!$appointments): ?><tr><td colspan="6" class="table-empty">No appointments to display.</td></tr><?php endif; ?></tbody></table></div></div></div></section>
<?php require '../includes/footer.php'; ?>
