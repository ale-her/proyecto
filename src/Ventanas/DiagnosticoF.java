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

public class DiagnosticoF  extends JFrame {
  
    private static final String TABLE_NAME = "diagnostico"; 
   
    private final JTextField tfId = new JTextField(8);
    private final JTextField tfPaciente = new JTextField(12);
    private final JTextField tfMedico = new JTextField(12);
    private final JTextField tfEnfermero = new JTextField(12);
    private final JTextField tfFecha = new JTextField(10); // yyyy-MM-dd
    private final JTextArea taDiagnostico = new JTextArea(3, 20);
    private final JCheckBox cbFuma = new JCheckBox();
    private final JCheckBox cbPadece = new JCheckBox();
    private final JTextField tfEnfermedad = new JTextField(15);
    private final JTextArea taReceta = new JTextArea(3, 20);

    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnConsultar = new JButton("Consultar");
    private final JButton btnRegresar = new JButton("Regresar al menú");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Código Paciente","Código Médico","Código Enfermero","Fecha","Diagnóstico","Fuma","Padece","Enfermedad","Receta"}, 0) {
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);

    public DiagnosticoF() {
        setTitle("Diagnóstico de paciente");
        setSize(1000,580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; top.add(new JLabel("ID Diagnóstico:"), c); c.gridx = 1; top.add(tfId, c);
        c.gridx = 2; top.add(new JLabel("Código Paciente:"), c); c.gridx = 3; top.add(tfPaciente, c);
        c.gridx = 4; top.add(new JLabel("Código Médico:"), c); c.gridx = 5; top.add(tfMedico, c);

        c.gridx = 0; c.gridy = 1; top.add(new JLabel("Código Enfermero:"), c); c.gridx = 1; top.add(tfEnfermero, c);
        c.gridx = 2; top.add(new JLabel("Fecha (yyyy-MM-dd):"), c); c.gridx = 3; top.add(tfFecha, c);
        c.gridx = 4; top.add(new JLabel("Fuma:"), c); c.gridx = 5; top.add(cbFuma, c);

        c.gridx = 0; c.gridy = 2; top.add(new JLabel("Padece enfermedad:"), c); c.gridx = 1; top.add(cbPadece, c);
        c.gridx = 2; top.add(new JLabel("Enfermedad que padece:"), c); c.gridx = 3; top.add(tfEnfermedad, c);

        // Diagnóstico textarea (spanning)
        c.gridx = 0; c.gridy = 3; c.gridwidth = 1; top.add(new JLabel("Diagnóstico:"), c);
        c.gridx = 1; c.gridwidth = 5;
        JScrollPane spDiag = new JScrollPane(taDiagnostico);
        top.add(spDiag, c);

        // Receta textarea
        c.gridx = 0; c.gridy = 4; c.gridwidth = 1; top.add(new JLabel("Receta / Tratamiento:"), c);
        c.gridx = 1; c.gridwidth = 5;
        JScrollPane spRec = new JScrollPane(taReceta);
        top.add(spRec, c);

        c.gridwidth = 1; // reset

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
                tfId.setText(model.getValueAt(r,0).toString());
                tfPaciente.setText(nullToEmpty(model.getValueAt(r,1)));
                tfMedico.setText(nullToEmpty(model.getValueAt(r,2)));
                tfEnfermero.setText(nullToEmpty(model.getValueAt(r,3)));
                tfFecha.setText(nullToEmpty(model.getValueAt(r,4)));
                taDiagnostico.setText(nullToEmpty(model.getValueAt(r,5)));
                cbFuma.setSelected("1".equals(String.valueOf(model.getValueAt(r,6))) || "true".equalsIgnoreCase(String.valueOf(model.getValueAt(r,6))));
                cbPadece.setSelected("1".equals(String.valueOf(model.getValueAt(r,7))) || "true".equalsIgnoreCase(String.valueOf(model.getValueAt(r,7))));
                tfEnfermedad.setText(nullToEmpty(model.getValueAt(r,8)));
                taReceta.setText(nullToEmpty(model.getValueAt(r,9)));
            }
        });
    }

    private String nullToEmpty(Object o) {
        return o == null ? "" : o.toString();
    }

    private void loadTable() {
        model.setRowCount(0);
        String sql = "SELECT id_diagnostico, codigo_paciente, codigo_medico, codigo_enfermero, fecha_ingreso, diagnostico, fuma, padece_enfermedad, enfermedad_padece, receta_tratamiento FROM " + TABLE_NAME;
        try (Connection c = conexion.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id_diagnostico"));
                row.add(rs.getString("codigo_paciente"));
                row.add(rs.getString("codigo_medico"));
                row.add(rs.getString("codigo_enfermero"));
                Date d = rs.getDate("fecha_ingreso");
                row.add(d != null ? d.toString() : "");
                row.add(rs.getString("diagnostico"));
                row.add(rs.getInt("fuma"));
                row.add(rs.getInt("padece_enfermedad"));
                row.add(rs.getString("enfermedad_padece"));
                row.add(rs.getString("receta_tratamiento"));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar diagnóstico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        String sql = "INSERT INTO " + TABLE_NAME + " (codigo_paciente, codigo_medico, codigo_enfermero, fecha_ingreso, diagnostico, fuma, padece_enfermedad, enfermedad_padece, receta_tratamiento) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfPaciente.getText().trim());
            ps.setString(2, tfMedico.getText().trim());
            ps.setString(3, tfEnfermero.getText().trim());
            ps.setDate(4, parseDate(tfFecha.getText().trim()));
            ps.setString(5, taDiagnostico.getText().trim());
            ps.setInt(6, cbFuma.isSelected() ? 1 : 0);
            ps.setInt(7, cbPadece.isSelected() ? 1 : 0);
            ps.setString(8, tfEnfermedad.getText().trim());
            ps.setString(9, taReceta.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guardado correctamente");
            loadTable();
            clearFields();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Error de integridad (FK no existe): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        String idText = tfId.getText().trim();
        if (idText.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el id a actualizar"); return; }
        String sql = "UPDATE " + TABLE_NAME + " SET codigo_paciente=?, codigo_medico=?, codigo_enfermero=?, fecha_ingreso=?, diagnostico=?, fuma=?, padece_enfermedad=?, enfermedad_padece=?, receta_tratamiento=? WHERE id_diagnostico=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfPaciente.getText().trim());
            ps.setString(2, tfMedico.getText().trim());
            ps.setString(3, tfEnfermero.getText().trim());
            ps.setDate(4, parseDate(tfFecha.getText().trim()));
            ps.setString(5, taDiagnostico.getText().trim());
            ps.setInt(6, cbFuma.isSelected() ? 1 : 0);
            ps.setInt(7, cbPadece.isSelected() ? 1 : 0);
            ps.setString(8, tfEnfermedad.getText().trim());
            ps.setString(9, taReceta.getText().trim());
            ps.setInt(10, Integer.parseInt(idText));
            int upd = ps.executeUpdate();
            if (upd > 0) {
                JOptionPane.showMessageDialog(this, "Actualizado correctamente");
                loadTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No existe ese id");
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Error de integridad (FK no existe): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        String idText = tfId.getText().trim();
        if (idText.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el id a eliminar"); return; }
        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar diagnóstico con id " + idText + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id_diagnostico=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(idText));
            int del = ps.executeUpdate();
            if (del > 0) {
                JOptionPane.showMessageDialog(this, "Eliminado correctamente");
                loadTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No existe ese id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultar() {
        String idText = tfId.getText().trim();
        if (idText.isEmpty()) {
            // si no escribe id, muestro todo (loadTable)
            loadTable();
            return;
        }
        String sql = "SELECT id_diagnostico, codigo_paciente, codigo_medico, codigo_enfermero, fecha_ingreso, diagnostico, fuma, padece_enfermedad, enfermedad_padece, receta_tratamiento FROM " + TABLE_NAME + " WHERE id_diagnostico=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(idText));
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);
                if (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id_diagnostico"));
                    row.add(rs.getString("codigo_paciente"));
                    row.add(rs.getString("codigo_medico"));
                    row.add(rs.getString("codigo_enfermero"));
                    Date d = rs.getDate("fecha_ingreso");
                    row.add(d != null ? d.toString() : "");
                    row.add(rs.getString("diagnostico"));
                    row.add(rs.getInt("fuma"));
                    row.add(rs.getInt("padece_enfermedad"));
                    row.add(rs.getString("enfermedad_padece"));
                    row.add(rs.getString("receta_tratamiento"));
                    model.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(this, "No existe ese diagnóstico");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfId.setText(""); tfPaciente.setText(""); tfMedico.setText(""); tfEnfermero.setText("");
        tfFecha.setText(""); taDiagnostico.setText(""); cbFuma.setSelected(false); cbPadece.setSelected(false);
        tfEnfermedad.setText(""); taReceta.setText("");
        table.clearSelection();
    }
}