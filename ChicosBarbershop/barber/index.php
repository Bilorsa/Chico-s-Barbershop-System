<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
$barber = require_barber($pdo);
$pageTitle = 'Barber dashboard';
$barberId = (int) $barber['id'];

$statsStmt = $pdo->prepare("SELECT
    SUM(appointment_date = CURDATE() AND status <> 'Cancelled') AS today_count,
    SUM(status = 'Pending' AND appointment_date >= CURDATE()) AS pending_count,
    SUM(appointment_date >= CURDATE() AND status IN ('Pending','Confirmed')) AS upcoming_count
    FROM appointments WHERE barber_id = ?");
$statsStmt->execute([$barberId]);
$stats = $statsStmt->fetch() ?: [];

$todayStmt = $pdo->prepare("SELECT a.id, a.appointment_date, a.start_time, a.end_time, a.status, a.customer_note,
    u.first_name, u.last_name, u.phone, s.name AS service_name, s.duration, s.price
    FROM appointments a JOIN users u ON u.id = a.user_id JOIN services s ON s.id = a.service_id
    WHERE a.barber_id = ? AND a.appointment_date = CURDATE() AND a.status <> 'Cancelled'
    ORDER BY a.start_time ASC LIMIT 8");
$todayStmt->execute([$barberId]);
$todayAppointments = $todayStmt->fetchAll();

$nextStmt = $pdo->prepare("SELECT a.id, a.appointment_date, a.start_time, a.status, u.first_name, u.last_name, s.name AS service_name
    FROM appointments a JOIN users u ON u.id = a.user_id JOIN services s ON s.id = a.service_id
    WHERE a.barber_id = ? AND a.appointment_date > CURDATE() AND a.status IN ('Pending','Confirmed')
    ORDER BY a.appointment_date, a.start_time LIMIT 5");
$nextStmt->execute([$barberId]);
$nextAppointments = $nextStmt->fetchAll();

require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">CHICO'S / BARBER WORKSPACE</span><h1>Your chair. <em>Your schedule.</em></h1><p>Welcome, <?= e($barber['first_name']) ?>. Here's your appointment activity and what is coming up.</p></div></section>
<section class="section section-light"><div class="container admin-content">
    <div class="barber-welcome-card"><div><span class="eyebrow eyebrow-dark">YOUR BARBER PROFILE</span><h2><?= e($barber['first_name'] . ' ' . $barber['last_name']) ?></h2><p><?= e($barber['specialty']) ?> · <?= (int) $barber['experience_years'] ?> years' experience</p></div><span class="barber-profile-pill">Active barber</span></div>
    <div class="admin-stats barber-stats">
        <div class="admin-stat"><span>Today's appointments</span><strong><?= (int) ($stats['today_count'] ?? 0) ?></strong><small>Bookings assigned to you today</small></div>
        <div class="admin-stat"><span>Pending requests</span><strong><?= (int) ($stats['pending_count'] ?? 0) ?></strong><small>Awaiting your confirmation</small></div>
        <div class="admin-stat"><span>Upcoming bookings</span><strong><?= (int) ($stats['upcoming_count'] ?? 0) ?></strong><small>Future active appointments</small></div>
    </div>
    <div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">YOUR WORKDAY</span><h2>Today's schedule</h2></div><div class="admin-links"><a href="appointments.php">Full appointment list ↗</a></div></div>
    <div class="barber-appointment-list">
        <?php foreach ($todayAppointments as $a): ?>
            <article class="barber-appointment-row"><div class="barber-time"><strong><?= date('g:i', strtotime($a['start_time'])) ?></strong><span><?= date('A', strtotime($a['start_time'])) ?></span></div><div class="barber-appointment-main"><h3><?= e($a['first_name'] . ' ' . $a['last_name']) ?></h3><p><?= e($a['service_name']) ?> · <?= (int) $a['duration'] ?> min</p><small><?= e($a['phone']) ?><?php if ($a['customer_note']): ?> · Note: <?= e($a['customer_note']) ?><?php endif; ?></small></div><div class="barber-appointment-meta"><strong><?= money($a['price']) ?></strong><span class="status status-<?= strtolower($a['status']) ?>"><?= e($a['status']) ?></span></div></article>
        <?php endforeach; ?>
        <?php if (!$todayAppointments): ?><div class="empty-state"><span class="empty-icon">✦</span><h3>No appointments today</h3><p>Enjoy the breathing room. New bookings assigned to you will appear here.</p></div><?php endif; ?>
    </div>
    <div class="table-card barber-upcoming-card"><div class="table-heading"><h3>Coming up next</h3><a href="appointments.php">Manage schedule →</a></div><div class="table-scroll"><table><thead><tr><th>Customer</th><th>Service</th><th>Date</th><th>Time</th><th>Status</th></tr></thead><tbody>
        <?php foreach ($nextAppointments as $a): ?><tr><td><strong><?= e($a['first_name'] . ' ' . $a['last_name']) ?></strong></td><td><?= e($a['service_name']) ?></td><td><?= date('d M Y', strtotime($a['appointment_date'])) ?></td><td><?= date('g:i A', strtotime($a['start_time'])) ?></td><td><span class="status status-<?= strtolower($a['status']) ?>"><?= e($a['status']) ?></span></td></tr><?php endforeach; ?>
        <?php if (!$nextAppointments): ?><tr><td colspan="5" class="table-empty">No future appointments are currently assigned to you.</td></tr><?php endif; ?>
    </tbody></table></div></div>
</div></section>
<?php require '../includes/footer.php'; ?>
