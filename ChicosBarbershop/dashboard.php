<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
require_customer();
$pageTitle = 'My appointments';
$activePage = 'dashboard';
$stmt = $pdo->prepare("SELECT a.*, s.name AS service_name, s.price, s.duration, b.first_name AS barber_first, b.last_name AS barber_last
    FROM appointments a JOIN services s ON s.id = a.service_id JOIN barbers b ON b.id = a.barber_id
    WHERE a.user_id = ? ORDER BY a.appointment_date DESC, a.start_time DESC");
$stmt->execute([(int) current_user()['id']]);
$appointments = $stmt->fetchAll();
$upcoming = array_filter($appointments, fn($a) => $a['appointment_date'] >= date('Y-m-d') && !in_array($a['status'], ['Cancelled', 'Completed'], true));
$past = array_filter($appointments, fn($a) => !($a['appointment_date'] >= date('Y-m-d') && !in_array($a['status'], ['Cancelled', 'Completed'], true)));
require 'includes/header.php';
?>
<section class="page-hero dashboard-hero"><div class="container"><span class="eyebrow">YOUR CHICO'S ACCOUNT</span><h1>Good to see you,<br><em><?= e(current_user()['first_name']) ?>.</em></h1><p>Your appointments, all in one place.</p></div></section>
<section class="section section-light"><div class="container dashboard-content"><div class="dashboard-top"><div><span class="eyebrow eyebrow-dark">YOUR SCHEDULE</span><h2>Upcoming appointments</h2></div><a class="btn btn-dark" href="book.php">Book another <span>↗</span></a></div>
<?php if ($upcoming): ?><div class="appointment-list"><?php foreach ($upcoming as $appointment): ?><article class="appointment-card"><div class="appointment-date"><strong><?= date('d', strtotime($appointment['appointment_date'])) ?></strong><span><?= strtoupper(date('M', strtotime($appointment['appointment_date']))) ?></span><small><?= date('D', strtotime($appointment['appointment_date'])) ?></small></div><div class="appointment-details"><h3><?= e($appointment['service_name']) ?></h3><p>With <?= e($appointment['barber_first'] . ' ' . $appointment['barber_last']) ?></p><span><?= date('g:i A', strtotime($appointment['start_time'])) ?> · <?= (int) $appointment['duration'] ?> minutes</span></div><div class="appointment-side"><span class="status status-<?= strtolower($appointment['status']) ?>"><?= e($appointment['status']) ?></span><strong><?= money($appointment['price']) ?></strong><?php if (in_array($appointment['status'], ['Pending', 'Confirmed'], true)): ?><form action="cancel_booking.php" method="post" onsubmit="return confirm('Cancel this appointment?');"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="appointment_id" value="<?= (int) $appointment['id'] ?>"><button class="link-button" type="submit">Cancel booking</button></form><?php endif; ?></div></article><?php endforeach; ?></div><?php else: ?><div class="empty-state"><span class="empty-icon">✦</span><h3>No upcoming appointments yet.</h3><p>Ready for a refresh? Choose your service and book your next visit.</p><a class="btn btn-dark" href="book.php">Book an appointment <span>↗</span></a></div><?php endif; ?>
<div class="history-heading"><div><span class="eyebrow eyebrow-dark">THE ARCHIVE</span><h2>Appointment history</h2></div></div>
<?php if ($past): ?><div class="history-list"><?php foreach ($past as $appointment): ?><div class="history-row"><div><strong><?= e($appointment['service_name']) ?></strong><span><?= date('d M Y', strtotime($appointment['appointment_date'])) ?> · <?= date('g:i A', strtotime($appointment['start_time'])) ?></span></div><span class="status status-<?= strtolower($appointment['status']) ?>"><?= e($appointment['status']) ?></span><strong><?= money($appointment['price']) ?></strong></div><?php endforeach; ?></div><?php else: ?><p class="muted-copy">Your completed and cancelled appointments will appear here.</p><?php endif; ?>
</div></section>
<?php require 'includes/footer.php'; ?>
