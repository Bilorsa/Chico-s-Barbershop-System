<?php
$title='Register'; require_once __DIR__ . '/includes/functions.php';
if (currentUser()) redirect('index.php');
if ($_SERVER['REQUEST_METHOD']==='POST') {
    verifyCsrf(); $name=trim($_POST['name']??''); $email=trim($_POST['email']??''); $phone=trim($_POST['phone']??''); $password=$_POST['password']??'';
    if (!$name || !filter_var($email,FILTER_VALIDATE_EMAIL) || !$phone || strlen($password)<8) flash('error','Use a valid name, email, phone and password of at least 8 characters.');
    else {
        $stmt=db()->prepare("SELECT id FROM customers WHERE email=?"); $stmt->execute([$email]);
        if ($stmt->fetch()) flash('error','An account with that email already exists.');
        else { $stmt=db()->prepare("INSERT INTO customers(name,email,phone,password_hash) VALUES(?,?,?,?)"); $stmt->execute([$name,$email,$phone,password_hash($password,PASSWORD_DEFAULT)]); flash('success','Registration complete. Please log in.'); redirect('login.php'); }
    }
}
require __DIR__.'/includes/header.php';
?>
<div class="card"><h2>Create account</h2><form method="post"><input type="hidden" name="csrf" value="<?=e(csrfToken())?>"><div class="form-grid">
<div class="field"><label>Name</label><input name="name" required></div><div class="field"><label>Phone</label><input name="phone" required></div>
<div class="field full"><label>Email</label><input type="email" name="email" required></div><div class="field full"><label>Password</label><input type="password" name="password" minlength="8" required></div>
<div class="field full"><button>Register</button></div></div></form></div>
<?php require __DIR__.'/includes/footer.php'; ?>
