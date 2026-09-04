<?php
$title = 'Premium grooming, made simple';
require_once __DIR__ . '/includes/functions.php';

$pdo = db();
$services = $pdo->query("SELECT id, name, duration_minutes, price FROM services WHERE active=1 ORDER BY price ASC")->fetchAll();
$barbers = $pdo->query("SELECT id, name, email FROM barbers WHERE active=1 ORDER BY name LIMIT 6")->fetchAll();

require __DIR__ . '/includes/header.php';
?>

<section class="landing-hero">
    <div class="hero-copy">
        <span class="eyebrow">SHARPCUT BARBERSHOP</span>
        <h1>Sharp style.<br><span>Zero waiting.</span></h1>
        <p>Premium cuts, fades and beard grooming from experienced barbers. Choose your service, pick your barber and book in seconds.</p>
        <div class="hero-actions">
            <a class="btn btn-large" href="book.php">Book Now <span>→</span></a>
            <a class="hero-link" href="#services">Explore services <span>↓</span></a>
        </div>
        <div class="hero-trust">
            <div><strong>4.9/5</strong><span>client rating</span></div>
            <div><strong>1,500+</strong><span>cuts completed</span></div>
            <div><strong>3</strong><span>master barbers</span></div>
        </div>
    </div>
    <div class="hero-visual">
        <div class="hero-glow"></div>
        <div class="logo-showcase">
            <div class="logo-frame">
                <img src="assets/images/chico-logo.png" alt="Chico's Barber Shop logo">
            </div>
            <div class="logo-caption">
                <span>CHICO'S BARBER SHOP</span>
                <strong>EST. 2020</strong>
            </div>
        </div>
    </div>
</section>

<section class="section" id="services">
    <div class="section-heading">
        <div><span class="eyebrow">WHAT WE DO</span><h2>Built for a <span>better cut.</span></h2></div>
        <p>Simple services. Premium execution. Every appointment is tailored to your look.</p>
    </div>
    <div class="service-grid">
        <?php
        $serviceImages = [
            'Classic Cut' => 'assets/images/classic-cut.webp',
            'Skin Fade' => 'assets/images/skin-fade.webp',
            'Beard Trim' => 'assets/images/beard-x-haircut.webp',
            'Cut + Beard' => 'assets/images/beard-x-haircut.webp',
        ];
        foreach ($services as $i => $service):
            $serviceImage = $serviceImages[$service['name']] ?? 'assets/images/classic-cut.webp';
        ?>
        <article class="service-card service-card-image">
            <div class="service-image-wrap">
                <img src="<?= e($serviceImage) ?>" alt="<?= e($service['name']) ?>">
                <div class="service-image-overlay"></div>
                <div class="service-number">0<?= $i + 1 ?></div>
            </div>
            <div class="service-card-content">
                <div class="service-icon"><?= $i % 2 === 0 ? '✂' : '◒' ?></div>
                <h3><?= e($service['name']) ?></h3>
                <p><?= $service['duration_minutes'] ?> minutes of focused grooming.</p>
                <div class="service-meta"><strong>R<?= number_format((float)$service['price'], 2) ?></strong><span><?= $service['duration_minutes'] ?> min</span></div>
            </div>
        </article>
        <?php endforeach; ?>
    </div>
</section>

<section class="split-section">
    <div class="split-copy">
        <span class="eyebrow">THE SHARPCUT STANDARD</span>
        <h2>Your barber.<br><span>Your style.</span></h2>
        <p>We combine classic barbering technique with modern styling. From a clean skin fade to a detailed beard finish, every appointment is about precision.</p>
        <a class="text-arrow" href="book.php">Find your appointment <span>→</span></a>
    </div>
    <div class="standard-grid">
        <div class="standard-item"><span>01</span><strong>Precision</strong><p>Clean lines and considered details.</p></div>
        <div class="standard-item"><span>02</span><strong>Consistency</strong><p>The same premium experience every visit.</p></div>
        <div class="standard-item"><span>03</span><strong>Confidence</strong><p>Leave looking and feeling sharper.</p></div>
        <div class="standard-item"><span>04</span><strong>Convenience</strong><p>Book online around your schedule.</p></div>
    </div>
</section>

<section class="section" id="barbers">
    <div class="section-heading">
        <div><span class="eyebrow">MEET THE TEAM</span><h2>The hands behind<br><span>the chair.</span></h2></div>
        <p>Our barbers bring technique, attention to detail and their own signature style.</p>
    </div>
    <div class="barber-grid">
        <?php
        $barberImages = [
            'assets/images/barber-chico.webp',
            'assets/images/barber-joe.webp',
            'assets/images/barber-mike.webp',
            'assets/images/barber-troy.webp',
        ];
        foreach ($barbers as $i => $barber):
            $barberImage = $barberImages[$i % count($barberImages)];
        ?>
        <article class="barber-card">
            <div class="barber-photo-wrap">
                <img class="barber-photo" src="<?= e($barberImage) ?>" alt="<?= e($barber['name']) ?>, barber">
                <div class="barber-photo-overlay"></div>
            </div>
            <div class="barber-info">
                <span>MASTER BARBER</span>
                <h3><?= e($barber['name']) ?></h3>
                <p><?= $i % 3 === 0 ? 'Fades · Classic cuts' : ($i % 3 === 1 ? 'Modern styles · Beard work' : 'Precision cuts · Styling') ?></p>
                <?php if ($barber['email']): ?><small><?= e($barber['email']) ?></small><?php endif; ?>
            </div>
            <a href="book.php" class="round-arrow" aria-label="Book with barber">↗</a>
        </article>
        <?php endforeach; ?>
    </div>
</section>

<section class="testimonial-section">
    <div class="eyebrow">CLIENT LOVE</div>
    <div class="testimonial-mark">“</div>
    <blockquote>Best cut I've had in a long time. The booking was effortless, the barber understood exactly what I wanted, and the finish was on point.</blockquote>
    <div class="testimonial-author"><strong>Daniel M.</strong><span>Regular client · 5 visits</span></div>
</section>

<section class="booking-cta" id="booking">
    <div>
        <span class="eyebrow">READY WHEN YOU ARE</span>
        <h2>Your next great cut<br><span>starts here.</span></h2>
        <p>Pick a service, choose your barber and lock in your time.</p>
    </div>
    <a class="btn btn-large" href="book.php">Book Your Appointment <span>→</span></a>
</section>

<?php require __DIR__ . '/includes/footer.php'; ?>
