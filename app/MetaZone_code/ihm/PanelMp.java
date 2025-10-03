package MateZone.ihm;

import MateZone.serveur.RemoteFileHandler;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Panel pour afficher les messages privés entre deux utilisateurs.
 */
public class PanelMp extends JPanel {
    /*-------------------------------*/
    /* Attributs                     */
    /*-------------------------------*/

    /** Zone de texte pour afficher les messages privés. */
    private JTextPane txtMessages;
    /** Nom du fichier contenant les messages privés. */
    private String fileName;
    /** Adresse de l'hôte du serveur. */
    private String host;
    /** Port du serveur. */
    private int port;
    private Timer refreshTimer; // Timer pour le rafraîchissement automatique

    /**
     * Constructeur de la classe PanelMp.
     *
     * @param fileName Le nom du fichier contenant les messages privés.
     * @param host     L'adresse de l'hôte du serveur.
     * @param port     Le port du serveur.
     */
    public PanelMp(String fileName, String host, int port) {
        this.fileName = fileName;
        this.host = host;
        this.port = port;

        setLayout(new BorderLayout());
        this.txtMessages = new JTextPane();
        this.txtMessages.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.txtMessages);
        this.add(scrollPane, BorderLayout.CENTER);

        // Charger les messages initiaux
        loadMessages();
        startAutoRefresh(); // Démarrer le rafraîchissement automatique
    }

    /**
     * Charge les messages depuis un fichier distant et local.
     * Cette méthode télécharge le fichier contenant les messages depuis un serveur
     * distant, puis lit son contenu pour afficher les messages dans l'interface.
     */
    public synchronized void loadMessages() 
    {
        synchronized (this) 
        {
            try {
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
                    this.txtMessages.setText("Le fichier de messages n'existe pas encore.");
                    return;
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
                {
                    this.txtMessages.setText(""); // Effacer les messages existants
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":", 2);
                        if (parts.length == 2) { addMessage(parts[0], parts[1]); }
                    }
                }
            } catch (IOException e) 
            {
                this.txtMessages.setText("Erreur lors du chargement des messages : " + e.getMessage());
                System.err.println("Erreur lors du chargement des messages : " + e.getMessage());
            }
        }
    }

    /**
     * Ajoute un message à l'affichage.
     * Cette méthode applique des styles spécifiques au pseudo et au contenu du message.
     *
     * @param pseudo  Le pseudo de l'utilisateur ayant envoyé le message.
     * @param message Le contenu du message.
     */
    private void addMessage(String pseudo, String message) {
        try {
            StyledDocument doc = this.txtMessages.getStyledDocument();
            SimpleAttributeSet pseudoStyle = new SimpleAttributeSet();

            // Appliquer des styles en fonction du pseudo
            if (pseudo.equalsIgnoreCase("Admin")) {
                StyleConstants.setForeground(pseudoStyle, Color.RED);
            } else {
                StyleConstants.setForeground(pseudoStyle, Color.BLUE);
            }
            StyleConstants.setBold(pseudoStyle, true);

            SimpleAttributeSet messageStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(messageStyle, Color.BLACK);

            doc.insertString(doc.getLength(), pseudo + ": ", pseudoStyle);
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
        } catch (BadLocationException e) {   e.printStackTrace(); }
    }

    /**
     * Démarre un rafraîchissement automatique des messages toutes les 0.5 secondes.
     */
    private void startAutoRefresh() {
        refreshTimer = new Timer(true); // Timer en mode daemon
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> loadMessages());
            }
        }, 0, 500); // Exécuter toutes les 500 ms
    }

    /**
     * Arrête le rafraîchissement automatique des messages.
     */
    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}
