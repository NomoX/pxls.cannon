package application.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;


public class DatabaseConnection {

    private Connection connection;

    public DatabaseConnection(String database) {
        connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "", "Database error", JOptionPane.ERROR_MESSAGE);
            //ex.printStackTrace();
        }
    }

    public Table createTable(String sql) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "" + ex.getSQLState(), "Database error", JOptionPane.ERROR_MESSAGE);
            //ex.printStackTrace();
        }
        return new Table(statement);
    }
    //
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "" + ex.getSQLState(), "Database error", JOptionPane.ERROR_MESSAGE);
            //ex.printStackTrace();
        }
    }
}
