<?php
$title='Barbers'; require_once __DIR__.'/../includes/functions.php'; requireAdmin(); $pdo=db();
if($_SERVER['REQUEST_METHOD']==='POST'){verifyCsrf();$name=trim($_POST['name']);$email=trim($_POST['email']);if($name){$stmt=$pdo->prepare("INSERT INTO barbers(name,email,active) VALUES(?,?,1)");$stmt->execute([$name,$email?:null]);flash('success','Barber added.');}redirect('barbers.php');}
$rows=$pdo->query("SELECT * FROM barbers ORDER BY name")->fetchAll();require __DIR__.'/../includes/header.php';?>
<div class="grid"><div class="card"><h2>Add barber</h2><form method="post"><input type="hidden" name="csrf" value="<?=e(csrfToken())?>"><div class="field"><label>Name</label><input name="name" required></div><div class="field"><label>Email</label><input type="email" name="email"></div><br><button>Add</button></form></div><div class="card"><h2>Barbers</h2><table class="table"><?php foreach($rows as $r):?><tr><td><?=e($r['name'])?></td><td><?=e($r['email'])?></td><td><?= $r['active']?'Active':'Inactive'?></td></tr><?php endforeach;?></table></div></div>
<?php require __DIR__.'/../includes/footer.php'; ?>
