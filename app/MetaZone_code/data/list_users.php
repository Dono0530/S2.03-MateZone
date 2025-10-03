<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['pseudo'])) {
    $pseudo = $_POST['pseudo'];
    $lines = file('client.data', FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    $users = [];
    foreach ($lines as $line) {
        list($p, ) = explode(':', $line, 2);
        if (trim($p) !== trim($pseudo)) {
            $users[] = trim($p);
        }
    }
    echo json_encode($users);
} else {
    echo json_encode([]);
}
?>