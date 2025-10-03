package MateZone.ihm;

import MateZone.ihm.PanelMp;
import MateZone.ihm.PanelEnvoyerMP;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Classe représentant une fenêtre pour les messages privés entre deux utilisateurs.
 */
public class FrameMP extends JFrame {
    /*-------------------*/
    /* Attributs         */
    /*-------------------*/

    /** Panel pour afficher les messages privés. */
    private PanelMp panelMp;
    /** Panel pour envoyer de nouveaux messages privés. */
    private PanelEnvoyerMP panelEnvoyerMP;

    /**
     * Nettoie le nom d'utilisateur pour générer un nom de fichier valide.
     *
     * @param name Le nom d'utilisateur à nettoyer.
     * @return Le nom nettoyé.
     */
    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\.]", "_");
    }

    /**
     * Constructeur de la classe FrameMP.
     *
     * @param user1 Le premier utilisateur.
     * @param user2 Le deuxième utilisateur.
     * @param host  L'adresse de l'hôte du serveur.
     * @param port  Le port du serveur.
     */
    public FrameMP(String user1, String user2, String host, int port) {
        setTitle("Messages Privés : " + user1 + " & " + user2);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10)); // Espacement global entre les composants

        // Nettoyer les noms d'utilisateur avant de générer le nom du fichier
        String sanitizedUser1 = sanitizeFileName(user1);
        String sanitizedUser2 = sanitizeFileName(user2);

        // Déterminer le nom du fichier pour les messages privés
        String fileName = (sanitizedUser1.compareToIgnoreCase(sanitizedUser2) < 0 ? sanitizedUser1 : sanitizedUser2) + "_" +
                          (sanitizedUser1.compareToIgnoreCase(sanitizedUser2) < 0 ? sanitizedUser2 : sanitizedUser1) + ".data";

        System.out.println("Nom du fichier généré pour les MP : " + fileName);

        // Initialiser les panels
        this.panelMp = new PanelMp(fileName, host, port);
        this.panelEnvoyerMP = new PanelEnvoyerMP(fileName, user1, host, port);

        // Ajouter des marges aux panels
        this.panelMp.setBorder(new EmptyBorder(10, 10, 10, 10)); // Marges internes
        this.panelEnvoyerMP.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Ajouter les panels à la fenêtre
        this.add(this.panelMp, BorderLayout.CENTER);
        this.add(this.panelEnvoyerMP, BorderLayout.SOUTH);

        // Centrer la fenêtre sur l'écran
        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Méthode pour fermer la fenêtre et arrêter le rafraîchissement automatique des messages.
     */
    @Override
    public void dispose() {
        // Arrêter le rafraîchissement automatique des messages
        if (this.panelMp != null) {
            this.panelMp.stopAutoRefresh();
        }
        super.dispose();
    }
}
