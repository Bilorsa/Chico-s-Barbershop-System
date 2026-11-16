<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
require_admin();
$pageTitle = 'Admin dashboard';
$stats = [
    'today' => (int) $pdo->query("SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE() AND status <> 'Cancelled'")->fetchColumn(),
    'pending' => (int) $pdo->query("SELECT COUNT(*) FROM appointments WHERE status = 'Pending'")->fetchColumn(),
    'customers' => (int) $pdo->query("SELECT COUNT(*) FROM users WHERE role = 'customer'")->fetchColumn(),
    'services' => (int) $pdo->query('SELECT COUNT(*) FROM services WHERE is_active = 1')->fetchColumn(),
];
$recent = $pdo->query("SELECT a.id, a.appointment_date, a.start_time, a.status, u.first_name, u.last_name, s.name AS service_name, b.first_name AS barber_name
    FROM appointments a JOIN users u ON u.id=a.user_id JOIN services s ON s.id=a.service_id JOIN barbers b ON b.id=a.barber_id
    ORDER BY a.created_at DESC LIMIT 8")->fetchAll();
require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">CHICO'S / MANAGEMENT</span><h1>Shop <em>overview.</em></h1><p>Welcome back. Here's what's happening at the shop.</p></div></section>
<section class="section section-light"><div class="container admin-content"><div class="admin-stats"><div class="admin-stat"><span>Today's bookings</span><strong><?= $stats['today'] ?></strong><small>Appointments scheduled today</small></div><div class="admin-stat"><span>Pending requests</span><strong><?= $stats['pending'] ?></strong><small>Need your attention</small></div><div class="admin-stat"><span>Customers</span><strong><?= $stats['customers'] ?></strong><small>Registered accounts</small></div><div class="admin-stat"><span>Active services</span><strong><?= $stats['services'] ?></strong><small>Available to book</small></div></div>
<div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">QUICK ACCESS</span><h2>Manage your shop</h2></div><div class="admin-links"><a href="appointments.php">Appointments ↗</a><a href="services.php">Services ↗</a><a href="barbers.php">Barbers ↗</a><a href="customers.php">Customers ↗</a></div></div>
<div class="table-card"><div class="table-heading"><h3>Recent appointment activity</h3><a href="appointments.php">View all →</a></div><div class="table-scroll"><table><thead><tr><th>Customer</th><th>Service</th><th>Barber</th><th>Date & time</th><th>Status</th></tr></thead><tbody><?php foreach ($recent as $row): ?><tr><td><?= e($row['first_name'] . ' ' . $row['last_name']) ?></td><td><?= e($row['service_name']) ?></td><td><?= e($row['barber_name']) ?></td><td><?= date('d M Y', strtotime($row['appointment_date'])) ?> · <?= date('g:i A', strtotime($row['start_time'])) ?></td><td><span class="status status-<?= strtolower($row['status']) ?>"><?= e($row['status']) ?></span></td></tr><?php endforeach; ?><?php if (!$recent): ?><tr><td colspan="5" class="table-empty">No appointments have been made yet.</td></tr><?php endif; ?></tbody></table></div></div></div></section>
<?php require '../includes/footer.php'; ?>
