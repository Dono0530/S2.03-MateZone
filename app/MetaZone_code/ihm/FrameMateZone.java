package MateZone.ihm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*-------------------------------*/
/* Classe FrameMateZone          */
/*-------------------------------*/

/**
 * Classe représentant la fenêtre principale de l'application MateZone.
 * Cette fenêtre contient les composants pour afficher les messages et envoyer de nouveaux messages.
 */
public class FrameMateZone extends JFrame {
	/** Panel pour afficher les messages. */
	private PanelMessage panelMessage;

	/** Panel pour envoyer de nouveaux messages. */
	private PanelEnvoyer panelEnvoyer;

	/** Panel en haut avec image et changement de file */
	private PanelHaut panelHaut;

	/** Bouton pour ouvrir une nouvelle fenêtre avec PanelMP. */
	private JButton btnOpenMP;

	/**
	 * Constructeur de la classe FrameMateZone.
	 *
	 * @param pseudo Le pseudo de l'utilisateur connecté.
	 * @param host L'adresse de l'hôte du serveur.
	 * @param port Le port du serveur.
	 */
	public FrameMateZone(String pseudo, String host, int port) {
		this.setTitle("MateZone - Application Principale");
		this.setSize(1000, 800); // Définir une taille personnalisée pour la fenêtre
		this.setLayout(new BorderLayout(10, 10)); // Espacement entre les composants

		// Création des panels
		this.panelMessage = new PanelMessage(pseudo, host, port);
		this.panelEnvoyer = new PanelEnvoyer(this.panelMessage, pseudo, host, port);
		this.panelHaut = new PanelHaut(host, port, pseudo);

		// Ajouter des marges aux panels
		this.panelMessage.setBorder(new EmptyBorder(10, 10, 10, 10)); // Marges internes
		this.panelEnvoyer.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.panelHaut.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Ajout des panels à la frame
		this.add(this.panelMessage, BorderLayout.CENTER);
		this.add(this.panelEnvoyer, BorderLayout.SOUTH);
		this.add(this.panelHaut, BorderLayout.NORTH);

		// Centrer la fenêtre sur l'écran
		this.setLocationRelativeTo(null);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * Ouvre une nouvelle fenêtre contenant un PanelMP.
	 *
	 * @param pseudo Le pseudo de l'utilisateur connecté.
	 * @param host L'adresse de l'hôte du serveur.
	 * @param port Le port du serveur.
	 */
	private void openFrameMP(String pseudo, String host, int port) {
		System.out.println("openFrameMP called"); // Débogage
		JFrame frameMP = new JFrame("Messages Privés");
		frameMP.setSize(800, 600); // Taille personnalisée pour la nouvelle fenêtre
		frameMP.setLayout(new BorderLayout());

		JLabel label = new JLabel("Fenêtre Messages Privés", SwingConstants.CENTER);
		frameMP.add(label, BorderLayout.CENTER);

		frameMP.setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
		frameMP.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme uniquement cette fenêtre
		frameMP.setVisible(true); // Affiche la fenêtre
	}
}