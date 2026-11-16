<?php
$title='My bookings'; require_once __DIR__.'/includes/functions.php'; requireLogin();
$stmt=db()->prepare("SELECT a.*,s.name service,b.name barber,p.status payment_status FROM appointments a JOIN services s ON s.id=a.service_id JOIN barbers b ON b.id=a.barber_id LEFT JOIN payments p ON p.appointment_id=a.id WHERE a.customer_id=? ORDER BY a.start_at DESC");
$stmt->execute([currentUser()['id']]); $rows=$stmt->fetchAll();
require __DIR__.'/includes/header.php';
?>
<div class="card"><h2>My bookings</h2><div class="table-wrap"><table class="table"><tr><th>Date</th><th>Service</th><th>Barber</th><th>Status</th><th>Payment</th></tr>
<?php foreach($rows as $r): ?><tr><td><?=e(date('d M Y H:i',strtotime($r['start_at'])))?></td><td><?=e($r['service'])?></td><td><?=e($r['barber'])?></td><td><span class="badge <?=e($r['status'])?>"><?=e($r['status'])?></span></td><td><?=e($r['payment_status']??'unpaid')?></td></tr><?php endforeach; ?>
<?php if(!$rows): ?><tr><td colspan="5">No bookings yet.</td></tr><?php endif; ?></table></div></div>
<?php require __DIR__.'/includes/footer.php'; ?>
