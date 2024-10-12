import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalApp {
    private List<Patient> patients = new ArrayList<>();
    private static final String DATA_FILE = "patients.txt";
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalApp::new);
    }

    public HospitalApp() {
        loadPatientsFromFile();
        JFrame frame = new JFrame("Hospital Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // Modern dark theme background
        frame.getContentPane().setBackground(new Color(18, 18, 18));  // Darker background for modern look

        // Main layout
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(30, 30, 30));  // Dark tab background
        tabbedPane.setForeground(Color.WHITE);  // White text for contrast
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 16));  // Modern font

        tabbedPane.addTab("Add Patient", createAddPatientPanel());
        tabbedPane.addTab("View Patients", createViewPatientsPanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // Create the "Add Patient" tab with modern styling
    private JPanel createAddPatientPanel() {
        JPanel panel = createModernPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel nameLabel = createModernLabel("Name:");
        JLabel ageLabel = createModernLabel("Age:");
        JLabel genderLabel = createModernLabel("Gender:");
        JLabel contactLabel = createModernLabel("Contact:");
        JLabel addressLabel = createModernLabel("Address:");

        JTextField nameField = createModernTextField(15);
        JTextField ageField = createModernTextField(15);
        JTextField genderField = createModernTextField(15);
        JTextField contactField = createModernTextField(15);
        JTextField addressField = createModernTextField(15);

        JButton addButton = createModernButton("Add Patient", new Color(87, 150, 255), Color.WHITE);

        // Adding components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(ageLabel, gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(genderLabel, gbc);
        gbc.gridx = 1;
        panel.add(genderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(contactLabel, gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(addressLabel, gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(addButton, gbc);

        // Add patient button action
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = genderField.getText();
                String contact = contactField.getText();
                String address = addressField.getText();

                Patient patient = new Patient(name, age, gender, contact, address);
                patients.add(patient);
                savePatientsToFile();
                loadTableData(tableModel, patients); // Refresh table data

                showModernMessageDialog("Patient added successfully!", "Success");

                // Clear fields
                nameField.setText("");
                ageField.setText("");
                genderField.setText("");
                contactField.setText("");
                addressField.setText("");
            } catch (NumberFormatException ex) {
                showModernMessageDialog("Please enter a valid number for age", "Error");
            }
        });

        return panel;
    }
    class RoundedTextField extends JTextField {
        private int arcWidth;
        private int arcHeight;

        public RoundedTextField(int columns, int arcWidth, int arcHeight) {
            super(columns);
            this.arcWidth = arcWidth;
            this.arcHeight = arcHeight;
            setOpaque(false); // Makes the background transparent for custom painting
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set the background color
            g2.setColor(getBackground());
            // Fill a rounded rectangle as the background
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

            // Draw the text field's border
            g2.setColor(getForeground());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);

            // Call the super method to ensure the text is rendered
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Create the "View Patients" tab with modern styling
    private JPanel createViewPatientsPanel() {
        JPanel panel = createModernPanel();
        panel.setLayout(new BorderLayout());

        // Search bar
        JPanel searchPanel = createModernPanel();
        JTextField searchField = createModernTextField(20);
        JButton searchButton = createModernButton("Search", new Color(87, 150, 255), Color.WHITE);
        searchPanel.add(createModernLabel("Search by Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Name", "Age", "Gender", "Contact", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter); // Enable sorting
        JScrollPane scrollPane = new JScrollPane(table);

        loadTableData(tableModel, patients); // Load initial data

        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel with modern buttons
        JPanel buttonPanel = createModernPanel();
        JButton deleteButton = createModernButton("Delete Selected", new Color(255, 87, 87), Color.WHITE);
        JButton updateButton = createModernButton("Update Selected", new Color(87, 150, 255), Color.WHITE);
        JButton exportButton = createModernButton("Export to CSV", new Color(67, 180, 120), Color.WHITE);

        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                patients.remove(table.convertRowIndexToModel(selectedRow));
                tableModel.removeRow(selectedRow);
                savePatientsToFile();
                showModernMessageDialog("Patient deleted successfully", "Success");
            } else {
                showModernMessageDialog("Please select a patient to delete", "Error");
            }
        });

        // Update button action
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                String newName = JOptionPane.showInputDialog("Enter new name", tableModel.getValueAt(modelRow, 0));
                String newAgeText = JOptionPane.showInputDialog("Enter new age", tableModel.getValueAt(modelRow, 1));
                String newGender = JOptionPane.showInputDialog("Enter new gender", tableModel.getValueAt(modelRow, 2));
                String newContact = JOptionPane.showInputDialog("Enter new contact", tableModel.getValueAt(modelRow, 3));
                String newAddress = JOptionPane.showInputDialog("Enter new address", tableModel.getValueAt(modelRow, 4));

                try {
                    int newAge = Integer.parseInt(newAgeText);
                    Patient patient = patients.get(modelRow);
                    patient.setName(newName);
                    patient.setAge(newAge);
                    patient.setGender(newGender);
                    patient.setContact(newContact);
                    patient.setAddress(newAddress);

                    tableModel.setValueAt(newName, modelRow, 0);
                    tableModel.setValueAt(newAge, modelRow, 1);
                    tableModel.setValueAt(newGender, modelRow, 2);
                    tableModel.setValueAt(newContact, modelRow, 3);
                    tableModel.setValueAt(newAddress, modelRow, 4);

                    savePatientsToFile();
                    showModernMessageDialog("Patient updated successfully", "Success");
                } catch (NumberFormatException ex) {
                    showModernMessageDialog("Please enter a valid number for age", "Error");
                }
            } else {
                showModernMessageDialog("Please select a patient to update", "Error");
            }
        });

        // Export to CSV button action
        exportButton.addActionListener(e -> exportToCSV());

        // Search functionality
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (searchText.trim().length() > 0) {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            } else {
                rowSorter.setRowFilter(null); // Reset filter if search field is empty
            }
        });

        return panel;
    }

    // Utility function to create modern buttons with gradient
    private JButton createModernButton(String text, Color startColor, Color endColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a gradient from the startColor to the endColor
                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Draw the button text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2 - 2);
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(160, 40));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        return button;
    }

    // Utility function to create modern panels
    private JPanel createModernPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));  // Dark background
        return panel;
    }

    // Utility function to create modern labels
    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    // Utility function to create modern text fields
    // Utility function to create modern text fields with rounded corners
    private JTextField createModernTextField(int columns) {
        RoundedTextField textField = new RoundedTextField(columns, 15, 15); // Rounded corners with 15px arc width and height
        textField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textField.setBackground(new Color(50, 50, 50)); // Dark background
        textField.setForeground(Color.WHITE); // White text
        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
        return textField;
    }


    // Show modern message dialog with custom styling
    private void showModernMessageDialog(String message, String title) {
        UIManager.put("OptionPane.background", new Color(30, 30, 30));
        UIManager.put("Panel.background", new Color(30, 30, 30));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 16));
        UIManager.put("Button.background", new Color(87, 150, 255));
        UIManager.put("Button.foreground", Color.WHITE);
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Utility function to load table data
    private void loadTableData(DefaultTableModel model, List<Patient> patientList) {
        model.setRowCount(0); // Clear current rows
        for (Patient patient : patientList) {
            model.addRow(new Object[]{patient.getName(), patient.getAge(), patient.getGender(),
                    patient.getContact(), patient.getAddress()});
        }
    }

    // Save patients to file
    private void savePatientsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Patient patient : patients) {
                writer.write(patient.getName() + "," + patient.getAge() + "," +
                        patient.getGender() + "," + patient.getContact() + "," +
                        patient.getAddress());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load patients from file
    private void loadPatientsFromFile() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 5) {
                        String name = data[0];
                        int age = Integer.parseInt(data[1]);
                        String gender = data[2];
                        String contact = data[3];
                        String address = data[4];
                        patients.add(new Patient(name, age, gender, contact, address));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Export data to CSV
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write("Name,Age,Gender,Contact,Address");
                writer.newLine();
                for (Patient patient : patients) {
                    writer.write(patient.getName() + "," + patient.getAge() + "," + patient.getGender() + "," +
                            patient.getContact() + "," + patient.getAddress());
                    writer.newLine();
                }
                showModernMessageDialog("Export successful!", "Success");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Patient class
    static class Patient {
        private String name;
        private int age;
        private String gender;
        private String contact;
        private String address;

        public Patient(String name, int age, String gender, String contact, String address) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.contact = contact;
            this.address = address;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
