<?php
$pageTitle = $pageTitle ?? "Chico's Barbershop";
$activePage = $activePage ?? '';
$user = current_user();
$scriptPath = (string) ($_SERVER['SCRIPT_NAME'] ?? '');
$basePath = (str_contains($scriptPath, '/admin/') || str_contains($scriptPath, '/barber/')) ? '../' : '';
$flash = get_flash();
?>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#070B14">
    <meta name="description" content="Book your next fresh cut at Chico's Barbershop. Professional grooming, skilled barbers and easy online appointments.">
    <title><?= e($pageTitle) ?> | Chico's Barbershop</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Playfair+Display:wght@600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<?= $basePath ?>assets/css/style.css">
</head>
<body>
<header class="site-header">
    <div class="container nav-wrap">
        <a class="brand" href="<?= $basePath ?>index.php" aria-label="Chico's Barbershop home"><span class="brand-mark">C</span><span>CHICO'S<small>BARBERSHOP</small></span></a>
        <button class="nav-toggle" type="button" aria-label="Toggle navigation" aria-expanded="false"><span></span><span></span><span></span></button>
        <nav class="main-nav" aria-label="Main navigation">
            <a class="<?= $activePage === 'home' ? 'active' : '' ?>" href="<?= $basePath ?>index.php">Home</a>
            <a class="<?= $activePage === 'services' ? 'active' : '' ?>" href="<?= $basePath ?>services.php">Services</a>
            <?php if (!$user || (($user['role'] ?? '') === 'customer')): ?><a class="<?= $activePage === 'book' ? 'active' : '' ?>" href="<?= $basePath ?>book.php">Book now</a><?php endif; ?>
            <?php if ($user): ?>
                <?php if (is_admin()): ?><a href="<?= $basePath ?>admin/index.php">Admin</a><?php elseif (is_barber()): ?><a href="<?= $basePath ?>barber/index.php">Barber portal</a><?php else: ?><a class="<?= $activePage === 'dashboard' ? 'active' : '' ?>" href="<?= $basePath ?>dashboard.php">My appointments</a><?php endif; ?>
                <span class="nav-user">Hi, <?= e($user['first_name']) ?></span>
                <a class="nav-login" href="<?= $basePath ?>logout.php">Sign out</a>
            <?php else: ?>
                <a href="<?= $basePath ?>login.php">Sign in</a>
                <a class="nav-cta" href="<?= $basePath ?>register.php">Create account</a>
            <?php endif; ?>
        </nav>
    </div>
</header>
<main>
<?php if ($flash): ?>
    <div class="container flash-wrap"><div class="alert alert-<?= e($flash['type']) ?>" role="status"><?= e($flash['message']) ?></div></div>
<?php endif; ?>
