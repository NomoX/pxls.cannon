package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.database.DatabaseConnection;
import application.database.Table;

public class Loader {
    private static DatabaseConnection databaseConnection;
    private static Table templates;
    public static void initialize() {
        databaseConnection = new DatabaseConnection("templates.db");
        
        String query = "CREATE TABLE if not exists 'templates' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' text, 'src' text, 'xpos' INTEGER, 'ypos' INTEGER, 'direction' INTEGER);";
        templates = databaseConnection.createTable(query);
    }
    public static List<BotTemplate> loadTemplateList() {
        List<BotTemplate> list = new ArrayList<>();
        ResultSet resSet = templates.getResult("SELECT * FROM 'templates';");
        try {
            while (resSet.next()) {
            	BotTemplate template = new BotTemplate(resSet.getString("title"),
            			resSet.getString("src"),
            			resSet.getInt("xpos"),
            			resSet.getInt("ypos"),
            			resSet.getInt("direction"));
            	template._id = resSet.getInt("id");
                list.add(template);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    public static void addTemplate(BotTemplate t) {
    	templates.execute(String.format("INSERT INTO 'templates' ('title', 'src', 'xpos', 'ypos', 'direction') VALUES ('%s', '%s', %d, %d, %d);",
    			t.title, t.src, t.x, t.y, t.direction));
    }
    public static void deleteAllTemplates() {
    	templates.execute("DELETE FROM 'templates';");
    }
	public static void deleteTemplate(int id) {
		templates.execute(String.format("DELETE FROM 'templates' WHERE id = %d;", id));
	}
    
}
