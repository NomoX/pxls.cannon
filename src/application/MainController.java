package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {
	BotTemplate template = null;
	BotNotifier notifier;
	ObservableList<String> options = FXCollections.observableArrayList(
		"Random",
		"Left - Right",
		"Right - Left",
		"Top - Bottom",
		"Bottom - Top",
		"Chess"
	);
	
	@FXML
	ComboBox<String> direction;
	@FXML
	TextField title, image, xpos, ypos, proxy, timeout, delay, threads;
	@FXML
	CheckBox pixelize;
	@FXML
	Button start;
	@FXML
	Label info, status;
	@FXML
	protected void initialize() {
		direction.setItems(options);
		direction.getSelectionModel().selectFirst();
		direction.valueProperty().addListener((obs, oldVal, newVal)->{
			if (template != null)
				template.direction = direction.getSelectionModel().getSelectedIndex();
		});
		
		notifier = new BotNotifier() {
			@Override
			public void status(String message) {
				Platform.runLater(()->{
					status.setText(message);
				});
			}
			@Override
			public void info(String message) {
				Platform.runLater(()->{
					info.setText(message);
				});
			}
		};
	}
	@FXML
	protected void buttonStart() {
		//start.setDisable(true);
		template = new BotTemplate(title.getText(),
				image.getText(),
				Integer.parseInt(xpos.getText()),
				Integer.parseInt(ypos.getText()),
				direction.getSelectionModel().getSelectedIndex());
		BotSettings settings = new BotSettings(proxy.getText(),
				Integer.parseInt(timeout.getText()),
				Integer.parseInt(delay.getText()),
				Integer.parseInt(threads.getText()),
				pixelize.isSelected());
		Thread t = new Thread(()->{
			Bot bot = new Bot(settings, template, notifier); // http://79.142.204.22:8081/
			bot.start();
		});
		t.setDaemon(true);
		t.start();
	}
}
