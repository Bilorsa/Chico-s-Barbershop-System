<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
$pageTitle = 'A sharper kind of barbershop';
$activePage = 'home';
$serviceCount = (int) $pdo->query('SELECT COUNT(*) FROM services WHERE is_active = 1')->fetchColumn();
$barberCount = (int) $pdo->query('SELECT COUNT(*) FROM barbers WHERE is_active = 1')->fetchColumn();
$services = $pdo->query('SELECT id, name, description, price, duration FROM services WHERE is_active = 1 ORDER BY id LIMIT 3')->fetchAll();
require 'includes/header.php';
?>
<section class="hero">
    <div class="hero-shade"></div>
    <div class="container hero-content">
        <span class="eyebrow"><span class="eyebrow-line"></span> THE ART OF A GOOD CUT</span>
        <h1>Look sharp.<br><em>Feel sharper.</em></h1>
        <p>More than a haircut. It's your time in the chair, a little reset, and a look that feels like you.</p>
        <div class="hero-actions"><a class="btn btn-gold" href="book.php">Book your appointment <span>↗</span></a><a class="text-link" href="services.php">Explore our services <span>→</span></a></div>
        <div class="hero-note"><span class="status-dot"></span> WALK IN WITH A PLAN. WALK OUT WITH CONFIDENCE.</div>
    </div>
    <div class="hero-side-label">CHICO'S / JOHANNESBURG</div>
</section>
<section class="intro-strip"><div class="container intro-inner"><div><span class="eyebrow eyebrow-dark">YOUR CHAIR IS WAITING</span><h2>Good grooming is<br><em>never an accident.</em></h2></div><p>From the first consultation to the final line-up, our barbers focus on the details that make the difference. Find your style, choose your barber and book a time that works for you.</p><a class="round-arrow" href="book.php" aria-label="Book an appointment">↗</a></div></section>
<section class="section section-light"><div class="container"><div class="section-heading"><div><span class="eyebrow eyebrow-dark">THE MENU</span><h2>Made for your <em>best look.</em></h2></div><a class="under-link" href="services.php">View all services <span>→</span></a></div>
    <div class="service-grid home-services">
        <?php foreach ($services as $index => $service): ?>
            <article class="service-card"><span class="service-number">0<?= $index + 1 ?></span><h3><?= e($service['name']) ?></h3><p><?= e($service['description']) ?></p><div class="service-meta"><strong><?= money($service['price']) ?></strong><span><?= (int) $service['duration'] ?> MIN</span></div><a href="book.php?service=<?= (int) $service['id'] ?>" class="service-arrow" aria-label="Book <?= e($service['name']) ?>">↗</a></article>
        <?php endforeach; ?>
    </div>
</div></section>
<section class="stats-band"><div class="container stats-grid"><div><strong><?= $serviceCount ?>+</strong><span>GROOMING SERVICES</span></div><div><strong><?= $barberCount ?></strong><span>SKILLED BARBERS</span></div><div><strong>6</strong><span>DAYS A WEEK</span></div><div class="stats-quote">Your style.<br><em>Your statement.</em></div></div></section>
<section class="booking-banner"><div class="container booking-banner-inner"><div><span class="eyebrow">MAKE TIME FOR YOURSELF</span><h2>Next-level fresh<br><em>starts here.</em></h2></div><a class="btn btn-gold" href="book.php">Find your time <span>↗</span></a></div></section>
<?php require 'includes/footer.php'; ?>
