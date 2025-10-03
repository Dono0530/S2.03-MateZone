<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['file']) && isset($_POST['msg'])) {
    $file = $_POST['file'];
    $msg = $_POST['msg'];
    $filePath = "/var/www/html/data/" . basename($file); // Utiliser un chemin absolu

    try {
        // Vérifier si le fichier existe, sinon le créer
        if (!file_exists($filePath)) {
            file_put_contents($filePath, "=== Début de la conversation ===\n");
        }

        // Ajouter le message au fichier
        file_put_contents($filePath, $msg . PHP_EOL, FILE_APPEND);
        echo "Message ajouté avec succès.";
    } catch (Exception $e) {
        echo "Erreur : " . $e->getMessage();
    }
} else {
    echo "Erreur : paramètres manquants.";
}
?>