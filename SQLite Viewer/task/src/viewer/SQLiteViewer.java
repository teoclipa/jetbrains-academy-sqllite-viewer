package viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class SQLiteViewer extends JFrame {

    private JTextField nameTextField;
    private JButton openFileButton;
    private JComboBox<String> tablesComboBox;
    private JTextArea queryTextArea;
    private JButton executeQueryButton;

    private JTable dataTable;
    private DefaultTableModel tableModel;

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JLabel nameLabel = new JLabel("Database:");
        nameLabel.setBounds(10, 10, 80, 20);
        add(nameLabel);

        nameTextField = new JTextField();
        nameTextField.setBounds(90, 10, 300, 20);
        nameTextField.setName("FileNameTextField");
        add(nameTextField);

        openFileButton = new JButton("Open");
        openFileButton.setBounds(400, 10, 80, 20);
        openFileButton.setName("OpenFileButton");
        openFileButton.addActionListener(new OpenButtonListener());
        add(openFileButton);

        tablesComboBox = new JComboBox<>();
        tablesComboBox.setBounds(90, 40, 300, 20);
        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.addActionListener(new TablesComboBoxListener());
        add(tablesComboBox);

        JLabel queryLabel = new JLabel("Query:");
        queryLabel.setBounds(10, 70, 80, 20);
        add(queryLabel);

        queryTextArea = new JTextArea();
        queryTextArea.setBounds(90, 70, 300, 100);
        queryTextArea.setName("QueryTextArea");
        add(queryTextArea);

        executeQueryButton = new JButton("Execute");
        executeQueryButton.setBounds(400, 70, 80, 20);
        executeQueryButton.setName("ExecuteQueryButton");
        add(executeQueryButton);

        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        dataTable.setName("Table");

        // Initially disable components until a database is loaded
        disableComponents();

        // Set up a scroll pane for the table to allow scrolling
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBounds(10, 200, 460, 250);
        add(scrollPane);

        executeQueryButton.addActionListener(new ExecuteQueryButtonListener());
    }

    private void disableComponents() {
        queryTextArea.setEnabled(false);
        executeQueryButton.setEnabled(false);
    }

    private class OpenButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = nameTextField.getText().trim();
            if (fileName.isEmpty()) {
                JOptionPane.showMessageDialog(new Frame(), "File name is empty!");
                disableComponents();
                return;
            }

            File file = new File(fileName);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
                disableComponents();
            } else {
                connectToDatabase(fileName);
            }
        }
    }

    private void connectToDatabase(String fileName) {
        String url = "jdbc:sqlite:" + fileName;
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';")) {
            tablesComboBox.removeAllItems(); // Clear previous items
            while (rs.next()) {
                tablesComboBox.addItem(rs.getString("name"));
            }
            queryTextArea.setEnabled(true);
            executeQueryButton.setEnabled(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            disableComponents(); // Disable components on connection failure
        }
    }

    private class TablesComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox comboBox = (JComboBox) e.getSource();
            String tableName = (String) comboBox.getSelectedItem();
            if (tableName != null && !tableName.isEmpty()) {
                queryTextArea.setText("SELECT * FROM " + tableName + ";");
            }
        }
    }


    private class ExecuteQueryButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String query = queryTextArea.getText().trim();
            if (!query.isEmpty()) {
                executeQuery(query);
            }
        }
    }

    private void executeQuery(String query) {
        String fileName = nameTextField.getText().trim();
        File file = new File(fileName);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
            disableComponents();
            return;
        }

        String url = "jdbc:sqlite:" + fileName;
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columnNames = new String[columnCount];
            for (int column = 0; column < columnCount; column++) {
                columnNames[column] = metaData.getColumnName(column + 1);
            }
            tableModel.setColumnIdentifiers(columnNames);

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(new Frame(), "SQL error: " + e.getMessage());
            disableComponents();
        }
    }
}
