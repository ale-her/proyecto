package Ventanas;

/**
 *
 * @author alehe
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;
import java.text.*;
import java.util.Vector;

public class MedicosF extends JFrame {
    private JTextField tfCodigo, tfNombre, tfApP, tfApM, tfFecha, tfArea;
    private JButton btnGuardar, btnActualizar, btnEliminar, btnConsultar, btnRegresar;
    private JTable table;
    private DefaultTableModel model;

    public MedicosF() {
        setTitle("Médicos");
        setSize(900,520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        tfCodigo = new JTextField(10);
        tfNombre = new JTextField(12);
        tfApP = new JTextField(12);
        tfApM = new JTextField(12);
        tfFecha = new JTextField(10); // yyyy-MM-dd
        tfArea = new JTextField(12);

        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnConsultar = new JButton("Consultar");
        btnRegresar = new JButton("Regresar al menú");

        model = new DefaultTableModel(new Object[]{"Código","Nombre","A.Paterno","A.Materno","Fecha","Área"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        table = new JTable(model);

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(4,4,4,4);

        c.gridx=0; c.gridy=0; top.add(new JLabel("Código:"), c); c.gridx=1; top.add(tfCodigo, c);
        c.gridx=2; top.add(new JLabel("Nombre:"), c); c.gridx=3; top.add(tfNombre, c);
        c.gridx=0; c.gridy=1; top.add(new JLabel("Apellido P:"), c); c.gridx=1; top.add(tfApP, c);
        c.gridx=2; top.add(new JLabel("Apellido M:"), c); c.gridx=3; top.add(tfApM, c);
        c.gridx=0; c.gridy=2; top.add(new JLabel("Fecha (yyyy-MM-dd):"), c); c.gridx=1; top.add(tfFecha, c);
        c.gridx=2; top.add(new JLabel("Área:"), c); c.gridx=3; top.add(tfArea, c);

        JPanel botones = new JPanel();
        botones.add(btnGuardar); botones.add(btnActualizar); botones.add(btnEliminar); botones.add(btnConsultar); botones.add(btnRegresar);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

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
                tfApP.setText(model.getValueAt(r,2).toString());
                tfApM.setText(model.getValueAt(r,3).toString());
                tfFecha.setText(model.getValueAt(r,4).toString());
                tfArea.setText(model.getValueAt(r,5).toString());
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);
        String sql = "SELECT codigo_medico,nombre_medico,a_paterno,a_materno,fecha_contratacion,area FROM medicos";
        try (Connection c = conexion.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("codigo_medico"));
                row.add(rs.getString("nombre_medico"));
                row.add(rs.getString("a_paterno"));
                row.add(rs.getString("a_materno"));
                Date d = rs.getDate("fecha_contratacion");
                row.add(d != null ? d.toString() : "");
                row.add(rs.getString("area"));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar medicos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        String sql = "INSERT INTO medicos (codigo_medico,nombre_medico,a_paterno,a_materno,fecha_contratacion,area) VALUES (?,?,?,?,?,?)";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfCodigo.getText().trim());
            ps.setString(2, tfNombre.getText().trim());
            ps.setString(3, tfApP.getText().trim());
            ps.setString(4, tfApM.getText().trim());
            ps.setDate(5, parseDate(tfFecha.getText().trim()));
            ps.setString(6, tfArea.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guardado correctamente");
            loadTable();
            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        String codigo = tfCodigo.getText().trim();
        if (codigo.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el código a actualizar"); return; }
        String sql = "UPDATE medicos SET nombre_medico=?, a_paterno=?, a_materno=?, fecha_contratacion=?, area=? WHERE codigo_medico=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfNombre.getText().trim());
            ps.setString(2, tfApP.getText().trim());
            ps.setString(3, tfApM.getText().trim());
            ps.setDate(4, parseDate(tfFecha.getText().trim()));
            ps.setString(5, tfArea.getText().trim());
            ps.setString(6, codigo);
            int upd = ps.executeUpdate();
            if (upd > 0) {
                JOptionPane.showMessageDialog(this, "Actualizado correctamente");
                loadTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No existe ese código");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        String codigo = tfCodigo.getText().trim();
        if (codigo.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el código a eliminar"); return; }
        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar registro con código " + codigo + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM medicos WHERE codigo_medico=?";
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
        if (codigo.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresa el código para consultar"); return; }
        String sql = "SELECT codigo_medico,nombre_medico,a_paterno,a_materno,fecha_contratacion,area FROM medicos WHERE codigo_medico=?";
        try (Connection c = conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);
                if (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("codigo_medico"));
                    row.add(rs.getString("nombre_medico"));
                    row.add(rs.getString("a_paterno"));
                    row.add(rs.getString("a_materno"));
                    Date d = rs.getDate("fecha_contratacion");
                    row.add(d != null ? d.toString() : "");
                    row.add(rs.getString("area"));
                    model.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(this, "No existe ese médico");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfCodigo.setText(""); tfNombre.setText(""); tfApP.setText(""); tfApM.setText(""); tfFecha.setText(""); tfArea.setText("");
        table.clearSelection();
    }
}
