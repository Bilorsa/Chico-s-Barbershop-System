<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
require_customer();
$pageTitle = 'Book an appointment';
$activePage = 'book';
$services = $pdo->query('SELECT id, name, price, duration FROM services WHERE is_active = 1 ORDER BY name')->fetchAll();
$barbers = $pdo->query('SELECT id, first_name, last_name, specialty FROM barbers WHERE is_active = 1 ORDER BY first_name')->fetchAll();
$selectedService = filter_input(INPUT_GET, 'service', FILTER_VALIDATE_INT) ?: 0;
$validServiceIds = array_column($services, 'id');
if (!in_array($selectedService, array_map('intval', $validServiceIds), true)) $selectedService = 0;
require 'includes/header.php';
?>
<section class="page-hero page-hero-book"><div class="container"><span class="eyebrow">YOUR FRESH START</span><h1>Book your <em>chair.</em></h1><p>Choose your service, barber and a time that suits you. We'll take care of the details.</p></div></section>
<section class="section section-light"><div class="container booking-layout"><div class="booking-main"><div class="form-heading"><span class="eyebrow eyebrow-dark">APPOINTMENT DETAILS</span><h2>Let's get you booked.</h2><p>All appointments are subject to availability.</p></div>
<form action="process_booking.php" method="post" class="form-stack booking-form" data-slots-url="available_slots.php"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>">
<div class="field"><label for="service_id">1. Choose your service</label><select id="service_id" name="service_id" required><option value="">Select a service</option><?php foreach ($services as $service): ?><option value="<?= (int) $service['id'] ?>" data-duration="<?= (int) $service['duration'] ?>" <?= $selectedService === (int) $service['id'] ? 'selected' : '' ?>><?= e($service['name']) ?> · <?= money($service['price']) ?> · <?= (int) $service['duration'] ?> min</option><?php endforeach; ?></select></div>
<div class="field"><label for="barber_id">2. Choose your barber</label><select id="barber_id" name="barber_id" required><option value="">Select a barber</option><?php foreach ($barbers as $barber): ?><option value="<?= (int) $barber['id'] ?>"><?= e($barber['first_name'] . ' ' . $barber['last_name']) ?> — <?= e($barber['specialty']) ?></option><?php endforeach; ?></select></div>
<div class="field"><label for="appointment_date">3. Choose a date</label><input id="appointment_date" name="appointment_date" type="date" min="<?= date('Y-m-d') ?>" required><small>We're open Monday to Saturday. Closed Sundays.</small></div>
<div class="field"><label for="start_time">4. Choose an available time</label><select id="start_time" name="start_time" required disabled><option value="">Choose a service, barber and date first</option></select><small id="slot-help" class="slot-help" aria-live="polite">Available times will appear here.</small></div>
<div class="field"><label for="customer_note">Anything we should know? <span class="optional">Optional</span></label><textarea id="customer_note" name="customer_note" rows="3" maxlength="500" placeholder="Tell your barber about your preferred style or any details..."></textarea></div>
<button class="btn btn-dark btn-full" type="submit">Confirm appointment <span>↗</span></button><p class="form-disclaimer">Your booking will be saved as pending until the shop confirms it.</p>
</form></div><aside class="booking-aside"><div class="aside-card"><span class="eyebrow">BEFORE YOU BOOK</span><h3>A little heads-up.</h3><ul class="check-list"><li>Appointments start every 30 minutes.</li><li>Weekday hours: 09:00–17:00.</li><li>Saturday hours: 09:00–15:00.</li><li>Please arrive 5 minutes early.</li><li>You can manage your bookings from your account.</li></ul><div class="aside-divider"></div><p>Need to change your plans? You can cancel an upcoming appointment from your dashboard.</p></div><div class="booking-help"><span>NEED A HAND?</span><strong>Talk to the team in-store.</strong><small>Johannesburg, South Africa</small></div></aside></div></section>
<?php require 'includes/footer.php'; ?>
