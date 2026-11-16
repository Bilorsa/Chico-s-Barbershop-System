<?php
require_once '../config/database.php';
require_once '../includes/functions.php';
require_admin();
$pageTitle = 'Manage barbers';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $action = (string) ($_POST['action'] ?? '');

    if ($action === 'save') {
        $id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT) ?: 0;
        $first = trim((string) ($_POST['first_name'] ?? ''));
        $last = trim((string) ($_POST['last_name'] ?? ''));
        $specialty = trim((string) ($_POST['specialty'] ?? ''));
        $experience = filter_input(INPUT_POST, 'experience_years', FILTER_VALIDATE_INT);
        $bio = trim((string) ($_POST['bio'] ?? ''));
        $email = strtolower(trim((string) ($_POST['account_email'] ?? '')));
        $phone = trim((string) ($_POST['account_phone'] ?? ''));
        $password = (string) ($_POST['account_password'] ?? '');

        if ($first === '' || $last === '' || $specialty === '' || $experience === false || $experience === null || $experience < 0 || $experience > 60) {
            set_flash('error', 'Please complete all required barber profile details.');
        } elseif ($email !== '' && !filter_var($email, FILTER_VALIDATE_EMAIL)) {
            set_flash('error', 'Enter a valid barber login email address.');
        } else {
            try {
                $pdo->beginTransaction();
                $existing = null;
                if ($id > 0) {
                    $find = $pdo->prepare('SELECT b.*, u.id AS account_id FROM barbers b LEFT JOIN users u ON u.id = b.user_id WHERE b.id = ? FOR UPDATE');
                    $find->execute([$id]);
                    $existing = $find->fetch();
                    if (!$existing) throw new RuntimeException('The selected barber profile could not be found.');
                }

                $accountId = $existing ? ($existing['account_id'] ? (int) $existing['account_id'] : null) : null;
                if ($accountId) {
                    $accountQuery = $pdo->prepare('SELECT email, phone FROM users WHERE id = ?');
                    $accountQuery->execute([$accountId]);
                    $accountDetails = $accountQuery->fetch() ?: [];
                    if ($email === '') $email = (string) ($accountDetails['email'] ?? '');
                    if ($phone === '') $phone = (string) ($accountDetails['phone'] ?? 'Not provided');
                }

                if ($accountId) {
                    if ($password !== '' && strlen($password) < 12) throw new RuntimeException('Barber login passwords must contain at least 12 characters.');
                    $updateUser = $pdo->prepare('UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?' . ($password !== '' ? ', password_hash = ?' : '') . ' WHERE id = ?');
                    $values = [$first, $last, $email, $phone !== '' ? $phone : 'Not provided'];
                    if ($password !== '') $values[] = password_hash($password, PASSWORD_DEFAULT);
                    $values[] = $accountId;
                    $updateUser->execute($values);
                } elseif ($email !== '' || $password !== '' || $phone !== '') {
                    if ($email === '' || $password === '' || strlen($password) < 12 || $phone === '') {
                        throw new RuntimeException('To create a barber login, provide an email, phone number and password of at least 12 characters.');
                    }
                    $createUser = $pdo->prepare("INSERT INTO users (first_name, last_name, email, phone, password_hash, role) VALUES (?, ?, ?, ?, ?, 'barber')");
                    $createUser->execute([$first, $last, $email, $phone, password_hash($password, PASSWORD_DEFAULT)]);
                    $accountId = (int) $pdo->lastInsertId();
                }

                if ($existing) {
                    $save = $pdo->prepare('UPDATE barbers SET first_name = ?, last_name = ?, specialty = ?, experience_years = ?, bio = ?, user_id = ? WHERE id = ?');
                    $save->execute([$first, $last, $specialty, $experience, $bio ?: null, $accountId, $id]);
                } else {
                    $save = $pdo->prepare('INSERT INTO barbers (first_name, last_name, specialty, experience_years, bio, user_id) VALUES (?, ?, ?, ?, ?, ?)');
                    $save->execute([$first, $last, $specialty, $experience, $bio ?: null, $accountId]);
                }
                $pdo->commit();
                set_flash('success', $accountId ? 'Barber profile and login account saved.' : 'Barber profile saved. You can create a login account by editing this profile.');
            } catch (Throwable $exception) {
                if ($pdo->inTransaction()) $pdo->rollBack();
                if ($exception instanceof RuntimeException) set_flash('error', $exception->getMessage());
                elseif ($exception instanceof PDOException && $exception->getCode() === '23000') set_flash('error', 'That email address is already in use. Choose another email.');
                else { error_log('Barber save failed: ' . $exception->getMessage()); set_flash('error', 'The barber profile could not be saved. Please check the details and try again.'); }
            }
        }
    } elseif ($action === 'toggle') {
        $id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT) ?: 0;
        $stmt = $pdo->prepare('UPDATE barbers SET is_active = 1 - is_active WHERE id = ?');
        $stmt->execute([$id]);
        set_flash('success', 'Barber booking availability updated.');
    }
    redirect('barbers.php');
}

$editId = filter_input(INPUT_GET, 'edit', FILTER_VALIDATE_INT) ?: 0;
$edit = null;
if ($editId) {
    $stmt = $pdo->prepare('SELECT b.*, u.email AS account_email, u.phone AS account_phone FROM barbers b LEFT JOIN users u ON u.id = b.user_id WHERE b.id = ?');
    $stmt->execute([$editId]);
    $edit = $stmt->fetch() ?: null;
}
$barbers = $pdo->query('SELECT b.*, u.email AS account_email FROM barbers b LEFT JOIN users u ON u.id = b.user_id ORDER BY b.is_active DESC, b.first_name')->fetchAll();
require '../includes/header.php';
?>
<section class="page-hero admin-hero"><div class="container"><span class="eyebrow">MANAGEMENT / TEAM</span><h1>The barber <em>line-up.</em></h1><p>Manage team profiles, booking availability and individual barber portal accounts.</p></div></section>
<section class="section section-light"><div class="container admin-content">
    <div class="admin-toolbar"><div><span class="eyebrow eyebrow-dark">TEAM PROFILES</span><h2><?= $edit ? 'Edit barber & access' : 'Add a barber' ?></h2></div><a class="back-link" href="index.php">← Dashboard</a></div>
    <div class="admin-form-card"><form method="post" class="admin-edit-form"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="action" value="save"><input type="hidden" name="id" value="<?= (int) ($edit['id'] ?? 0) ?>">
        <div class="field-row"><div class="field"><label for="first_name">First name</label><input id="first_name" name="first_name" required maxlength="60" value="<?= e($edit['first_name'] ?? '') ?>"></div><div class="field"><label for="last_name">Last name</label><input id="last_name" name="last_name" required maxlength="60" value="<?= e($edit['last_name'] ?? '') ?>"></div></div>
        <div class="field-row"><div class="field"><label for="specialty">Specialty</label><input id="specialty" name="specialty" required maxlength="120" value="<?= e($edit['specialty'] ?? 'All-round barber') ?>"></div><div class="field"><label for="experience_years">Years of experience</label><input id="experience_years" name="experience_years" type="number" min="0" max="60" required value="<?= e($edit['experience_years'] ?? '1') ?>"></div></div>
        <div class="field"><label for="bio">Short bio</label><textarea id="bio" name="bio" rows="2" maxlength="500"><?= e($edit['bio'] ?? '') ?></textarea></div>
        <div class="barber-account-fields"><div><span class="eyebrow eyebrow-dark">BARBER PORTAL ACCESS</span><p>Create or update this barber's separate sign-in. Leave these fields empty to save only the public profile.</p></div>
            <div class="field-row"><div class="field"><label for="account_email">Login email</label><input id="account_email" name="account_email" type="email" autocomplete="off" value="<?= e($edit['account_email'] ?? '') ?>" placeholder="barber@example.com"></div><div class="field"><label for="account_phone">Phone number</label><input id="account_phone" name="account_phone" type="tel" value="<?= e($edit['account_phone'] ?? '') ?>" placeholder="e.g. 071 234 5678"></div></div>
            <div class="field"><label for="account_password">Password <?= !empty($edit['account_email']) ? '(leave blank to keep current password)' : '' ?></label><input id="account_password" name="account_password" type="password" minlength="12" autocomplete="new-password" placeholder="Minimum 12 characters" <?= empty($edit['account_email']) ? '' : '' ?>></div>
            <small class="barber-security-note">A barber login is separate from a customer account. Passwords are securely hashed and never displayed.</small>
        </div>
        <div class="form-actions"><button class="btn btn-dark" type="submit"><?= $edit ? 'Save barber details' : 'Add barber' ?></button><?php if ($edit): ?><a class="back-link" href="barbers.php">Cancel edit</a><?php endif; ?></div>
    </form></div>
    <div class="team-admin-grid"><?php foreach ($barbers as $b): ?><article class="team-admin-card"><div class="team-avatar"><?= e(strtoupper(substr($b['first_name'], 0, 1) . substr($b['last_name'], 0, 1))) ?></div><span class="status <?= $b['is_active'] ? 'status-confirmed' : 'status-cancelled' ?>"><?= $b['is_active'] ? 'Active' : 'Hidden' ?></span><h3><?= e($b['first_name'] . ' ' . $b['last_name']) ?></h3><p><?= e($b['specialty']) ?> · <?= (int) $b['experience_years'] ?> years</p><small><?= e($b['bio'] ?? '') ?></small><div class="barber-account-status"><?php if ($b['account_email']): ?><span class="account-enabled">Portal enabled</span><small><?= e($b['account_email']) ?></small><?php else: ?><span class="account-disabled">No barber login yet</span><?php endif; ?></div><div class="action-row"><a href="barbers.php?edit=<?= (int) $b['id'] ?>">Edit profile / login</a><form method="post"><input type="hidden" name="csrf_token" value="<?= e(csrf_token()) ?>"><input type="hidden" name="action" value="toggle"><input type="hidden" name="id" value="<?= (int) $b['id'] ?>"><button class="link-button" type="submit"><?= $b['is_active'] ? 'Hide from bookings' : 'Activate' ?></button></form></div></article><?php endforeach; ?></div>
</div></section>
<?php require '../includes/footer.php'; ?>
