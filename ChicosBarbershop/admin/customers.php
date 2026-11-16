<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
require_admin();
$pageTitle = 'Customers';
$customers = $pdo->query("SELECT u.id,u.first_name,u.last_name,u.email,u.phone,u.created_at,COUNT(a.id) AS booking_count FROM users u LEFT JOIN appointments a ON a.user_id=u.id WHERE u.role='customer' GROUP BY u.id ORDER BY u.created_at DESC")->fetchAll();
require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">MANAGEMENT / CLIENTS</span><h1>People who <em>keep us sharp.</em></h1><p>Your customer directory and appointment activity.</p></div></section>
<section class="section section-light"><div class="container admin-content"><div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">CUSTOMER DIRECTORY</span><h2>Registered customers</h2></div><a class="back-link" href="index.php">← Dashboard</a></div><div class="table-card"><div class="table-scroll"><table><thead><tr><th>Customer</th><th>Email</th><th>Phone</th><th>Appointments</th><th>Joined</th></tr></thead><tbody><?php foreach ($customers as $c): ?><tr><td><strong><?= e($c['first_name'] . ' ' . $c['last_name']) ?></strong></td><td><?= e($c['email']) ?></td><td><?= e($c['phone']) ?></td><td><?= (int) $c['booking_count'] ?></td><td><?= date('d M Y', strtotime($c['created_at'])) ?></td></tr><?php endforeach; ?><?php if (!$customers): ?><tr><td colspan="5" class="table-empty">No customers have registered yet.</td></tr><?php endif; ?></tbody></table></div></div></div></section>
<?php require '../includes/footer.php'; ?>
