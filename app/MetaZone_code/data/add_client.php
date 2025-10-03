<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['pseudo']) && isset($_POST['mdp'])) {
    $pseudo = $_POST['pseudo'];
    $mdp = $_POST['mdp'];
    $lines = file('client.data', FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        list($p, ) = explode(':', $line, 2);
        if ($p === $pseudo) {
            echo "EXISTS";
            exit;
        }
    }
    $filename = 'client.data';
    $entry = $pseudo . ":" . $mdp;
    if (filesize($filename) > 0) {
        file_put_contents($filename, PHP_EOL . $entry, FILE_APPEND);
    } else {
        file_put_contents($filename, $entry, FILE_APPEND);
    }
    echo "OK";
} else {
    echo "Erreur";
}
?>