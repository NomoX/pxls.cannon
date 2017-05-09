package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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
	ObservableList<BotTemplate> templates = FXCollections.observableArrayList();
	
	@FXML
	ComboBox<String> direction;
	@FXML
	TextField title, image, xpos, ypos, delay, threads;
	@FXML
	CheckBox pixelize;
	@FXML
	Button start;
	@FXML
	Label info, status;
	@FXML
	ListView<BotTemplate> templates_list;
	@FXML
	protected void initialize() {
		Loader.initialize();
		
		templates_list.setItems(templates);
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
		
		templates_list.setCellFactory(listView -> new ListCell<BotTemplate>() {
			private StackPane pane;
			private Label name;
			private Button button;
			private ImageView timg;
			@Override
			public void updateItem(BotTemplate item, boolean empty) {
				super.updateItem(item, empty);
		        if (empty) {
		        	setGraphic(null);
		        } else {
		        	timg = new ImageView(item.src);
		        	timg.setFitWidth(50);
		        	timg.setFitHeight(50);
		        	name = new Label();
		        	name.setText(item.title + String.format(" @ %d, %d", item.x, item.y));
		        	//name.setPrefWidth(180);
		        	name.setMaxWidth(Double.MAX_VALUE);
		        	name.setPadding(new Insets(0, 0, 0, 55));
		        	//name.prefWidthProperty().bind(templates_list.prefWidthProperty());
		        	
		        	button = new Button();
		        	button.setId("button_delete");
		        	
		        	button.setOnAction(event -> {
		        		Loader.deleteTemplate(item._id);
		        		loadTemplates();
		        	});
		        	
		        	pane = new StackPane(timg, name, button);
		        	pane.prefWidthProperty().bind(templates_list.prefWidthProperty());
		        	//pane.setAlignment(Pos.BOTTOM_RIGHT);
		        	StackPane.setAlignment(button, Pos.CENTER_RIGHT);
		        	StackPane.setAlignment(timg, Pos.CENTER_LEFT);
		            setGraphic(pane);
		        }
			}
		});
		templates_list.setOnMouseClicked((e)->{
			if (e.getClickCount() == 2) {
				BotTemplate t = templates_list.getSelectionModel().getSelectedItem();
				title.setText(t.title);
				image.setText(t.src);
				xpos.setText(String.valueOf(t.x));
				ypos.setText(String.valueOf(t.y));
				direction.getSelectionModel().select(t.direction);
			}
		});
		
		loadTemplates();
	}
	@FXML
	protected void buttonStart() {
		//start.setDisable(true);
		template = new BotTemplate(title.getText(),
				image.getText(),
				Integer.parseInt(xpos.getText()),
				Integer.parseInt(ypos.getText()),
				direction.getSelectionModel().getSelectedIndex());
		BotSettings settings = new BotSettings("",
				15000,
				Integer.parseInt(delay.getText()),
				Integer.parseInt(threads.getText()),
				pixelize.isSelected());
		Thread t = new Thread(()->{
			Bot bot = new Bot(settings, template, notifier); // http://79.142.204.22:8081/
			bot.start();
		});
		t.setDaemon(true);
		t.start();
		if (!templates.stream()
				.anyMatch(e->e.src.equals(template.src) && e.x == template.x && e.y == template.y)) // add it if not present
			addTemplate(template);
	}
	void loadTemplates() {
		templates.clear();
		Loader.loadTemplateList().forEach(templates::add);
	}
	void addTemplate(BotTemplate t) {
		Loader.addTemplate(t);
		loadTemplates();
	}
}
