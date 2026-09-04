<?php
$title='Login'; require_once __DIR__ . '/includes/functions.php';
if (currentUser()) redirect('index.php');
if ($_SERVER['REQUEST_METHOD']==='POST') {
    verifyCsrf(); $email=trim($_POST['email']??''); $password=$_POST['password']??'';
    $stmt=db()->prepare("SELECT id,name,email,password_hash,role FROM customers WHERE email=?"); $stmt->execute([$email]); $u=$stmt->fetch();
    if ($u && $u['password_hash'] && password_verify($password,$u['password_hash'])) { unset($u['password_hash']); $_SESSION['user']=$u; redirect($u['role']==='admin'?'admin/index.php':'index.php'); }
    flash('error','Invalid email or password.');
}
require __DIR__.'/includes/header.php';
?>
<div class="card"><h2>Login</h2><form method="post"><input type="hidden" name="csrf" value="<?=e(csrfToken())?>"><div class="form-grid">
<div class="field full"><label>Email</label><input type="email" name="email" required></div><div class="field full"><label>Password</label><input type="password" name="password" required></div><div class="field full"><button>Login</button></div></div></form></div>
<?php require __DIR__.'/includes/footer.php'; ?>
