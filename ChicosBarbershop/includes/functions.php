<?php
declare(strict_types=1);

if (session_status() !== PHP_SESSION_ACTIVE) {
    session_set_cookie_params([
        'httponly' => true,
        'secure' => !empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off',
        'samesite' => 'Lax',
    ]);
    session_start();
}

date_default_timezone_set('Africa/Johannesburg');

function e(mixed $value): string
{
    return htmlspecialchars((string) $value, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}

function redirect(string $path): never
{
    header('Location: ' . $path);
    exit;
}

function csrf_token(): string
{
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf_token'];
}

function verify_csrf(): void
{
    $submitted = (string) ($_POST['csrf_token'] ?? '');
    if ($submitted === '' || !hash_equals(csrf_token(), $submitted)) {
        http_response_code(419);
        exit('Your session has expired. Please go back, refresh the page and try again.');
    }
}

function set_flash(string $type, string $message): void
{
    $_SESSION['flash'] = ['type' => $type, 'message' => $message];
}

function get_flash(): ?array
{
    $flash = $_SESSION['flash'] ?? null;
    unset($_SESSION['flash']);
    return $flash;
}

function current_user(): ?array
{
    return $_SESSION['user'] ?? null;
}

function require_login(): void
{
    if (!current_user()) {
        set_flash('info', 'Please sign in to continue.');
        $script = (string) ($_SERVER['SCRIPT_NAME'] ?? '');
        $prefix = (str_contains($script, '/admin/') || str_contains($script, '/barber/')) ? '../' : '';
        redirect($prefix . 'login.php');
    }
}

function require_admin(): void
{
    require_login();
    if ((current_user()['role'] ?? '') !== 'admin') {
        http_response_code(403);
        exit('You do not have permission to view this page.');
    }
}

function is_admin(): bool
{
    return (current_user()['role'] ?? '') === 'admin';
}

function is_barber(): bool
{
    return (current_user()['role'] ?? '') === 'barber';
}

function require_customer(): void
{
    require_login();
    if (is_admin()) redirect('admin/index.php');
    if (is_barber()) redirect('barber/index.php');
}

/** Returns the barber profile linked to the signed-in barber account. */
function require_barber(PDO $pdo): array
{
    require_login();
    if (!is_barber()) {
        http_response_code(403);
        exit('You do not have permission to view the barber portal.');
    }

    $stmt = $pdo->prepare('SELECT id, first_name, last_name, specialty, experience_years, bio, is_active FROM barbers WHERE user_id = ? LIMIT 1');
    $stmt->execute([(int) current_user()['id']]);
    $barber = $stmt->fetch();
    if (!$barber || !(int) $barber['is_active']) {
        http_response_code(403);
        exit('Your barber profile is not active. Please contact the shop administrator.');
    }
    return $barber;
}

function money(mixed $amount): string
{
    return 'R' . number_format((float) $amount, 2);
}

function valid_date(string $date): bool
{
    $parsed = DateTime::createFromFormat('Y-m-d', $date);
    return $parsed && $parsed->format('Y-m-d') === $date;
}

function business_close_time(string $date): string
{
    // Sunday is closed. Saturday closes earlier than weekdays.
    return (new DateTime($date))->format('N') === '6' ? '15:00:00' : '17:00:00';
}

function available_slots(PDO $pdo, int $barberId, int $serviceId, string $date): array
{
    if ($barberId < 1 || $serviceId < 1 || !valid_date($date)) {
        return [];
    }

    $day = (int) (new DateTime($date))->format('N');
    if ($day === 7 || $date < date('Y-m-d')) {
        return [];
    }

    $serviceStmt = $pdo->prepare('SELECT duration FROM services WHERE id = ? AND is_active = 1');
    $serviceStmt->execute([$serviceId]);
    $service = $serviceStmt->fetch();
    if (!$service) {
        return [];
    }

    $barberStmt = $pdo->prepare('SELECT id FROM barbers WHERE id = ? AND is_active = 1');
    $barberStmt->execute([$barberId]);
    if (!$barberStmt->fetch()) {
        return [];
    }

    $busyStmt = $pdo->prepare("SELECT start_time, end_time FROM appointments
        WHERE barber_id = ? AND appointment_date = ? AND status <> 'Cancelled'");
    $busyStmt->execute([$barberId, $date]);
    $busy = $busyStmt->fetchAll();

    $duration = (int) $service['duration'];
    $close = business_close_time($date);
    $slots = [];
    $now = new DateTime();

    // Appointments start every 30 minutes, from 09:00.
    for ($minutes = 9 * 60; $minutes <= 16 * 60 + 30; $minutes += 30) {
        $start = sprintf('%02d:%02d:00', intdiv($minutes, 60), $minutes % 60);
        $endMinutes = $minutes + $duration;
        $end = sprintf('%02d:%02d:00', intdiv($endMinutes, 60), $endMinutes % 60);
        if ($end > $close) {
            continue;
        }
        if ($date === date('Y-m-d') && new DateTime($date . ' ' . $start) <= $now) {
            continue;
        }

        $overlaps = false;
        foreach ($busy as $booking) {
            if ($start < $booking['end_time'] && $end > $booking['start_time']) {
                $overlaps = true;
                break;
            }
        }
        if (!$overlaps) {
            $slots[] = substr($start, 0, 5);
        }
    }
    return $slots;
}
