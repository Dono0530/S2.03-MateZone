package MateZone.serveur;

import MateZone.ihm.FrameMateZone;
import MateZone.serveur.Client;
import MateZone.Controleur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/*-------------------------------*/
/* Classe FrameClient            */
/*-------------------------------*/

/**
 * Classe représentant la fenêtre client de l'application MateZone.
 * Cette fenêtre permet à l'utilisateur de se connecter ou de créer un compte.
 */
public class FrameClient extends JFrame implements ActionListener 
{
	/*-------------------------------*/
	/* Attributs                     */
	/*-------------------------------*/
	/** Instance du contrôleur pour gérer la logique de l'application. */
	private Controleur controleur;

	/** Attribut pour stocker le client connecté */
	private Client client;

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


	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/

	/**
	 * Constructeur de la classe FrameClient.
	 *
	 * @param host Adresse de l'hôte du serveur.
	 * @param port Port du serveur.
	 * @param controleur Instance du contrôleur pour gérer la logique de l'application.
	 */
	public FrameClient(String host, int port, Controleur controleur) 
	{
		this.host = host;
		this.port = port;
		this.controleur = controleur;

		setTitle("Frame Client");
		setSize(400, 400);
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

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panelSaisie.add(new JLabel("Pseudo:"));
		panelSaisie.add(this.txtPseudo);

		panelSaisie.add(new JLabel("Mot de passe:"));
		panelSaisie.add(this.txtMdp);

		panelSaisie.add(this.btnConnect);
		panelSaisie.add(this.btnCreate);

		this.add(panelSaisie, BorderLayout.NORTH);
		this.add(new JScrollPane(this.txtOutput), BorderLayout.CENTER);

		/*-------------------------------*/
		/* Activation des listeners      */
		/*-------------------------------*/
		this.btnConnect.addActionListener(this);
		this.btnCreate.addActionListener(this);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		this.setVisible(true);
	}

	/*-------------------------------*/
	/* Méthodes                      */
	/*-------------------------------*/

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

		// Validation des champs de saisie
		if (!Client.isPasswordValid(mdp)) 
		{
			this.txtOutput.append("Erreur : Le mot de passe doit contenir au moins 8 caractères.\n");
			return;
		}

		// Vérification de la connexion ou de la création de compte
		if (e.getSource() == this.btnConnect) 
		{
			try 
			{
				// Après une connexion réussie
				if (Client.checkClientOnServer(host, port, pseudo, mdp)) 
				{
					this.txtOutput.append("Connexion réussie pour : " + pseudo + "\n");
					controleur.afficherMessageConnexion(pseudo, this);

					// Création du client et initialisation de la liste des autres utilisateurs
					Client client = new Client(host, port, pseudo, mdp);
					client.setAutreUser(Client.loadAutreClient(host, port, pseudo));

					// Passe ce client à ta fenêtre principale
					new FrameMateZone(pseudo, host, port);
					this.dispose();
				} 
				else 
				{
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
				if ("OK".equals(result)) 
				{
					this.txtOutput.append("Compte créé avec succès pour : " + pseudo + "\n");
					controleur.afficherMessageConnexion(pseudo, this);

					// Transition vers FrameMateZone
					new FrameMateZone(pseudo, host, port);
					this.dispose();
				} 
				else if ("EXISTS".equals(result)) 
				{
					this.txtOutput.append("Le pseudo existe déjà. Veuillez en choisir un autre.\n");
				}
				else 
				{
					this.txtOutput.append("Erreur lors de la création du compte.\n");
				}
			} 
			catch (IOException ex) { this.txtOutput.append("Erreur : " + ex.getMessage() + "\n"); }
		}
	}

	/**
	 * Récupère la liste des autres utilisateurs depuis le serveur et la stocke dans l'attribut du client.
	 * À appeler après la connexion ou lors d'un rafraîchissement.
	 */
	private void initListClient()
	{
		if (client != null)
		{
			try
			{
				client.setAutreUser(Client.loadAutreClient(client.getHost(), client.getPort(), client.getPseudo()));
			}
			catch (IOException ex)
			{	
				this.txtOutput.append("Erreur lors de la récupération des autres utilisateurs : " + ex.getMessage() + "\n");
			}
		}
	}

	/**
	 * Rafraîchit la liste des autres utilisateurs (fetch + traitement éventuel).
	 * À appeler pour mettre à jour la liste dans l'interface.
	 */
	private void majListClient()
	{
		initListClient();
		// Ici tu peux ajouter du code pour mettre à jour l'affichage si besoin
		// Par exemple : updateUserListDisplay();
	}
}