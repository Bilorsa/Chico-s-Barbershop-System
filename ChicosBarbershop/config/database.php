<?php
declare(strict_types=1);

$host = '127.0.0.1';
$dbname = 'barbershop_db';
$username = 'root';
$password = '';

try {
    $pdo = new PDO(
        "mysql:host={$host};dbname={$dbname};charset=utf8mb4",
        $username,
        $password,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false,
        ]
    );
} catch (PDOException $exception) {
    error_log('Database connection failed: ' . $exception->getMessage());
    http_response_code(503);
    exit('<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Setup required</title><body style="font-family:Arial,sans-serif;background:#f5f3ef;padding:3rem;color:#20201e"><h1>Chico\'s Barbershop is getting ready</h1><p>The application could not connect to its database. Import <strong>database/barbershop_db.sql</strong> using phpMyAdmin, then check the credentials in <strong>config/database.php</strong>.</p></body></html>');
}
