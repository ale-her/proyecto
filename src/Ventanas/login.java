package Ventanas;

/**
 *
 * @author alehe
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class login extends JFrame {
    private JTextField tfUser;
    private JPasswordField pfPass;
    private JButton btnLogin, btnSalir;

    public login() {
        setTitle("Login");
        setSize(320,180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        tfUser = new JTextField(15);
        pfPass = new JPasswordField(15);
        btnLogin = new JButton("Entrar");
        btnSalir = new JButton("Salir");

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Usuario:"), c);
        c.gridx = 1; p.add(tfUser, c);
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Contraseña:"), c);
        c.gridx = 1; p.add(pfPass, c);
        c.gridx = 0; c.gridy = 2; p.add(btnSalir, c);
        c.gridx = 1; p.add(btnLogin, c);

        add(p);

        btnSalir.addActionListener(e -> System.exit(0));
        btnLogin.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword()).trim();

        // verificar en BD
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // abrir menu
                    SwingUtilities.invokeLater(() -> {
                        new Menu().setVisible(true);
                    });
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}