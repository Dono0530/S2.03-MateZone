<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['pseudo']) && isset($_POST['mdp'])) {
    $pseudo = $_POST['pseudo'];
    $mdp = $_POST['mdp'];
    $lines = file('client.data', FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        list($p, $m) = explode(':', $line, 2);
        if (trim($p) === trim($pseudo) && trim($m) === trim($mdp)) {
            echo "OK";
            exit;
        }
    }
    echo "NO";
} else {
    echo "Erreur";
}
?>