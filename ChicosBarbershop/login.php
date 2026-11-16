<?php
require_once 'config/database.php';
require_once 'includes/functions.php';
if (current_user()) redirect(is_admin() ? 'admin/index.php' : (is_barber() ? 'barber/index.php' : 'dashboard.php'));
$pageTitle = 'Sign in';
$error = '';
$email = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $email = strtolower(trim((string) ($_POST['email'] ?? '')));
    $password = (string) ($_POST['password'] ?? '');
    $stmt = $pdo->prepare('SELECT id, first_name, last_name, email, password_hash, role FROM users WHERE email = ? LIMIT 1');
    $stmt->execute([$email]);
    $account = $stmt->fetch();
    if ($account && password_verify($password, $account['password_hash'])) {
        session_regenerate_id(true);
        $_SESSION['user'] = ['id' => (int) $account['id'], 'first_name' => $account['first_name'], 'last_name' => $account['last_name'], 'email' => $account['email'], 'role' => $account['role']];
        set_flash('success', 'Welcome back, ' . $account['first_name'] . '.');
        redirect($account['role'] === 'admin' ? 'admin/index.php' : ($account['role'] === 'barber' ? 'barber/index.php' : 'dashboard.php'));
    }
    $error = 'The email address or password you entered is incorrect.';
}
require 'includes/header.php';
?>
<section class="auth-section auth-section-login"><div class="auth-aside"><span class="eyebrow">WELCOME BACK</span><h1>Back in<br><em>the chair.</em></h1><p>Your next fresh cut is just a few clicks away. Sign in to manage your appointments.</p><div class="auth-aside-bottom">YOUR TIME. YOUR STYLE. YOUR BARBER.</div></div><div class="auth-panel"><div class="form-heading"><span class="eyebrow eyebrow-dark">GOOD TO SEE YOU</span><h2>Sign in</h2><p>Enter your details to continue.</p></div>
<?php if ($error): ?><div class="alert alert-error" role="alert"><?= e($error) ?></div><?php endif; ?>
<form method="post" class="form-stack"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><div class="field"><label for="email">Email address</label><input id="email" name="email" type="email" autocomplete="email" required value="<?= e($email) ?>"></div><div class="field"><label for="password">Password</label><input id="password" name="password" type="password" autocomplete="current-password" required></div><button class="btn btn-dark btn-full" type="submit">Sign in <span>↗</span></button></form><p class="form-foot">New to Chico's? <a href="register.php">Create an account</a></p></div></section>
<?php require 'includes/footer.php'; ?>
