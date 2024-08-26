package oops_project;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.DateFormat;
import java.util.Vector;
public class Courier {
     private JTextField nameData;
     private JTextField addressData;
     private JTable table1;
    private JButton ADDRECORDButton;
    private JButton UPDATERECORDButton;
    private JPanel clinicPanel;
    private JComboBox status;
    private JPanel datePanel;
    private JLabel total;
    private JComboBox type;
    private JTextField cost;
    private JComboBox litres;
    JFrame clinicF = new JFrame();
    JDateChooser dateChooser = new JDateChooser();
    public Courier(){
        clinicF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clinicPanel = new JPanel();
        clinicPanel.setLayout(new BorderLayout());
        clinicF.setContentPane(clinicPanel);
        clinicF.pack();
        clinicF.setLocationRelativeTo(null);
        datePanel = new JPanel();
        datePanel.add(dateChooser);
        table1 = new JTable();
        ADDRECORDButton = new JButton("Add Record");
        UPDATERECORDButton = new JButton("Update Record");
        total = new JLabel("Total:");
        nameData = new JTextField();
        addressData = new JTextField();
        status = new JComboBox();
        type = new JComboBox();
        cost = new JTextField();
        clinicF.pack();
        clinicF.setLocationRelativeTo(null);
        tableData();
        ADDRECORDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nameData.getText().equals("")|| addressData.getText().equals("")|| status.getSelectedItem()==null){
                    JOptionPane.showMessageDialog(null,"Please Fill All Fields to add Record.");
                }else{
                    try {
                        String sql = "insert into courier"+"(sender_name,shipping_address,estimated_delivery,type,cost,status)"+"values (?,?,?,?,?,?)";
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oopscp","root","8848");
                        PreparedStatement statement = connection.prepareStatement(sql);
                        String date= DateFormat.getDateInstance().format(dateChooser.getDate());
                        statement.setString(1,nameData.getText());
                        statement.setString(2, addressData.getText());
                        statement.setString(3, date);
                        statement.setString(4,""+type.getSelectedItem());
                        statement.setString(5,cost.getText());
                        statement.setString(6,""+status.getSelectedItem());
                        statement.executeUpdate();
                        JOptionPane.showMessageDialog(null,"DETAILS ADDED SUCCESSFULLY");
                        nameData.setText("");
                        addressData.setText("");
                        dateChooser.setCalendar(null);
                        cost.setText("");
                        clinicF.setVisible(true);
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(null,ex.getMessage());
                    }
                    tableData();
                    total.setText(String.valueOf(count()));
                }
            }
        });
        UPDATERECORDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String sql = "UPDATE courier SET status = '%s' WHERE sender_name= '%s' AND shipping_address= '%s'".formatted(status.getSelectedItem(), nameData.getText(), addressData.getText());
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oopscp","root","8848");
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Updated successfully");
                }catch (Exception e2){
                    System.out.println(e2);
                }
                tableData();
            }
        });
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel dm = (DefaultTableModel)table1.getModel();
                int selectedRow = table1.getSelectedRow();
                nameData.setText(dm.getValueAt(selectedRow,0).toString());
                addressData.setText(dm.getValueAt(selectedRow,1).toString());
                cost.setText(dm.getValueAt(selectedRow,4).toString());
            }
        });
    }

    public void tableData() {
        try{
            String a;
            a = "Select * from courier";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oopscp","root","8848");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(a);
            table1.setModel(buildTableModel(rs));
        }catch (Exception ex1){
            JOptionPane.showMessageDialog(null,ex1.getMessage());
        }
    }
    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
// names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
// data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
    public int count(){
        int total = 0;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oopscp","root","8848");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("Select cost from courier");
            while (rs.next()){
                total= total+rs.getInt(1);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return total;
    }
}

