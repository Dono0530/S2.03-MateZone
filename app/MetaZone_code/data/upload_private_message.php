<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_FILES['file'])) {
    $file = $_FILES['file'];
    $uploadDir = "/var/www/html/data/";
    $uploadFile = $uploadDir . basename($file['name']);

    try {
        // Déplacer le fichier uploadé vers le dossier cible
        if (move_uploaded_file($file['tmp_name'], $uploadFile)) {
            echo "Fichier uploadé avec succès.";
        } else {
            throw new Exception("Erreur lors de l'upload du fichier.");
        }
    } catch (Exception $e) {
        echo "Erreur : " . $e->getMessage();
    }
} else {
    echo "Erreur : Aucun fichier reçu.";
}
?>