<?php
declare(strict_types=1);
require_once __DIR__ . '/includes/functions.php';

$pdo = db();
$services = $pdo->query("SELECT id,name,duration_minutes,price FROM services WHERE active=1 ORDER BY name")->fetchAll();
$barbers = $pdo->query("SELECT id,name FROM barbers WHERE active=1 ORDER BY name")->fetchAll();

$serviceId = (int)($_POST['service_id'] ?? $_GET['service_id'] ?? 0);
$barberId = (int)($_POST['barber_id'] ?? $_GET['barber_id'] ?? 0);
$date = trim($_POST['appointment_date'] ?? $_GET['appointment_date'] ?? date('Y-m-d'));
$selectedTime = trim($_POST['appointment_time'] ?? '');

$service = null;
$barber = null;

if ($serviceId > 0) {
    $stmt = $pdo->prepare("SELECT id,name,duration_minutes,price FROM services WHERE id=? AND active=1");
    $stmt->execute([$serviceId]);
    $service = $stmt->fetch();
}
if ($barberId > 0) {
    $stmt = $pdo->prepare("SELECT id,name FROM barbers WHERE id=? AND active=1");
    $stmt->execute([$barberId]);
    $barber = $stmt->fetch();
}

if (!validDate($date) || $date < date('Y-m-d')) {
    $date = date('Y-m-d');
}

function bookingUrl(int $serviceId, int $barberId, string $date): string {
    return 'book.php?' . http_build_query([
        'service_id' => $serviceId,
        'barber_id' => $barberId,
        'appointment_date' => $date,
    ]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verifyCsrf();

    if (!$service || !$barber || !validDate($date) || !validTime($selectedTime) || $date < date('Y-m-d')) {
        flash('error', 'Please select a valid service, barber, date and time.');
        redirect(bookingUrl($serviceId, $barberId, $date));
    }

    $customerId = (int)(currentUser()['id'] ?? 0);

    if (!$customerId) {
        $name = trim($_POST['customer_name'] ?? '');
        $email = trim($_POST['customer_email'] ?? '');
        $phone = trim($_POST['customer_phone'] ?? '');

        if ($name === '' || !filter_var($email, FILTER_VALIDATE_EMAIL) || $phone === '') {
            flash('error', 'Please enter your name, a valid email address and phone number.');
            redirect(bookingUrl($serviceId, $barberId, $date));
        }

        $stmt = $pdo->prepare("SELECT id FROM customers WHERE email=?");
        $stmt->execute([$email]);
        $customerId = (int)($stmt->fetchColumn() ?: 0);

        if ($customerId === 0) {
            $stmt = $pdo->prepare("INSERT INTO customers(name,email,phone,password_hash,role) VALUES(?,?,?,NULL,'customer')");
            $stmt->execute([$name, $email, $phone]);
            $customerId = (int)$pdo->lastInsertId();
        } else {
            $stmt = $pdo->prepare("UPDATE customers SET name=?, phone=? WHERE id=?");
            $stmt->execute([$name, $phone, $customerId]);
        }
    }

    $notes = trim($_POST['notes'] ?? '');
    $start = new DateTime("$date $selectedTime:00");
    $end = (clone $start)->modify('+' . (int)$service['duration_minutes'] . ' minutes');
    $open = new DateTime("$date 09:00:00");
    $close = new DateTime("$date 18:00:00");

    if ($start < $open || $end > $close) {
        flash('error', 'Please choose a time between 09:00 and 18:00.');
        redirect(bookingUrl($serviceId, $barberId, $date));
    }
    if ($start <= new DateTime()) {
        flash('error', 'That time has already passed. Please choose another slot.');
        redirect(bookingUrl($serviceId, $barberId, $date));
    }

    try {
        $pdo->beginTransaction();

        // Lock the barber's existing appointments while checking for an overlap.
        $stmt = $pdo->prepare(
            "SELECT id FROM appointments
             WHERE barber_id=?
             AND status IN ('pending','confirmed')
             AND start_at < ?
             AND end_at > ?
             FOR UPDATE"
        );
        $stmt->execute([
            $barberId,
            $end->format('Y-m-d H:i:s'),
            $start->format('Y-m-d H:i:s')
        ]);

        if ($stmt->fetch()) {
            throw new RuntimeException('That time was just booked. Please choose another slot.');
        }

        $stmt = $pdo->prepare(
            "INSERT INTO appointments(customer_id,barber_id,service_id,start_at,end_at,status,notes)
             VALUES(?,?,?,?,?,'confirmed',?)"
        );
        $stmt->execute([
            $customerId,
            $barberId,
            $serviceId,
            $start->format('Y-m-d H:i:s'),
            $end->format('Y-m-d H:i:s'),
            $notes
        ]);

        $appointmentId = (int)$pdo->lastInsertId();

        $stmt = $pdo->prepare("INSERT INTO payments(appointment_id,amount,status,method) VALUES(?,?,'unpaid','cash')");
        $stmt->execute([$appointmentId, $service['price']]);

        $pdo->commit();

        $_SESSION['booking_confirmation'] = [
            'id' => $appointmentId,
            'service' => $service['name'],
            'barber' => $barber['name'],
            'date' => $start->format('D, d M Y'),
            'time' => $start->format('H:i'),
            'price' => (float)$service['price'],
        ];
        redirect('booking_success.php');
    } catch (Throwable $e) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        flash('error', $e instanceof RuntimeException ? $e->getMessage() : 'We could not complete the booking. Please try again.');
        redirect(bookingUrl($serviceId, $barberId, $date));
    }
}

$availableSlots = [];
if ($service && $barber && $date >= date('Y-m-d')) {
    $stmt = $pdo->prepare(
        "SELECT start_at,end_at FROM appointments
         WHERE barber_id=?
         AND DATE(start_at)=?
         AND status IN ('pending','confirmed')
         ORDER BY start_at"
    );
    $stmt->execute([$barberId, $date]);
    $busy = $stmt->fetchAll();

    $slot = new DateTime("$date 09:00:00");
    $closing = new DateTime("$date 18:00:00");
    $now = new DateTime();

    while ($slot < $closing) {
        $slotEnd = (clone $slot)->modify('+' . (int)$service['duration_minutes'] . ' minutes');
        $overlap = false;

        foreach ($busy as $appointment) {
            $busyStart = new DateTime($appointment['start_at']);
            $busyEnd = new DateTime($appointment['end_at']);
            if ($slot < $busyEnd && $slotEnd > $busyStart) {
                $overlap = true;
                break;
            }
        }

        if (!$overlap && $slotEnd <= $closing && ($date !== date('Y-m-d') || $slot > $now)) {
            $availableSlots[] = $slot->format('H:i');
        }
        $slot->modify('+30 minutes');
    }
}

$title = 'Book an appointment';
require __DIR__ . '/includes/header.php';
?>

<section class="booking-page">
    <div class="booking-header">
        <span class="eyebrow">CHICO'S ONLINE BOOKING</span>
        <h1>Reserve your <span>chair.</span></h1>
        <p>Select a service, barber and date, then choose from real available appointment times.</p>
    </div>

    <div class="booking-layout">
        <div class="booking-form card">
            <!-- Step 1: selections use GET, so choosing one option never loses the other selections. -->
            <form method="get" action="book.php" id="availability-form">
                <div class="booking-step"><span class="step-number">01</span><div><h2>Choose your service</h2><p>What are you getting done?</p></div></div>
                <div class="choice-grid">
                    <?php foreach ($services as $s): ?>
                        <label class="choice-card">
                            <input type="radio" name="service_id" value="<?= (int)$s['id'] ?>" <?= $serviceId === (int)$s['id'] ? 'checked' : '' ?> required>
                            <span><strong><?= e($s['name']) ?></strong><small><?= (int)$s['duration_minutes'] ?> min · R<?= number_format((float)$s['price'], 2) ?></small></span>
                        </label>
                    <?php endforeach; ?>
                </div>

                <div class="booking-step"><span class="step-number">02</span><div><h2>Choose your barber</h2><p>Pick the person behind your cut.</p></div></div>
                <div class="choice-grid barber-choice-grid">
                    <?php foreach ($barbers as $b): ?>
                        <label class="choice-card">
                            <input type="radio" name="barber_id" value="<?= (int)$b['id'] ?>" <?= $barberId === (int)$b['id'] ? 'checked' : '' ?> required>
                            <span><strong><?= e($b['name']) ?></strong><small>Master Barber</small></span>
                        </label>
                    <?php endforeach; ?>
                </div>

                <div class="booking-step"><span class="step-number">03</span><div><h2>Pick a date</h2><p>Opening hours: 09:00–18:00.</p></div></div>
                <div class="field">
                    <label for="appointment_date">Appointment date</label>
                    <input id="appointment_date" type="date" name="appointment_date" value="<?= e($date) ?>" min="<?= date('Y-m-d') ?>" required>
                </div>
                <button class="btn" type="submit" style="margin-top:16px">Check availability <span>→</span></button>
            </form>

            <?php if ($service && $barber): ?>
                <div class="booking-step" style="margin-top:38px"><span class="step-number">04</span><div><h2>Choose a time</h2><p><?= e($service['name']) ?> with <?= e($barber['name']) ?> on <?= e(date('D, d M Y', strtotime($date))) ?>.</p></div></div>
                <?php if ($availableSlots): ?>
                    <form method="post" action="book.php" id="booking-confirm-form">
                        <input type="hidden" name="csrf" value="<?= e(csrfToken()) ?>">
                        <input type="hidden" name="service_id" value="<?= (int)$serviceId ?>">
                        <input type="hidden" name="barber_id" value="<?= (int)$barberId ?>">
                        <input type="hidden" name="appointment_date" value="<?= e($date) ?>">

                        <div class="time-grid">
                            <?php foreach ($availableSlots as $slot): ?>
                                <label class="time-option"><input type="radio" name="appointment_time" value="<?= e($slot) ?>" required><span><?= e($slot) ?></span></label>
                            <?php endforeach; ?>
                        </div>

                        <?php if (!currentUser()): ?>
                            <div class="booking-step"><span class="step-number">05</span><div><h2>Your details</h2><p>Guest booking — no account required.</p></div></div>
                            <div class="form-grid">
                                <div class="field"><label for="customer_name">Name</label><input id="customer_name" name="customer_name" maxlength="100" autocomplete="name" required></div>
                                <div class="field"><label for="customer_phone">Phone</label><input id="customer_phone" name="customer_phone" maxlength="30" autocomplete="tel" required></div>
                                <div class="field full"><label for="customer_email">Email</label><input id="customer_email" type="email" name="customer_email" maxlength="190" autocomplete="email" required></div>
                            </div>
                        <?php endif; ?>

                        <div class="field" style="margin-top:18px"><label for="notes">Notes <span class="muted">(optional)</span></label><textarea id="notes" name="notes" rows="3" maxlength="500" placeholder="Anything your barber should know?"></textarea></div>
                        <button class="btn btn-large booking-submit" type="submit">Confirm Appointment <span>→</span></button>
                    </form>
                <?php else: ?>
                    <div class="no-slots"><strong>No available times for this selection.</strong><span>Try another date or barber.</span></div>
                <?php endif; ?>
            <?php else: ?>
                <div class="booking-placeholder"><span>✦</span><strong>Select a service, barber and date, then click “Check availability”.</strong></div>
            <?php endif; ?>
        </div>

        <aside class="booking-summary">
            <div class="summary-card">
                <span class="eyebrow">YOUR APPOINTMENT</span><h3>Booking summary</h3>
                <div class="summary-row"><span>Service</span><strong><?= $service ? e($service['name']) : 'Not selected' ?></strong></div>
                <div class="summary-row"><span>Barber</span><strong><?= $barber ? e($barber['name']) : 'Not selected' ?></strong></div>
                <div class="summary-row"><span>Date</span><strong><?= e(date('d M Y', strtotime($date))) ?></strong></div>
                <div class="summary-row"><span>Duration</span><strong><?= $service ? (int)$service['duration_minutes'].' min' : '—' ?></strong></div>
                <div class="summary-total"><span>Total</span><strong><?= $service ? 'R'.number_format((float)$service['price'],2) : '—' ?></strong></div>
            </div>
            <div class="booking-note"><strong>✓ Instant confirmation</strong><p>The server checks the barber's live schedule again when you confirm, preventing double bookings.</p></div>
        </aside>
    </div>
</section>

<?php require __DIR__ . '/includes/footer.php'; ?>
