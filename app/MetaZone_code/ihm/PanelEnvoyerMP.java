package MateZone.ihm;

import MateZone.serveur.RemoteFileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Panel pour envoyer des messages privés.
 */

public class PanelEnvoyerMP extends JPanel implements ActionListener 
{
	/*-------------------------------*/
	/* Attributs                     */
	/*-------------------------------*/

	/** Champ de texte pour saisir le message. */
	private JTextField txtMessage;
	/** Bouton pour envoyer le message. */
	private JButton btnEnvoyer;
	/** Nom du fichier contenant les messages privés. */
	private String fileName;
	/** Pseudo de l'utilisateur. */
	private String user;
	/** Adresse de l'hôte du serveur. */
	private String host;
	/** Port du serveur. */
	private int port;

	/**
	 * Constructeur de la classe PanelEnvoyerMP.
	 *
	 * @param fileName Le nom du fichier contenant les messages privés.
	 * @param user     Le pseudo de l'utilisateur envoyant le message.
	 * @param host     L'adresse de l'hôte du serveur.
	 * @param port     Le port du serveur.
	 */
	public PanelEnvoyerMP(String fileName, String user, String host, int port) {
		this.fileName = fileName;
		this.user = user;
		this.host = host;
		this.port = port;

		setLayout(new BorderLayout());
		this.txtMessage = new JTextField();
		this.btnEnvoyer = new JButton("Envoyer");

		this.add(this.txtMessage, BorderLayout.CENTER);
		this.add(this.btnEnvoyer, BorderLayout.EAST);

		this.btnEnvoyer.addActionListener(this);
	}

	/**
	 * Gère l'événement déclenché lorsque le bouton "Envoyer" est cliqué.
	 * Cette méthode sauvegarde le message localement dans un fichier, puis
	 * tente de l'uploader sur un serveur distant.
	 *
	 * @param e L'événement d'action déclenché.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String message = this.txtMessage.getText().trim();
		if (message.isEmpty()) 
		{
			System.out.println("Aucun message à envoyer.");
			return;
		}

		try 
		{
			// Sauvegarder le message localement
			File file = new File("data/" + fileName);
			boolean isNewFile = file.createNewFile(); // Crée le fichier s'il n'existe pas
			System.out.println("Fichier local : " + file.getAbsolutePath() + " (Nouveau fichier : " + isNewFile + ")");
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) 
			{
				if (isNewFile) 
				{
					writer.write("=== Début de la conversation ===\n");
				}
				writer.write(user + ": " + message);
				writer.newLine();
			}
			System.out.println("Message sauvegardé localement : " + message);

			// Uploader le fichier sur le serveur
			RemoteFileHandler.uploadFile(host, port, "data/" + fileName, fileName);
			System.out.println("Fichier uploadé sur le serveur : " + fileName);

			this.txtMessage.setText("");
		} catch (IOException ex) 
		{
			System.err.println("Erreur lors de l'envoi du message : " + ex.getMessage());
			JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi du message : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Charge les messages depuis un fichier distant et les affiche dans la console.
	 * Cette méthode télécharge le fichier contenant les messages depuis un serveur
	 * distant, puis lit son contenu pour afficher les messages.
	 */
	public void loadMessages() 
	{
		synchronized (this) 
		{
			try 
			{
				// Télécharger le fichier distant
				String remoteUrl = "http://" + host + ":" + port + "/data/" + fileName;
				String localPath = "data/" + fileName;
				System.out.println("Tentative de téléchargement depuis : " + remoteUrl);

				RemoteFileHandler.downloadFile(remoteUrl, localPath);
				System.out.println("Fichier téléchargé avec succès : " + localPath);

				// Charger les messages dans l'interface
				File file = new File(localPath);
				if (!file.exists()) 
				{
					System.out.println("Le fichier de messages n'existe pas encore.");
					return;
				}

				try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
				{
					String line;
					while ((line = reader.readLine()) != null) 
					{
						System.out.println("Message lu : " + line);
					}
				}
			} catch (IOException e) { System.err.println("Erreur lors du chargement des messages : " + e.getMessage()); }
		}
	}
}
