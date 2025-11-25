package Ventanas;

/**
 *
 * @author alehe
 */
import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    private JButton btnMedicos, btnPacientes, btnDiagnostico, btnEnfermeros, btnSalir;

    public Menu() {
        setTitle("Menú Principal");
        setSize(400,220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        btnMedicos = new JButton("Médicos");
        btnPacientes = new JButton("Pacientes");
        btnDiagnostico = new JButton("Diagnóstico de paciente");
        btnEnfermeros = new JButton("Enfermeros");
        btnSalir = new JButton("Salir");

        JPanel p = new JPanel(new GridLayout(5,1,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(btnMedicos);
        p.add(btnPacientes);
        p.add(btnDiagnostico);
        p.add(btnEnfermeros);
        p.add(btnSalir);

        add(p);

        btnMedicos.addActionListener(e -> {
            new MedicosF().setVisible(true);
            this.dispose();
        });
        btnEnfermeros.addActionListener(e -> {
            new EnfermerosF().setVisible(true);
            this.dispose();
        });
        btnPacientes.addActionListener(e -> {
            new PacienteF().setVisible(true);
            this.dispose();
        });
        btnDiagnostico.addActionListener(e -> {
            new DiagnosticoF().setVisible(true);
            this.dispose();
        });
        btnSalir.addActionListener(e -> {
            System.exit(0);
        });
    }
}
