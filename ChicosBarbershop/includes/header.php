<?php
require_once __DIR__ . '/functions.php';
$user = currentUser();
?>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><?= e($title ?? APP_NAME) ?></title>
    <link rel="stylesheet" href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../assets/css/style.css' : 'assets/css/style.css' ?>">
</head>
<body>
<header class="site-header">
    <div class="navbar">
        <a class="brand" href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../index.php' : 'index.php' ?>">
            <img class="brand-mark brand-logo" src="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../assets/images/chico-logo.png' : 'assets/images/chico-logo.png' ?>" alt="Chico's Barber Shop">
            <span><?= e(APP_NAME) ?></span>
        </a>
        <button class="nav-toggle" type="button" aria-label="Toggle navigation" onclick="document.querySelector('.nav-links').classList.toggle('open')">☰</button>
        <nav class="nav-links">
            <a href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../index.php' : 'index.php' ?>">Book</a>
            <?php if ($user): ?><a href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../my_bookings.php' : 'my_bookings.php' ?>">My bookings</a><?php endif; ?>
            <?php if (($user['role'] ?? '') === 'admin'): ?><a href="index.php">Admin</a><?php endif; ?>
            <?php if ($user): ?>
                <a href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../logout.php' : 'logout.php' ?>">Logout</a>
            <?php else: ?>
                <a href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../login.php' : 'login.php' ?>">Login</a>
                <a class="nav-cta" href="<?= str_contains($_SERVER['PHP_SELF'], '/admin/') ? '../register.php' : 'register.php' ?>">Get started</a>
            <?php endif; ?>
        </nav>
    </div>
</header>
<main class="container">
<?php foreach (flashes() as $f): ?>
    <div class="flash <?= e($f['type']) ?>"><?= e($f['message']) ?></div>
<?php endforeach; ?>
