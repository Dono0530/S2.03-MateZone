package MateZone.ihm;

import MateZone.serveur.RemoteFileHandler;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.*;

/*-------------------------------*/
/* Classe PanelMessage           */
/*-------------------------------*/

/**
 * Classe représentant le panneau d'affichage des messages.
 * Ce panneau permet de charger et d'afficher les messages provenant du serveur ou des fichiers locaux.
 */
public class PanelMessage extends JPanel
{
	/*-------------------------------*/
	/* Attributs                     */
	/*-------------------------------*/

	/** Zone de texte pour afficher les messages. */
	private JTextPane txtMessages;

	/** URL distante pour télécharger les messages. */
	private String remoteUrl;

	/** Verrou pour synchroniser l'accès aux fichiers. */
	public static final Object fileLock = new Object();

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/

	/**
	 * Constructeur de la classe PanelMessage.
	 *
	 * @param pseudo Le pseudo de l'utilisateur connecté.
	 * @param host L'adresse de l'hôte du serveur.
	 * @param port Le port du serveur.
	 */
	public PanelMessage(String pseudo, String host, int port)
	{
		setLayout(new BorderLayout());
		this.txtMessages = new JTextPane();
		this.txtMessages.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(this.txtMessages);
		this.add(scrollPane, BorderLayout.CENTER);

		// Construire dynamiquement l'URL
		this.remoteUrl = "http://" + host + ":" + port + "/data/message.data";

		// Rafraîchissement automatique des messages
		Timer timer = new Timer(2000, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				loadMessages(pseudo);
			}
		});
		timer.start();
	}

	/*-------------------------------*/
	/* Méthode de chargement         */
	/*-------------------------------*/

	/**
	 * Charge les messages depuis le serveur ou les fichiers locaux.
	 *
	 * @param pseudo Le pseudo de l'utilisateur connecté.
	 */
	public synchronized void loadMessages(String pseudo)
	{
		synchronized (fileLock)
		{
			// Fusionner les messages locaux et distants
			mergeMessages();

			// Télécharger les messages depuis le serveur
			try
			{
				System.out.println("Tentative de téléchargement depuis : " + remoteUrl);
				RemoteFileHandler.downloadFile(remoteUrl, "data/message.data");
			}
			catch (IOException e)
			{
				addMessage("Erreur", "Impossible de télécharger les messages depuis le serveur : " + e.getMessage(), pseudo);
				return;
			}

			// Charger les messages dans l'interface
			File file = new File("data/message.data");
			if (!file.exists()) return;

			try (BufferedReader reader = new BufferedReader(new FileReader(file)))
			{
				String line;
				this.txtMessages.setText(""); // Effacer les messages existants
				while ((line = reader.readLine()) != null)
				{
					String[] parts = line.split(":", 2);
					if (parts.length == 2) addMessage(parts[0], parts[1], pseudo);
				}
			}
			catch (IOException e)
			{
				addMessage("Erreur", "Erreur lors du chargement des messages : " + e.getMessage(), pseudo);
			}
		}
	}

	/*----------------------------------*/
	/* Ajout d'un message à l'affichage */
	/*----------------------------------*/

	/**
	 * Ajoute un message à l'affichage.
	 *
	 * @param pseudo Le pseudo de l'utilisateur ayant envoyé le message.
	 * @param message Le contenu du message.
	 * @param currentUser Le pseudo de l'utilisateur connecté.
	 */
	public void addMessage(String pseudo, String message, String currentUser)
	{
		try
		{
			StyledDocument doc = this.txtMessages.getStyledDocument();
			SimpleAttributeSet pseudoStyle = new SimpleAttributeSet();

			if (pseudo.equalsIgnoreCase(currentUser))
			{
				StyleConstants.setForeground(pseudoStyle, Color.GREEN);
			}
			else if (pseudo.equalsIgnoreCase("Admin"))
			{
				StyleConstants.setForeground(pseudoStyle, Color.RED);
			}
			else
			{
				StyleConstants.setForeground(pseudoStyle, Color.BLUE);
			}
			StyleConstants.setBold(pseudoStyle, true);

			SimpleAttributeSet messageStyle = new SimpleAttributeSet();
			StyleConstants.setForeground(messageStyle, Color.BLACK);

			doc.insertString(doc.getLength(), pseudo + ": ", pseudoStyle);
			doc.insertString(doc.getLength(), message + "\n", messageStyle);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	/*-------------------------------*/
	/* Surveillance du fichier local (optionnel) */
	/*-------------------------------*/

	/**
	 * Démarre la surveillance du fichier local pour détecter les modifications.
	 * Cette méthode est optionnelle et désactivée par défaut.
	 */
	private void startFileWatcher()
	{
		Path path = Paths.get("data");
		try
		{
			WatchService watchService = FileSystems.getDefault().newWatchService();
			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

			Executors.newSingleThreadExecutor().submit(() ->
			{
				while (true)
				{
					WatchKey key;
					try {  key = watchService.take(); }
					catch (InterruptedException e) {  return; }
					
					for (WatchEvent<?> event : key.pollEvents())
					{
						if (event.context().toString().equals("message.data"))
						{
							try { Thread.sleep(500); } catch (InterruptedException ignored) {}
							SwingUtilities.invokeLater(() -> loadMessages("currentUser")); // Remplacez "currentUser" par l'utilisateur actuel
						}
					}
					key.reset();
				}
			});
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	/*-------------------------------*/
	/* Getter pour le verrou         */
	/*-------------------------------*/

	/**
	 * Retourne le verrou utilisé pour synchroniser l'accès aux fichiers.
	 *
	 * @return L'objet de verrou.
	 */
	public static Object getFileLock() { return fileLock; }

	/*-------------------------------*/
	/* Fusion des messages locaux    */
	/*-------------------------------*/

	/**
	 * Fusionne les messages locaux et distants dans un seul fichier.
	 */
	private void mergeMessages()
	{
		synchronized (fileLock)
		{
			File mainFile = new File("data/message.data");
			File tempFile = new File("data/message_act.data");

			if (!tempFile.exists()) return; // Si aucun message local n'a été envoyé, rien à fusionner

			try (BufferedReader mainReader = new BufferedReader(new FileReader(mainFile));
				 BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
				 BufferedWriter writer = new BufferedWriter(new FileWriter(mainFile, true)))
			{
				// Lire les messages existants dans `message.data`
				Set<String> messages = new LinkedHashSet<>();
				String line;
				while ((line = mainReader.readLine()) != null)
				{
					messages.add(line);
				}

				// Ajouter les messages de `message_act.data`
				while ((line = tempReader.readLine()) != null)
				{
					if (!messages.contains(line)) // Éviter les doublons
					{
						writer.write(line);
						writer.newLine();
					}
				}
				// Supprimer le fichier temporaire après la fusion
				tempFile.delete();
			} catch (IOException e)  {  e.printStackTrace();  }
		}
	}
}