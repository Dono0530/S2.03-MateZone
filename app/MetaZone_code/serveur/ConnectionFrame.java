package MateZone.serveur;

import MateZone.Controleur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*-------------------------------*/
/* ConnectionFrame               */
/*-------------------------------*/

/**
 * Classe représentant la fenêtre de connexion de l'application MateZone.
 * Cette fenêtre permet à l'utilisateur de saisir l'adresse de l'hôte et le port
 * pour établir une connexion avec le serveur.
 */
public class ConnectionFrame extends JFrame implements ActionListener
{
	/*-------------------------------*/
	/* Attributs                     */
	/*-------------------------------*/

	/** Champ de texte pour saisir l'adresse de l'hôte. */
	private JTextField txtHost;

	/** Champ de texte pour saisir le port du serveur. */
	private JTextField txtPort;

	/** Bouton pour établir la connexion. */
	private JButton btnConnect;

	/** Zone de texte pour afficher les messages de sortie (logs). */
	private JTextArea txtOutput;

	/** Instance du contrôleur pour gérer la logique de l'application. */
	private Controleur controleur;

	/*-------------------------------*/
	/* Constructeur                  */
	/*-------------------------------*/

	/**
	 * Constructeur de la classe ConnectionFrame.
	 *
	 * @param controleur Instance du contrôleur pour gérer la logique de l'application.
	 */
	public ConnectionFrame(Controleur controleur)
	{
		this.controleur = controleur;

		setTitle("Connection Frame");
		setSize(400, 300);
		setLayout(new BorderLayout());

		/*-------------------------------*/
		/* Création des composants       */
		/*-------------------------------*/
		JPanel panelSaisie = new JPanel();
		panelSaisie.setLayout(new GridLayout(3, 2));

		this.txtHost = new JTextField();
		this.txtPort = new JTextField();
		this.btnConnect = new JButton("Se connecter");
		this.txtOutput = new JTextArea();
		this.txtOutput.setEditable(false);

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panelSaisie.add(new JLabel("Host:"));
		panelSaisie.add(this.txtHost);

		panelSaisie.add(new JLabel("Port:"));
		panelSaisie.add(this.txtPort);

		panelSaisie.add(this.btnConnect);

		this.add(panelSaisie, BorderLayout.NORTH);
		this.add(new JScrollPane(this.txtOutput), BorderLayout.CENTER);

		/*-------------------------------*/
		/* Activation des listeners      */
		/*-------------------------------*/
		this.btnConnect.addActionListener(this);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		this.setVisible(true);
	}

	/*-------------------------------*/
	/* Méthodes                      */
	/*-------------------------------*/

	/**
	 * Méthode appelée lors du clic sur le bouton "Se connecter".
	 * Tente d'établir une connexion avec le serveur en utilisant les informations saisies.
	 *
	 * @param e L'événement déclenché par le clic sur le bouton.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String host = this.txtHost.getText();
		String port = this.txtPort.getText();

		try
		{
			TestConnexion test = new TestConnexion();
			if (test.tentativeConnexion(host, Integer.parseInt(port)))
			{
				this.txtOutput.append("Connexion réussie !\n");
				controleur = new Controleur(host, Integer.parseInt(port)); // Initialiser le Controleur avec host et port
				controleur.lancerFrameMateZone(host, Integer.parseInt(port)); // Appeler le Controleur
				this.dispose(); // Fermer la ConnectionFrame
			}
			else { this.txtOutput.append("Échec de la connexion.\n");  }
		}
		catch (Exception ex) { this.txtOutput.append("Erreur : " + ex.getMessage() + "\n");  }
	}
}