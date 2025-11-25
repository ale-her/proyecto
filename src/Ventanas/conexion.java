
package Ventanas;

/**
 *
 * @author alehe
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/clinica";
    private static final String USER = "root"; 
    private static final String PASS = "";    
    
    public static Connection getConnection() throws SQLException {
        // Driver moderno carga automático; opcional:
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Añade el connector al classpath.");
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
