<?php
$title='Admin dashboard'; require_once __DIR__.'/../includes/functions.php'; requireAdmin();
$pdo=db();
$total=(int)$pdo->query("SELECT COUNT(*) FROM appointments")->fetchColumn();
$today=(int)$pdo->query("SELECT COUNT(*) FROM appointments WHERE DATE(start_at)=CURDATE() AND status<>'cancelled'")->fetchColumn();
$customers=(int)$pdo->query("SELECT COUNT(*) FROM customers WHERE role='customer'")->fetchColumn();
$revenue=(float)$pdo->query("SELECT COALESCE(SUM(amount),0) FROM payments WHERE status='paid'")->fetchColumn();
require __DIR__.'/../includes/header.php';
?>
<div class="stats"><div class="stat">Appointments<strong><?=$total?></strong></div><div class="stat">Today<strong><?=$today?></strong></div><div class="stat">Customers<strong><?=$customers?></strong></div><div class="stat">Paid revenue<strong>R<?=number_format($revenue,2)?></strong></div></div>
<br><div class="card"><div class="actions"><h2>Appointment management</h2><a class="btn" href="appointments.php">Manage appointments</a><a class="btn secondary" href="services.php">Services</a><a class="btn secondary" href="barbers.php">Barbers</a></div></div>
<?php require __DIR__.'/../includes/footer.php'; ?>
