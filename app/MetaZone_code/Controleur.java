package MateZone;

import MateZone.serveur.FrameClient;
import MateZone.serveur.ConnectionFrame;
import MateZone.ihm.FrameMateZone;
import MateZone.serveur.RemoteFileHandler;

import java.io.IOException;


/*-------------------------------*/
/* Classe Contreoleur            */
/*-------------------------------*/

/**
 * Classe principale de contrôle de l'application MateZone.
 * Gère la logique de connexion et le lancement des différentes interfaces utilisateur.
 */
public class Controleur
{
	/*--------------------------*/
	/*        Attributs         */
	/*--------------------------*/

	/** Adresse de l'hôte du serveur. */
	private String host;

	/** Port du serveur. */
	private int port;


	/*--------------------------*/
	/*     Conctructeur         */
	/*--------------------------*/
	/**
	 * Constructeur de la classe Controleur.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 */
	public Controleur(String host, int port)
	{
		this.host = host;
		this.port = port;
	}


	/*-------------------------------*/
	/* Auyrs méthodes                */
	/*-------------------------------*/
	/**
	 * Lance la fenêtre de connexion (ConnectionFrame).
	 * Télécharge les fichiers distants nécessaires avant de démarrer l'application.
	 */
	public void lancerConnectionFrame()
	{
		String remoteClientUrl = "http://" + host + ":" + port + "/data/client.data";
		String remoteMessageUrl = "http://" + host + ":" + port + "/data/message.data";

		try
		{
			RemoteFileHandler.downloadFile(remoteClientUrl, "data/client.data");
			RemoteFileHandler.downloadFile(remoteMessageUrl, "data/message.data");
		}
		catch (IOException e)
		{
			System.err.println("Erreur lors du téléchargement des fichiers distants : " + e.getMessage());
		}

		new ConnectionFrame(this);
	}

	/**
	 * Lance la fenêtre principale de l'application (FrameClient).
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 */
	public void lancerFrameMateZone(String host, int port)
	{
		new FrameClient(host, port, this);
	}

	/**
	 * Affiche un message de confirmation de connexion dans la console.
	 *
	 * @param pseudo Pseudo de l'utilisateur connecté.
	 * @param frameClient Instance de la fenêtre FrameClient.
	 */
	public void afficherMessageConnexion(String pseudo, FrameClient frameClient)
	{
		System.out.println("Connexion réussie pour l'utilisateur : " + pseudo);
	}


	/*-------------------------------*/
	/*           MAIN                */
	/*-------------------------------*/
	/**
	 * Méthode principale pour lancer l'application.
	 * Les arguments permettent de spécifier l'hôte et le port du serveur.
	 *
	 * @param args Arguments de la ligne de commande (hôte et port).
	 */
	public static void main(String[] args)
	{
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 80;

		Controleur controleur = new Controleur(host, port);
		controleur.lancerConnectionFrame();
	}
}
