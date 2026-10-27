import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS pracownicy (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "imie VARCHAR(50) NOT NULL, " +
                "nazwisko VARCHAR(50) NOT NULL, " +
                "stanowisko VARCHAR(100) NOT NULL)";

        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, imie, nazwisko, stanowisko FROM pracownicy ORDER BY id";

        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(readEmployee(resultSet));
            }
        }

        return employees;
    }

    public List<Employee> findByPhrase(String phrase) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, imie, nazwisko, stanowisko FROM pracownicy " +
                "WHERE imie LIKE ? OR nazwisko LIKE ? OR stanowisko LIKE ? ORDER BY id";

        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String pattern = "%" + phrase + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(readEmployee(resultSet));
                }
            }
        }

        return employees;
    }

    private Employee readEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("id"),
                resultSet.getString("imie"),
                resultSet.getString("nazwisko"),
                resultSet.getString("stanowisko")
        );
    }

    public void insert(Employee employee) throws SQLException {
        String sql = "INSERT INTO pracownicy (imie, nazwisko, stanowisko) VALUES (?, ?, ?)";

        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setString(3, employee.getPosition());
            statement.executeUpdate();
        }
    }
}
