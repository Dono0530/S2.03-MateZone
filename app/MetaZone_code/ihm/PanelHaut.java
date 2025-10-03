package MateZone.ihm;

import MateZone.ihm.FrameMP;
import MateZone.serveur.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Panel pour afficher la liste des utilisateurs connectés et gérer les interactions
 * telles que l'envoi de messages privés ou la sélection d'une image.
 */
public class PanelHaut extends JPanel implements ActionListener {
	/*-------------------------------*/
	/* Attributs                     */
	/*-------------------------------*/

	/** Bouton pour envoyer un message privé. */
	private JButton btnHello;
	/** Liste des utilisateurs connectés. */
	private JList<String> list;
	/** Label pour afficher l'image sélectionnée. */
	private JLabel lblImage;
	/** Modèle de données pour la liste des utilisateurs. */
	private DefaultListModel<String> listModel;
	/** Bouton pour sélectionner une image. */
	private JButton btnSelectImage;
	/** Chemin de l'image sélectionnée. */
	private String imagePath;

	/** Adresse de l'hôte du serveur. */
	private String host;
	/** Port du serveur. */
	private int port;
	/** Pseudo de l'utilisateur actuel. */
	private String pseudo;

	/** Timer pour rafraîchir la liste des utilisateurs. */
	private Timer refreshTimer;

	/**
	 * Constructeur de la classe PanelHaut.
	 *
	 * @param host   L'adresse de l'hôte du serveur.
	 * @param port   Le port du serveur.
	 * @param pseudo Le pseudo de l'utilisateur actuel.
	 */
	public PanelHaut(String host, int port, String pseudo) 
	{
		this.host = host;
		this.port = port;
		this.pseudo = pseudo;

		setLayout(new BorderLayout(5, 5));

		// Initialiser les composants
		this.btnHello = new JButton("Envoyer un mp");
		this.listModel = new DefaultListModel<>();
		this.list = new JList<>(listModel);
		this.lblImage = new JLabel("Aucune image sélectionnée", SwingConstants.CENTER);
		this.btnSelectImage = new JButton("Choisir une image");

		// Ajouter les composants au panel
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(this.btnHello, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(this.lblImage, BorderLayout.CENTER);
		rightPanel.add(this.btnSelectImage, BorderLayout.SOUTH);

		this.add(leftPanel, BorderLayout.WEST);
		this.add(new JScrollPane(this.list), BorderLayout.CENTER);
		this.add(rightPanel, BorderLayout.EAST);

		// Ajouter les listeners
		this.btnHello.addActionListener(this);
		this.btnSelectImage.addActionListener(this);

		// Charger les utilisateurs au démarrage
		loadUsers();

		// Charger l'image de l'utilisateur au démarrage
		loadUserImage();

		// Initialiser et démarrer le Timer pour rafraîchir la liste toutes les secondes
		refreshTimer = new Timer(1500, e -> loadUsers());
		refreshTimer.start();
	}

	/**
	 * Charge la liste des utilisateurs connectés depuis le serveur.
	 * Cette méthode met à jour la liste affichée dans l'interface.
	 */
	private void loadUsers() 
	{
		try 
		{
			List<String> users = Client.loadAutreClient(host, port, pseudo);
			listModel.clear(); // Efface les anciens éléments
			for (String user : users) 
				if (!listModel.contains(user)) { listModel.addElement(user); }
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Gère les événements déclenchés par les boutons.
	 *
	 * @param e L'événement d'action déclenché.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnHello) {
			// Envoyer un message privé
			String selectedItem = this.list.getSelectedValue();
			if (selectedItem != null) {
				new FrameMP(this.pseudo, selectedItem, this.host, this.port);
			} else {
				JOptionPane.showMessageDialog(this, "Aucun utilisateur sélectionné.", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
		} else if (e.getSource() == this.btnSelectImage) {
			// Sélectionner une image
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = fileChooser.showOpenDialog(this);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				this.imagePath = selectedFile.getAbsolutePath();

				// Copier l'image dans le dossier img avec le nom de l'utilisateur
				File imgDir = new File("img"); // Mettre à jour le chemin vers la racine
				if (!imgDir.exists()) {
					imgDir.mkdirs(); // Créer le dossier si nécessaire
				}

				File destinationFile = new File(imgDir, this.pseudo + getFileExtension(selectedFile));
				try {
					copyFile(selectedFile, destinationFile);
					JOptionPane.showMessageDialog(this, "Image copiée avec succès dans : " + destinationFile.getAbsolutePath());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, "Erreur lors de la copie de l'image : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}

				// Afficher l'image dans le label
				displayImage(destinationFile);
			}
		}
	}

	/**
	 * Affiche l'image dans le label `lblImage`.
	 *
	 * @param imageFile Le fichier image à afficher.
	 */
	private void displayImage(File imageFile) {
		ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
		Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		this.lblImage.setIcon(new ImageIcon(image));
		this.lblImage.setText("");
	}

	/**
	 * Vérifie si une image correspondant au pseudo existe et l'affiche.
	 */
	private void loadUserImage() {
		File imgDir = new File("img"); // Mettre à jour le chemin vers la racine
		File userImage = new File(imgDir, this.pseudo + ".jpg"); // Par défaut, extension .jpg
		if (userImage.exists()) {
			displayImage(userImage);
		}
	}

	/**
	 * Copie un fichier source vers un fichier destination.
	 *
	 * @param sourceFile      Le fichier source.
	 * @param destinationFile Le fichier destination.
	 * @throws IOException En cas d'erreur lors de la copie.
	 */
	private void copyFile(File sourceFile, File destinationFile) throws IOException {
		try (InputStream in = new FileInputStream(sourceFile);
			 OutputStream out = new FileOutputStream(destinationFile)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
	}

	/**
	 * Récupère l'extension d'un fichier.
	 *
	 * @param file Le fichier.
	 * @return L'extension du fichier (par exemple, ".jpg", ".png").
	 */
	private String getFileExtension(File file) {
		String name = file.getName();
		int lastIndex = name.lastIndexOf('.');
		return (lastIndex == -1) ? "" : name.substring(lastIndex);
	}
}