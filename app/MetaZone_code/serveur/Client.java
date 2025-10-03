package MateZone.serveur;

import java.io.*;
import java.net.*;
import java.util.List;

/*-------------------------------*/
/* Classe Client                 */
/*-------------------------------*/

/**
 * Classe représentant un client de l'application MateZone.
 * Cette classe permet de gérer les informations des clients et d'interagir avec le serveur.
 */
public class Client
{
	/*-------------------------------*/
	/* Attributs                     */
	/*-------------------------------*/

	/** Adresse de l'hôte du serveur. */
	private String host;

	/** Port du serveur. */
	private int port;

	/** Pseudo du client. */
	private String pseudo;

	/** Mot de passe du client. */
	private String mdp;

	/** Liste des autres utilisateurs. */
	private java.util.List<String> autreUser;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/

	/**
	 * Constructeur de la classe Client.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 * @param pseudo Pseudo du client.
	 * @param mdp Mot de passe du client.
	 */
	public Client(String host, int port, String pseudo, String mdp)
	{
		this.host = host;
		this.port = port;
		this.pseudo = pseudo;
		this.mdp = mdp;
	}

	/*-------------------------------*/
	/* Méthodes statiques (factory)  */
	/*-------------------------------*/

	/**
	 * Ajoute un nouveau client au serveur et retourne le statut.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 * @param pseudo Pseudo du client.
	 * @param mdp Mot de passe du client.
	 * @return Le statut de l'ajout ("OK", "EXISTS", ou "Erreur").
	 * @throws IOException En cas d'erreur de communication avec le serveur.
	 */
	public static String addClientOnServer(String host, int port, String pseudo, String mdp) throws IOException
	{
		String urlStr = "http://" + host + ":" + port + "/data/add_client.php";
		URL url = URI.create(urlStr).toURL(); // Updated to avoid deprecation warning
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		String data = "pseudo=" + URLEncoder.encode(pseudo, "UTF-8") + "&mdp=" + URLEncoder.encode(mdp, "UTF-8");
		try (OutputStream os = conn.getOutputStream()) {
			os.write(data.getBytes());
		}

		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				return in.readLine(); // "OK" ou "EXISTS"
			}
		}
		return "Erreur";
	}

	/**
	 * Vérifie si un client existe sur le serveur.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 * @param pseudo Pseudo du client.
	 * @param mdp Mot de passe du client.
	 * @return {@code true} si le client existe sur le serveur, {@code false} sinon.
	 * @throws IOException En cas d'erreur de communication avec le serveur.
	 */
	public static boolean checkClientOnServer(String host, int port, String pseudo, String mdp) throws IOException
	{
		String urlStr = "http://" + host + ":" + port + "/data/check_client.php";
		URL url = URI.create(urlStr).toURL(); // Updated to avoid deprecation warning
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		String data = "pseudo=" + URLEncoder.encode(pseudo, "UTF-8") + "&mdp=" + URLEncoder.encode(mdp, "UTF-8");
		try (OutputStream os = conn.getOutputStream()) {
			os.write(data.getBytes());
		}

		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String resp = in.readLine();
				return "OK".equals(resp);
			}
		}
		return false;
	}

	/**
	 * Vérifie si le mot de passe est valide.
	 *
	 * @param mdp Le mot de passe à vérifier.
	 * @return {@code true} si le mot de passe est valide, {@code false} sinon.
	 */
	public static boolean isPasswordValid(String mdp)
	{
		return mdp.length() >= 8;
	}

	/**
	 * Crée un nouveau client.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 * @param pseudo Pseudo du client.
	 * @param mdp Mot de passe du client.
	 * @return Une instance de {@code Client}.
	 */
	public static Client createClient(String host, int port, String pseudo, String mdp)
	{
		if (!isPasswordValid(mdp)) throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
		return new Client(host, port, pseudo, mdp);
	}

	/**
	 * Récupère la liste des autres utilisateurs.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 * @param pseudo Pseudo du client.
	 * @return Une liste des pseudos des autres utilisateurs.
	 * @throws IOException En cas d'erreur de communication avec le serveur.
	 */
	public static List<String> loadAutreClient(String host, int port, String pseudo) throws IOException 
	{
		// Construction de l'URL pour accéder à un script PHP sur un serveur
		String urlStr = "http://" + host + ":" + port + "/data/list_users.php";
		URL url = URI.create(urlStr).toURL(); // Updated to avoid deprecation warning

		// Ouverture d'une connexion HTTP à l'URL
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST"); // Définition de la méthode HTTP comme POST
		conn.setDoOutput(true); // Autorise l'envoi de données dans la requête

		// Préparation des données à envoyer dans la requête POST
		String data = "pseudo=" + URLEncoder.encode(pseudo, "UTF-8"); // Encodage du pseudo pour éviter les caractères spéciaux
		try (OutputStream os = conn.getOutputStream()) {
			os.write(data.getBytes()); // Envoi des données au serveur
		}

		// Liste pour stocker les utilisateurs récupérés
		List<String> users = new java.util.ArrayList<>();

		// Récupération du code de réponse HTTP
		int responseCode = conn.getResponseCode();
		if (responseCode == 200) // Si la réponse est OK (200)
		{ 
			// Lecture de la réponse du serveur
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) 
			{
				String resp = in.readLine(); // Lecture de la première ligne de la réponse
				if (resp != null && !resp.isEmpty()) // Si la réponse n'est pas vide
				{ 
					resp = resp.trim(); // Suppression des espaces inutiles
					if (resp.startsWith("[") && resp.endsWith("]")) // Vérifie si la réponse est un tableau JSON
					{ 
						resp = resp.substring(1, resp.length() - 1); // Supprime les crochets [ ] du tableau JSON
						for (String user : resp.split(","))  // Sépare les utilisateurs par des virgules
						{
							String u = user.replaceAll("\"", "").trim(); // Supprime les guillemets et les espaces
							if (!u.isEmpty()) users.add(u); // Ajoute l'utilisateur à la liste s'il n'est pas vide
						}
					}
				}
			}
		}

		// Affiche la liste des utilisateurs récupérés
		System.out.println("Liste des utilisateurs : " + users);

		// Retourne une liste vide si aucun utilisateur n'a été trouvé, sinon retourne la liste des utilisateurs
		return users.isEmpty() ? java.util.Collections.emptyList() : users;
	}

	/*-------------------------------*/
	/* Getters et Setters            */
	/*-------------------------------*/

	public String getHost  () { return host;   }
	public int    getPort  () { return port;   }
	public String getPseudo() { return pseudo; }
	public String getMdp   () { return mdp;    }

	public void setAutreUser(List<String> autreUser)
	{
		this.autreUser = autreUser;
	}
	public List<String> getAutreUser()
	{
		return autreUser;
	}
}
