
package Ventanas;

/**
 *
 * @author alehe
 */
import javax.swing.SwingUtilities;

public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}
