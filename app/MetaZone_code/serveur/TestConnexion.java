package MateZone.serveur;

import java.io.IOException;
import java.net.Socket;

/*-------------------------------*/
/* Classe TestConnexion          */
/*-------------------------------*/

/**
 * Classe utilitaire pour tester la connexion à un serveur.
 * Permet de vérifier si un hôte et un port sont accessibles.
 */
public class TestConnexion
{
	/*-------------------------------*/
	/* Méthode : Tester la connexion */
	/*-------------------------------*/

	/**
	 * Tente d'établir une connexion à un serveur donné.
	 *
	 * @param host L'adresse de l'hôte du serveur.
	 * @param port Le port du serveur.
	 * @return {@code true} si la connexion réussit, {@code false} sinon.
	 */
	public boolean tentativeConnexion(String host, int port)
	{
		try (Socket socket = new Socket(host, port))
		{
			System.out.println("Connexion réussie à " + host + ":" + port);
			return true;
		}
		catch (IOException e)
		{
			System.err.println("Erreur de connexion à " + host + ":" + port + " - " + e.getMessage());
			return false;
		}
	}

	/*-------------------------------*/
	/* Méthode main (test unitaire)  */
	/*-------------------------------*/

	/**
	 * Méthode principale pour tester la connexion à un serveur.
	 * Permet de vérifier si un hôte et un port sont accessibles.
	 *
	 * @param args Arguments de la ligne de commande (non utilisés).
	 */
	public static void main(String[] args)
	{
		TestConnexion testConnexion = new TestConnexion();
		String host = "localhost"; // Remplace par l'hôte que tu veux tester
		int port = 8080;           // Remplace par le port que tu veux tester

		boolean resultat = testConnexion.tentativeConnexion(host, port);
		if (resultat) System.out.println("La connexion a réussi.");
		else          System.out.println("La connexion a échoué.");
	}
}
