package MateZone.serveur;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/*-------------------------------*/
/* Classe RemoteFileHandler      */
/*-------------------------------*/

/**
 * Classe utilitaire pour gérer les interactions avec le serveur.
 * Fournit des méthodes pour télécharger, uploader des fichiers et envoyer des messages.
 */
public class RemoteFileHandler
{
	/*---------------------------------------------------------*/
	/* Méthode : Télécharger un fichier depuis un serveur HTTP */
	/*---------------------------------------------------------*/

	/**
	 * Télécharge un fichier depuis un serveur HTTP et le sauvegarde localement.
	 *
	 * @param url L'URL du fichier distant.
	 * @param localPath Le chemin local où sauvegarder le fichier.
	 * @throws IOException En cas d'erreur lors du téléchargement ou de l'écriture du fichier.
	 */
	public static void downloadFile(String remoteUrl, String localPath) throws IOException {
	System.out.println("Téléchargement du fichier depuis : " + remoteUrl);
	try (InputStream in = URI.create(remoteUrl).toURL().openStream();
		 FileOutputStream out = new FileOutputStream(localPath)) {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
	} catch (IOException e) {
		System.err.println("Erreur lors du téléchargement : " + e.getMessage());
		throw e;
	}
	System.out.println("Fichier téléchargé avec succès : " + localPath);
}

	/*----------------------------------------------------*/
	/* Méthode : Uploader un fichier vers un serveur HTTP */
	/*----------------------------------------------------*/

	/**
	 * Upload un fichier vers un serveur HTTP.
	 *
	 * @param url L'URL du serveur où uploader le fichier.
	 * @param filePath Le chemin local du fichier à uploader.
	 * @throws IOException En cas d'erreur lors de l'upload.
	 */
	public static void uploadFile(String host, int port, String localFilePath, String remoteFileName) throws IOException {
	String urlStr = "http://" + host + ":" + port + "/data/upload_private_message.php";
	URL url = URI.create(urlStr).toURL();
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("POST");
	conn.setDoOutput(true);
	conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---Boundary");

	File file = new File(localFilePath);
	if (!file.exists()) {
		throw new FileNotFoundException("Le fichier local n'existe pas : " + localFilePath);
	}

	try (OutputStream os = conn.getOutputStream();
		 FileInputStream fis = new FileInputStream(file)) {
		String boundary = "---Boundary";
		String lineSeparator = "\r\n";

		// Début de la requête multipart
		os.write(("--" + boundary + lineSeparator).getBytes());
		os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + remoteFileName + "\"" + lineSeparator).getBytes());
		os.write(("Content-Type: application/octet-stream" + lineSeparator + lineSeparator).getBytes());

		// Écrire le contenu du fichier
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytesRead);
		}

		// Fin de la requête multipart
		os.write((lineSeparator + "--" + boundary + "--" + lineSeparator).getBytes());
	}

	// Lire la réponse du serveur
	int responseCode = conn.getResponseCode();
	if (responseCode != HttpURLConnection.HTTP_OK) {
		throw new IOException("Erreur lors de l'upload du fichier : Code " + responseCode);
	}

	try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
		String response = in.readLine();
		System.out.println("Réponse du serveur : " + response);
	}
}

	/*-----------------------------------------*/
	/* Méthode : Envoyer un message au serveur */
	/*-----------------------------------------*/

	/**
	 * Envoie un message privé au serveur et crée le fichier si nécessaire.
	 *
	 * @param host     L'adresse de l'hôte du serveur.
	 * @param port     Le port du serveur.
	 * @param user     L'utilisateur envoyant le message.
	 * @param message  Le message à envoyer.
	 * @param fileName Le nom du fichier de message privé.
	 * @throws IOException En cas d'erreur lors de la requête HTTP.
	 */
	public static void sendPrivateMessage(String host, int port, String user, String message, String fileName) throws IOException {
		String urlStr = "http://" + host + ":" + port + "/data/add_private_message.php";
		URL url = URI.create(urlStr).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		// Préparer les données POST
		String data = "file=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) +
					  "&msg=" + URLEncoder.encode(user + ": " + message, StandardCharsets.UTF_8);
		System.out.println("URL : " + urlStr);
		System.out.println("Data : " + data);

		try (OutputStream os = conn.getOutputStream()) {
			os.write(data.getBytes(StandardCharsets.UTF_8));
		}

		// Lire la réponse du serveur
		int responseCode = conn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException("Erreur lors de l'envoi du message : Code " + responseCode);
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			String response = in.readLine();
			System.out.println("Réponse du serveur : " + response);
		}
	}

	public static void sendMessageToServer(String host, int port, String user, String message, String fileName) throws IOException {
		sendPrivateMessage(host, port, user, message, fileName);
	}

	/**
	 * Appelle le script PHP save_client.php pour mettre à jour le fichier all_clients.data.
	 *
	 * @param host L'adresse de l'hôte du serveur.
	 * @param port Le port du serveur.
	 * @throws IOException En cas d'erreur lors de la requête HTTP.
	 */
	public static void updateAllClients(String host, int port) throws IOException {
		String urlStr = "http://" + host + ":" + port + "/data/save_client.php";
		URL url = URI.create(urlStr).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		// Préparer les données POST
		String data = "host=" + URLEncoder.encode(host, "UTF-8") + "&port=" + URLEncoder.encode(String.valueOf(port), "UTF-8");
		try (OutputStream os = conn.getOutputStream()) {
			os.write(data.getBytes());
		}

		// Lire la réponse du serveur
		int responseCode = conn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String response = in.readLine();
				System.out.println("Réponse du serveur : " + response);
			}
		} else {
			System.err.println("Erreur lors de l'appel à save_client.php : Code " + responseCode);
		}
	}
}