package Ventanas;

/**
 *
 * @author alehe
 */
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class PacienteF  extends JFrame {
    private final JTextField tfCodigo = new JTextField(12);
    private final JTextField tfNombre = new JTextField(12);
    private final JTextField tfApellidos = new JTextField(12);
    private final JTextField tfDireccion = new JTextField(15);
    private final JTextField tfTelefono = new JTextField(12);
    private final JTextField tfFecha = new JTextField(10); // yyyy-MM-dd
    private final JTextField tfMedico = new JTextField(12);
    private final JTextField tfEnfermero = new JTextField(12);

    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnConsultar = new JButton("Consultar");
    private final JButton btnRegresar = new JButton("Regresar al menú");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Código Paciente","Nombre","Apellidos","Dirección","Teléfono","Fecha Nac","Código Médico","Código Enfermero"}, 0) {
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);

    public PacienteF() {
        setTitle("Pacientes");
        setSize(920,520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(4,4,4,4);

        c.gridx=0; c.gridy=0; top.add(new JLabel("Código Paciente:"), c); c.gridx=1; top.add(tfCodigo, c);
        c.gridx=2; top.add(new JLabel("Nombre:"), c); c.gridx=3; top.add(tfNombre, c);

        c.gridx=0; c.gridy=1; top.add(new JLabel("Apellidos:"), c); c.gridx=1; top.add(tfApellidos, c);
        c.gridx=2; top.add(new JLabel("Dirección:"), c); c.gridx=3; top.add(tfDireccion, c);

        c.gridx=0; c.gridy=2; top.add(new JLabel("Teléfono:"), c); c.gridx=1; top.add(tfTelefono, c);
        c.gridx=2; top.add(new JLabel("Fecha (yyyy-MM-dd):"), c); c.gridx=3; top.add(tfFecha, c);

        c.gridx=0; c.gridy=3; top.add(new JLabel("Código Médico:"), c); c.gridx=1; top.add(tfMedico, c);
        c.gridx=2; top.add(new JLabel("Código Enfermero:"), c); c.gridx=3; top.add(tfEnfermero, c);

        JPanel botones = new JPanel();
        botones.add(btnGuardar); botones.add(btnActualizar); botones.add(btnEliminar); botones.add(btnConsultar); botones.add(btnRegresar);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        // listeners
        btnRegresar.addActionListener(e -> { new Menu().setVisible(true); this.dispose(); });
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnConsultar.addActionListener(e -> consultar());

        table.getSelectionModel().addListSelectionListener((ListSelectionListener) ev -> {
            if (!ev.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                tfCodigo.setText(model.getValueAt(r,0).toString());
                tfNombre.setText(model.getValueAt(r,1).toString());
                tfApellidos.setText(model.getValueAt(r,2).toString());
                tfDireccion.setText(model.getValueAt(r,3).toString());
                tfTelefono.setText(model.getValueAt(r,4).toString());
                tfFecha.setText(model.getValueAt(r,5).toString());
                tfMedico.setText(model.getValueAt(r,6).toString());
                tfEnfermero.setText(model.getValueAt(r,7).toString());
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);
        String sql = "SELECT codigo_paciente, nombre_paciente, apellidos_paciente, direccion, telefono_paciente, fecha_nacimiento, codigo_medico, codigo_enfermero FROM pacientes";
        try (Connection c = conexion.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("codigo_paciente"));
                row.add(rs.getString("nombre_paciente"));
                row.add(rs.getString("apellidos_paciente"));
                row.add(rs.getString("direccion"));
                row.add(rs.getString("telefono_paciente"));
                Date d = rs.getDate("fecha_nacimiento");
                row.add(d != null ? d.toString() : "");
                row.add(rs.getString("codigo_medico"));
                row.add(rs.getString("codigo_enfermero"));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar pacientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private java.sql.Date parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            java.util.Date ud = new SimpleDateFormat("yyyy-MM-dd").parse(s);
            return new java.sql.Date(ud.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private void guardar() {
        String sql = "INSERT INTO pacientes (codigo_paciente, nombre_paciente, apellidos_paciente, direccion, telefono_paciente, fecha_nacimiento, codigo_medico, codigo_enfermero) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfCodigo.getText().trim());
            ps.setString(2, tfNombre.getText().trim());
            ps.setString(3, tfApellidos.getText().trim());
            ps.setString(4, tfDireccion.getText().trim());
            ps.setString(5, tfTelefono.getText().trim());
            ps.setDate(6, parseDate(tfFecha.getText().trim()));
            ps.setString(7, tfMedico.getText().trim());
            ps.setString(8, tfEnfermero.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guardado correctamente");
            loadTable();
            clearFields();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Error de integridad (clave duplicada o FK no existe): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        String codigo = tfCodigo.getText().trim();
        if (codigo.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el código a actualizar"); return; }
        String sql = "UPDATE pacientes SET nombre_paciente=?, apellidos_paciente=?, direccion=?, telefono_paciente=?, fecha_nacimiento=?, codigo_medico=?, codigo_enfermero=? WHERE codigo_paciente=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfNombre.getText().trim());
            ps.setString(2, tfApellidos.getText().trim());
            ps.setString(3, tfDireccion.getText().trim());
            ps.setString(4, tfTelefono.getText().trim());
            ps.setDate(5, parseDate(tfFecha.getText().trim()));
            ps.setString(6, tfMedico.getText().trim());
            ps.setString(7, tfEnfermero.getText().trim());
            ps.setString(8, codigo);
            int upd = ps.executeUpdate();
            if (upd > 0) {
                JOptionPane.showMessageDialog(this, "Actualizado correctamente");
                loadTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No existe ese código");
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Error de integridad (FK no existe): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        String codigo = tfCodigo.getText().trim();
        if (codigo.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el código a eliminar"); return; }
        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar paciente con código " + codigo + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM pacientes WHERE codigo_paciente=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            int del = ps.executeUpdate();
            if (del > 0) {
                JOptionPane.showMessageDialog(this, "Eliminado correctamente");
                loadTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No existe ese código");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultar() {
        String codigo = tfCodigo.getText().trim();
        if (codigo.isEmpty()) {
            // si no escribe código, muestra todo
            loadTable();
            return;
        }
        String sql = "SELECT codigo_paciente, nombre_paciente, apellidos_paciente, direccion, telefono_paciente, fecha_nacimiento, codigo_medico, codigo_enfermero FROM pacientes WHERE codigo_paciente=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);
                if (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("codigo_paciente"));
                    row.add(rs.getString("nombre_paciente"));
                    row.add(rs.getString("apellidos_paciente"));
                    row.add(rs.getString("direccion"));
                    row.add(rs.getString("telefono_paciente"));
                    Date d = rs.getDate("fecha_nacimiento");
                    row.add(d != null ? d.toString() : "");
                    row.add(rs.getString("codigo_medico"));
                    row.add(rs.getString("codigo_enfermero"));
                    model.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(this, "No existe ese paciente");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfCodigo.setText(""); tfNombre.setText(""); tfApellidos.setText(""); tfDireccion.setText("");
        tfTelefono.setText(""); tfFecha.setText(""); tfMedico.setText(""); tfEnfermero.setText("");
        table.clearSelection();
    }
}