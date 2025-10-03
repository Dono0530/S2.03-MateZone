<?php
// filepath: /app/MetaZone_code/data/add_message.php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['msg'])) {
    file_put_contents('message.data', $_POST['msg'] . PHP_EOL, FILE_APPEND);
    echo "OK";
} else {
    echo "Erreur";
}
?>