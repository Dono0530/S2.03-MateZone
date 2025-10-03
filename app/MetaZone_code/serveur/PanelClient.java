package MateZone.serveur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/*-------------------------------*/
/* Classe PanelClient            */
/*-------------------------------*/

/**
 * Classe représentant le panneau client de l'application MateZone.
 * Ce panneau permet à l'utilisateur de se connecter ou de créer un compte.
 */
public class PanelClient extends JPanel implements ActionListener 
{
	/*--------------------------*/
	/*        Attributs         */
	/*--------------------------*/

	/** Champ de texte pour saisir le pseudo de l'utilisateur. */
	private JTextField txtPseudo;

	/** Champ de texte pour saisir le mot de passe de l'utilisateur. */
	private JPasswordField txtMdp;

	/** Bouton pour se connecter. */
	private JButton btnConnect;

	/** Bouton pour créer un compte. */
	private JButton btnCreate;

	/** Zone de texte pour afficher les messages de sortie (logs). */
	private JTextArea txtOutput;

	/** Adresse de l'hôte du serveur. */
	private String host;

	/** Port du serveur. */
	private int port;

	/*--------------------------*/
	/*        Constructeur      */
	/*--------------------------*/

	/**
	 * Constructeur de la classe PanelClient.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 */
	public PanelClient(String host, int port) 
	{
		this.host = host;
		this.port = port;

		setLayout(new BorderLayout());

		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/
		JPanel panelSaisie = new JPanel(new GridLayout(4, 2));
		
		this.txtPseudo     = new JTextField();
		this.txtMdp        = new JPasswordField();

		this.btnConnect    = new JButton("Se connecter");
		this.btnCreate     = new JButton("Créer un compte");

		this.txtOutput     = new JTextArea();
		this.txtOutput.setEditable(false);

		/*--------------------------------*/
		/*  Positionnement des composants */
		/*--------------------------------*/
		panelSaisie.add(new JLabel("Pseudo:"));
		panelSaisie.add(this.txtPseudo);

		panelSaisie.add(new JLabel("Mot de passe:"));
		panelSaisie.add(this.txtMdp);

		panelSaisie.add(this.btnConnect);
		panelSaisie.add(this.btnCreate);

		this.add(panelSaisie, BorderLayout.NORTH);
		this.add(new JScrollPane(this.txtOutput), BorderLayout.CENTER);

		/*-----------------------------*/
		/*  Activation des composants  */
		/*-----------------------------*/
		this.btnConnect.addActionListener(this);
		this.btnCreate.addActionListener(this);
	}

	/*--------------------------*/
	/*        Méthodes          */
	/*--------------------------*/

	/**
	 * Méthode appelée lors du clic sur un bouton (se connecter ou créer un compte).
	 * Gère la logique de connexion ou de création de compte.
	 *
	 * @param e L'événement déclenché par le clic sur un bouton.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String pseudo = this.txtPseudo.getText();
		String mdp = new String(this.txtMdp.getPassword());

		// Test de connexion
		if (e.getSource() == this.btnConnect) 
		{
			try 
			{
				if (Client.checkClientOnServer(host, port, pseudo, mdp)) {
					this.txtOutput.append("Connexion réussie pour : " + pseudo + "\n");
				} else {
					this.txtOutput.append("Échec de la connexion : utilisateur ou mot de passe incorrect.\n");
				}
			} 
			catch (IOException ex) { this.txtOutput.append("Erreur : " + ex.getMessage() + "\n"); }
		} 
		// Création de compte
		else if (e.getSource() == this.btnCreate) 
		{
			try 
			{
				String result = Client.addClientOnServer(host, port, pseudo, mdp);
				if ("OK".equals(result)) {
					this.txtOutput.append("Compte créé avec succès pour : " + pseudo + "\n");
				} else if ("EXISTS".equals(result)) {
					this.txtOutput.append("Le pseudo existe déjà. Veuillez en choisir un autre.\n");
				} else {
					this.txtOutput.append("Erreur lors de la création du compte.\n");
				}
			} 
			catch (IOException ex) { this.txtOutput.append("Erreur : " + ex.getMessage() + "\n");}
		}
	}
}