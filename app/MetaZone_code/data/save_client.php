<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['host']) && isset($_POST['port'])) {
    $host = $_POST['host'];
    $port = $_POST['port'];
    $remoteFile = "http://$host:$port/data/client.data";
    $localFile = "all_clients.data";

    try {
        // Télécharger le fichier client.data depuis le serveur
        $remoteData = file_get_contents($remoteFile);
        if ($remoteData === false) {
            throw new Exception("Impossible de télécharger le fichier distant.");
        }

        // Charger les données locales existantes
        $localData = file_exists($localFile) ? file($localFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES) : [];

        // Charger les données distantes
        $remoteLines = explode(PHP_EOL, $remoteData);
        $remoteLines = array_filter($remoteLines, 'trim'); // Supprimer les lignes vides

        // Fusionner les données sans doublons
        $mergedData = array_unique(array_merge($localData, $remoteLines));

        // Vérifier si des données ont été ajoutées
        if (count($mergedData) > count($localData)) {
            // Sauvegarder les données fusionnées dans le fichier local
            file_put_contents($localFile, implode(PHP_EOL, $mergedData));
            echo "Les clients ont été sauvegardés avec succès.";
        } else {
            echo "Aucun nouveau client à ajouter.";
        }
    } catch (Exception $e) {
        echo "Erreur : " . $e->getMessage();
    }
} else {
    echo "Erreur : paramètres 'host' et 'port' manquants.";
}
?>