import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class App extends JFrame {
    private final EmployeeDao employeeDao = new EmployeeDao();

    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField positionField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final DefaultTableModel tableModel;

    public App() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setTitle("INF.04 - System Obsługi Pracowników");
        setSize(700, 500);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nowy pracownik"));

        formPanel.add(new JLabel("Imię:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Nazwisko:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Stanowisko:"));
        formPanel.add(positionField);

        JButton addButton = new JButton("Dodaj Pracownika");
        addButton.addActionListener(event -> addEmployee());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(addButton, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        String[] columns = {"ID", "Imię", "Nazwisko", "Stanowisko"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton searchButton = new JButton("Szukaj");
        searchButton.addActionListener(event -> loadEmployees());
        searchField.addActionListener(event -> loadEmployees());

        JButton clearButton = new JButton("Wyczyść");
        clearButton.addActionListener(event -> {
            searchField.setText("");
            loadEmployees();
        });

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchButtons.add(searchButton);
        searchButtons.add(clearButton);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchPanel.add(new JLabel("Szukaj:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        prepareDatabase();
        loadEmployees();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void prepareDatabase() {
        try {
            employeeDao.createTableIfNotExists();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nie udało się przygotować tabeli pracownicy:\n" + exception.getMessage(),
                    "Błąd bazy danych",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        String phrase = searchField.getText().trim();

        try {
            List<Employee> employees = phrase.isEmpty()
                    ? employeeDao.findAll()
                    : employeeDao.findByPhrase(phrase);

            for (Employee employee : employees) {
                tableModel.addRow(new Object[]{
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getPosition()
                });
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Błąd podczas pobierania danych:\n" + exception.getMessage(),
                    "Błąd bazy danych",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addEmployee() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String position = positionField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Wszystkie pola muszą być wypełnione.",
                    "Walidacja",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            employeeDao.insert(new Employee(firstName, lastName, position));

            JOptionPane.showMessageDialog(
                    this,
                    "Pracownik został dodany.",
                    "Sukces",
                    JOptionPane.INFORMATION_MESSAGE
            );

            firstNameField.setText("");
            lastNameField.setText("");
            positionField.setText("");
            searchField.setText("");
            firstNameField.requestFocus();

            loadEmployees();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Błąd podczas dodawania pracownika:\n" + exception.getMessage(),
                    "Błąd bazy danych",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
