package MateZone.ihm;

import MateZone.serveur.RemoteFileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/*-------------------------------*/
/* Classe PanelEnvoyer           */
/*-------------------------------*/

/**
 * Classe représentant le panneau d'envoi de messages.
 * Ce panneau permet à l'utilisateur de saisir et d'envoyer des messages au serveur.
 */
public class PanelEnvoyer extends JPanel implements ActionListener
{
	/*--------------------*/
	/* Attributs          */
	/*--------------------*/

	/** Champ de texte pour saisir le message. */
	private JTextField txtMessage;

	/** Bouton pour envoyer le message. */
	private JButton btnEnvoyer;

	/** Référence au panneau des messages pour recharger les messages après envoi. */
	private PanelMessage panelMessage;

	/** Pseudo de l'utilisateur connecté. */
	private String pseudo;

	/** Adresse de l'hôte du serveur. */
	private String host;

	/** Port du serveur. */
	private int port;

	/*--------------------*/
	/* Constructeur       */
	/*--------------------*/

	/**
	 * Constructeur de la classe PanelEnvoyer.
	 *
	 * @param panelMessage Référence au panneau des messages.
	 * @param pseudo Le pseudo de l'utilisateur connecté.
	 * @param host L'adresse de l'hôte du serveur.
	 * @param port Le port du serveur.
	 */
	public PanelEnvoyer(PanelMessage panelMessage, String pseudo, String host, int port)
	{
		this.panelMessage = panelMessage;
		this.pseudo = pseudo;
		this.host = host;
		this.port = port;

		setLayout(new BorderLayout());
 		/*----------------------------*/
		/* Configuration de la taille */
		/*----------------------------*/
		this.setPreferredSize(new Dimension(800, 50));

		/*---------------------------*/
		/* Création des composants   */
		/*---------------------------*/
		this.txtMessage = new JTextField();
		this.btnEnvoyer = new JButton("Envoyer");

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		this.add(this.txtMessage, BorderLayout.CENTER);
		this.add(this.btnEnvoyer, BorderLayout.EAST);

		/*---------------------------*/
		/* Activation des listeners  */
		/*---------------------------*/
		this.btnEnvoyer.addActionListener(this);
	}

	/*--------------------*/
	/* Méthodes           */
	/*--------------------*/

	/**
	 * Méthode appelée lors du clic sur le bouton "Envoyer".
	 * Envoie le message saisi au serveur et recharge les messages.
	 *
	 * @param e L'événement déclenché par le clic sur le bouton.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String message = this.txtMessage.getText().trim();
		if (message.isEmpty()) return;

		try 
		{
			RemoteFileHandler.sendPrivateMessage(host, port, pseudo, message, "message.data");
			this.txtMessage.setText("");
		} catch (IOException ex) 
		{
			JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi du message : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
