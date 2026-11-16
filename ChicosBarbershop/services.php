<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
$pageTitle = 'Our grooming services';
$activePage = 'services';
$services = $pdo->query('SELECT id, name, description, price, duration FROM services WHERE is_active = 1 ORDER BY price ASC')->fetchAll();
require 'includes/header.php';
?>
<section class="page-hero"><div class="container"><span class="eyebrow">THE CHICO'S MENU</span><h1>Find your <em>finish.</em></h1><p>From a clean classic to a detailed fade, choose the service that fits your style.</p></div></section>
<section class="section section-light"><div class="container"><div class="service-grid service-grid-all">
<?php foreach ($services as $index => $service): ?><article class="service-card"><span class="service-number"><?= str_pad((string) ($index + 1), 2, '0', STR_PAD_LEFT) ?></span><div class="service-icon" aria-hidden="true">✦</div><h3><?= e($service['name']) ?></h3><p><?= e($service['description']) ?></p><div class="service-meta"><strong><?= money($service['price']) ?></strong><span><?= (int) $service['duration'] ?> MIN</span></div><a class="service-arrow" href="book.php?service=<?= (int) $service['id'] ?>" aria-label="Book <?= e($service['name']) ?>">↗</a></article><?php endforeach; ?>
</div><?php if (!$services): ?><div class="empty-state"><h3>Services are being updated.</h3><p>Please check back shortly.</p></div><?php endif; ?></div></section>
<section class="booking-banner"><div class="container booking-banner-inner"><div><span class="eyebrow">READY WHEN YOU ARE</span><h2>Pick a service.<br><em>We'll handle the rest.</em></h2></div><a class="btn btn-gold" href="book.php">Book now <span>↗</span></a></div></section>
<?php require 'includes/footer.php'; ?>
