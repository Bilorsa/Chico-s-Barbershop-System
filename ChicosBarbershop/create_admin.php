<?php
/** Run from a terminal: php create_admin.php */
if (PHP_SAPI !== 'cli') {
    http_response_code(403);
    exit('Run this setup script from the command line only.');
}
require_once __DIR__ . '/config/database.php';

echo "Chico's Barbershop — Create administrator\n";
echo "First name: ";
$first = trim((string) fgets(STDIN));
echo "Last name: ";
$last = trim((string) fgets(STDIN));
echo "Email: ";
$email = strtolower(trim((string) fgets(STDIN)));
echo "Phone: ";
$phone = trim((string) fgets(STDIN));
echo "Password (minimum 12 characters): ";
$password = rtrim((string) fgets(STDIN), "\r\n");

if ($first === '' || $last === '' || !filter_var($email, FILTER_VALIDATE_EMAIL) || $phone === '' || strlen($password) < 12) {
    fwrite(STDERR, "Invalid details. Password must be at least 12 characters. No account was created.\n");
    exit(1);
}
try {
    $stmt = $pdo->prepare("INSERT INTO users (first_name,last_name,email,phone,password_hash,role) VALUES (?,?,?,?,?,'admin')");
    $stmt->execute([$first,$last,$email,$phone,password_hash($password, PASSWORD_DEFAULT)]);
    echo "Administrator account created successfully for {$email}.\n";
} catch (PDOException $exception) {
    fwrite(STDERR, $exception->getCode() === '23000' ? "An account with that email already exists.\n" : "Could not create the administrator account. Check your database connection.\n");
    exit(1);
}
