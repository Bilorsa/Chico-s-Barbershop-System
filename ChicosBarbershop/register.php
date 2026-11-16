<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
if (current_user()) redirect(is_admin() ? 'admin/index.php' : (is_barber() ? 'barber/index.php' : 'dashboard.php'));
$pageTitle = 'Create your account';
$errors = [];
$values = ['first_name' => '', 'last_name' => '', 'email' => '', 'phone' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    foreach ($values as $key => $_) $values[$key] = trim((string) ($_POST[$key] ?? ''));
    $password = (string) ($_POST['password'] ?? '');
    if ($values['first_name'] === '' || strlen($values['first_name']) > 60) $errors[] = 'Enter a valid first name (maximum 60 characters).';
    if ($values['last_name'] === '' || strlen($values['last_name']) > 60) $errors[] = 'Enter a valid last name (maximum 60 characters).';
    if (!filter_var($values['email'], FILTER_VALIDATE_EMAIL)) $errors[] = 'Enter a valid email address.';
    if ($values['phone'] === '' || strlen($values['phone']) > 25) $errors[] = 'Enter a valid contact number.';
    if (strlen($password) < 8) $errors[] = 'Your password must contain at least 8 characters.';
    if (!$errors) {
        try {
            $stmt = $pdo->prepare('INSERT INTO users (first_name, last_name, email, phone, password_hash) VALUES (?, ?, ?, ?, ?)');
            $stmt->execute([$values['first_name'], $values['last_name'], strtolower($values['email']), $values['phone'], password_hash($password, PASSWORD_DEFAULT)]);
            session_regenerate_id(true);
            $_SESSION['user'] = ['id' => (int) $pdo->lastInsertId(), 'first_name' => $values['first_name'], 'last_name' => $values['last_name'], 'email' => strtolower($values['email']), 'role' => 'customer'];
            set_flash('success', 'Your account is ready. Let’s get you booked in.');
            redirect('book.php');
        } catch (PDOException $exception) {
            if ($exception->getCode() === '23000') $errors[] = 'An account with that email address already exists.';
            else $errors[] = 'We could not create your account right now. Please try again.';
        }
    }
}
require 'includes/header.php';
?>
<section class="auth-section"><div class="auth-aside"><span class="eyebrow">JOIN THE CHICO'S CLUB</span><h1>Your next<br><em>great cut</em><br>starts here.</h1><p>Create an account to book appointments and keep track of every visit.</p><div class="auth-aside-bottom">GOOD CUTS. GOOD PEOPLE. GREAT CONFIDENCE.</div></div><div class="auth-panel"><div class="form-heading"><span class="eyebrow eyebrow-dark">GET STARTED</span><h2>Create an account</h2><p>It only takes a minute.</p></div>
<?php if ($errors): ?><div class="alert alert-error"><ul><?php foreach ($errors as $error): ?><li><?= e($error) ?></li><?php endforeach; ?></ul></div><?php endif; ?>
<form method="post" class="form-stack" novalidate><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><div class="field-row"><div class="field"><label for="first_name">First name</label><input id="first_name" name="first_name" autocomplete="given-name" required maxlength="60" value="<?= e($values['first_name']) ?>"></div><div class="field"><label for="last_name">Last name</label><input id="last_name" name="last_name" autocomplete="family-name" required maxlength="60" value="<?= e($values['last_name']) ?>"></div></div><div class="field"><label for="email">Email address</label><input id="email" name="email" type="email" autocomplete="email" required value="<?= e($values['email']) ?>"></div><div class="field"><label for="phone">Contact number</label><input id="phone" name="phone" type="tel" autocomplete="tel" required maxlength="25" placeholder="e.g. 071 234 5678" value="<?= e($values['phone']) ?>"></div><div class="field"><label for="password">Password</label><input id="password" name="password" type="password" autocomplete="new-password" minlength="8" required><small>Use at least 8 characters.</small></div><button class="btn btn-dark btn-full" type="submit">Create my account <span>↗</span></button></form><p class="form-foot">Already have an account? <a href="login.php">Sign in</a></p></div></section>
<?php require 'includes/footer.php'; ?>
