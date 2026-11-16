<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
require_admin();
$pageTitle = 'Manage services';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $action = (string) ($_POST['action'] ?? '');
    if ($action === 'save') {
        $id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT) ?: 0;
        $name = trim((string) ($_POST['name'] ?? ''));
        $description = trim((string) ($_POST['description'] ?? ''));
        $price = filter_input(INPUT_POST, 'price', FILTER_VALIDATE_FLOAT);
        $duration = filter_input(INPUT_POST, 'duration', FILTER_VALIDATE_INT);
        if ($name === '' || strlen($name) > 100 || $description === '' || $price === false || $price === null || $price < 0 || !$duration || $duration < 15 || $duration > 240) {
            set_flash('error', 'Enter a service name, description, valid price and duration (15–240 minutes).');
        } else {
            if ($id) { $stmt=$pdo->prepare('UPDATE services SET name=?, description=?, price=?, duration=? WHERE id=?'); $stmt->execute([$name,$description,$price,$duration,$id]); }
            else { $stmt=$pdo->prepare('INSERT INTO services (name, description, price, duration) VALUES (?,?,?,?)'); $stmt->execute([$name,$description,$price,$duration]); }
            set_flash('success', 'Service saved successfully.');
        }
    } elseif ($action === 'toggle') {
        $id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT) ?: 0;
        $stmt = $pdo->prepare('UPDATE services SET is_active = 1 - is_active WHERE id = ?'); $stmt->execute([$id]);
        set_flash('success', 'Service availability updated.');
    }
    redirect('services.php');
}
$editId = filter_input(INPUT_GET, 'edit', FILTER_VALIDATE_INT) ?: 0;
$edit = null;
if ($editId) { $stmt=$pdo->prepare('SELECT * FROM services WHERE id=?'); $stmt->execute([$editId]); $edit=$stmt->fetch() ?: null; }
$services = $pdo->query('SELECT * FROM services ORDER BY is_active DESC, name')->fetchAll();
require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">MANAGEMENT / MENU</span><h1>Service <em>studio.</em></h1><p>Keep your service menu, prices and appointment durations current.</p></div></section>
<section class="section section-light"><div class="container admin-content"><div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">SERVICE CATALOGUE</span><h2><?= $edit ? 'Edit service' : 'Add a service' ?></h2></div><a class="back-link" href="index.php">← Dashboard</a></div>
<div class="admin-form-card"><form method="post" class="admin-edit-form"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="action" value="save"><input type="hidden" name="id" value="<?= (int) ($edit['id'] ?? 0) ?>"><div class="field"><label for="name">Service name</label><input id="name" name="name" maxlength="100" required value="<?= e($edit['name'] ?? '') ?>"></div><div class="field"><label for="description">Description</label><input id="description" name="description" maxlength="500" required value="<?= e($edit['description'] ?? '') ?>"></div><div class="field-row"><div class="field"><label for="price">Price (R)</label><input id="price" name="price" type="number" min="0" step="0.01" required value="<?= e($edit['price'] ?? '') ?>"></div><div class="field"><label for="duration">Duration (minutes)</label><input id="duration" name="duration" type="number" min="15" max="240" step="5" required value="<?= e($edit['duration'] ?? '30') ?>"></div></div><div class="form-actions"><button class="btn btn-dark" type="submit"><?= $edit ? 'Save changes' : 'Add service' ?></button><?php if ($edit): ?><a class="back-link" href="services.php">Cancel edit</a><?php endif; ?></div></form></div>
<div class="table-card"><div class="table-heading"><h3>Current services</h3></div><div class="table-scroll"><table><thead><tr><th>Service</th><th>Price</th><th>Duration</th><th>Availability</th><th>Actions</th></tr></thead><tbody><?php foreach ($services as $s): ?><tr><td><strong><?= e($s['name']) ?></strong><small class="table-sub"><?= e($s['description']) ?></small></td><td><?= money($s['price']) ?></td><td><?= (int) $s['duration'] ?> min</td><td><span class="status <?= $s['is_active'] ? 'status-confirmed' : 'status-cancelled' ?>"><?= $s['is_active'] ? 'Active' : 'Hidden' ?></span></td><td><div class="action-row"><a href="services.php?edit=<?= (int) $s['id'] ?>">Edit</a><form method="post"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="action" value="toggle"><input type="hidden" name="id" value="<?= (int) $s['id'] ?>"><button class="link-button" type="submit"><?= $s['is_active'] ? 'Hide' : 'Activate' ?></button></form></div></td></tr><?php endforeach; ?></tbody></table></div></div></div></section>
<?php require '../includes/footer.php'; ?>
