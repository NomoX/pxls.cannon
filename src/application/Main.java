package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
			GridPane root = (GridPane)fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icons/logo.png")));
			primaryStage.show();

			MainController myController = fxmlLoader.<MainController>getController();
			scene.setOnDragOver(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasFiles() || db.hasString()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			});
			scene.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					success = true;
					myController.dragAndDropFiles(db.getFiles());
				}
				else if (db.hasString()) {
					success = true;
					myController.dragAndDropString(db.getString());
				}
				event.setDropCompleted(success);
				event.consume();
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
