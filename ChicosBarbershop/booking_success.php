<?php
declare(strict_types=1);
require_once __DIR__ . '/includes/functions.php';

$confirmation = $_SESSION['booking_confirmation'] ?? null;
unset($_SESSION['booking_confirmation']);
if (!$confirmation) {
    redirect('book.php');
}

$title = 'Booking confirmed';
require __DIR__ . '/includes/header.php';
?>
<section class="booking-success-page">
    <div class="success-card">
        <div class="success-icon">✓</div>
        <span class="eyebrow">BOOKING CONFIRMED</span>
        <h1>Your chair is <span>reserved.</span></h1>
        <p>Thanks for booking with Chico's Barber Shop. We have saved your appointment.</p>
        <div class="confirmation-details">
            <div><span>Booking #</span><strong><?= (int)$confirmation['id'] ?></strong></div>
            <div><span>Service</span><strong><?= e($confirmation['service']) ?></strong></div>
            <div><span>Barber</span><strong><?= e($confirmation['barber']) ?></strong></div>
            <div><span>Date</span><strong><?= e($confirmation['date']) ?></strong></div>
            <div><span>Time</span><strong><?= e($confirmation['time']) ?></strong></div>
            <div><span>Total</span><strong>R<?= number_format((float)$confirmation['price'], 2) ?></strong></div>
        </div>
        <div class="actions">
            <?php if (currentUser()): ?><a class="btn" href="my_bookings.php">View My Bookings</a><?php endif; ?>
            <a class="btn secondary" href="index.php">Back to Home</a>
            <a class="btn secondary" href="book.php">Book Another</a>
        </div>
    </div>
</section>
<?php require __DIR__ . '/includes/footer.php'; ?>
