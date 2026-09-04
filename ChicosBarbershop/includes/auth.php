<?php
declare(strict_types=1);
require_once __DIR__ . '/../config/database.php';

if (session_status() !== PHP_SESSION_ACTIVE) {
    session_start();
}

function currentUser(): ?array {
    return $_SESSION['user'] ?? null;
}

function requireLogin(): void {
    if (!currentUser()) {
        header('Location: login.php');
        exit;
    }
}

function requireAdmin(): void {
    requireLogin();
    if ((currentUser()['role'] ?? '') !== 'admin') {
        http_response_code(403);
        exit('Forbidden');
    }
}
