package application.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class Table {
    private String name;
    private final Statement statement;
    public Table(Statement statement) {
        this.statement = statement;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void execute(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "" + ex.getSQLState(), "Table error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public ResultSet getResult(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "" + ex.getSQLState(), "Table error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return null;
    }
    public void close() {
        try {
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "" + ex.getSQLState(), "Table error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    // insert("name, age", "", 10)
    public void insert(String fields, Object... values) {
        String query = "INSERT ";
        PreparedStatement prepStatement = null;
        try {
            prepStatement = statement.getConnection().prepareStatement(query);
            //
            prepStatement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
