package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {
	final static String[] IMAGE_EXTENSION = {"png", "jpg", "jpeg", "gif"};

	FileChooser chooser = new FileChooser();
	List<Thumbnail> cached_images;
	
	Bot selected_bot = null;
	Placer selected_placer = null;
	Notifier notifier;
	ObservableList<String> options = FXCollections.observableArrayList(
		"Random",
		"Left - Right",
		"Right - Left",
		"Top - Bottom",
		"Bottom - Top",
		"Chess"
	);
	ObservableList<Bot> bots = FXCollections.observableArrayList();
	
	@FXML
	ComboBox<String> direction;
	@FXML
	TextField title, image, xpos, ypos, delay, threads;
	@FXML
	CheckBox pixelize;
	@FXML
	Button start, stop;
	@FXML
	Label status;
	@FXML
	ListView<Bot> bot_list;
	@FXML
	protected void initialize() {
		chooser.setTitle("Open File");
		chooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
			new FileChooser.ExtensionFilter("Ayumish", "*.png", "*.jpg", "*.jpeg", "*.gif")
		);

		Loader.initialize();

		bot_list.setItems(bots);
		direction.setItems(options);
		direction.getSelectionModel().selectFirst();
		direction.valueProperty().addListener((obs, oldVal, newVal)->{
			if (selected_placer != null) {
				selected_placer.getTemplate().direction = direction.getSelectionModel().getSelectedIndex();
				bot_list.refresh();
			}
			/*
			if (selected_bot != null)
				selected_bot.getTemplate().direction = direction.getSelectionModel().getSelectedIndex();*/
		});
		notifier = (message, type) -> {
			System.out.println(message);
			switch (type) {
				case DIALOG:
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Information");
					alert.setHeaderText(null);
					alert.setContentText(message);
					alert.showAndWait();
					break;
				case STATUS:
					Platform.runLater(()->{
						status.setText(message);
					});
					break;
			}
		};
		bot_list.setCellFactory(listView -> new ListCell<Bot>() {
			@Override
			public void updateItem(Bot item, boolean empty) {
				super.updateItem(item, empty);
		        if (empty) {
		        	setGraphic(null);
		        } else {
					ImageView timg = new ImageView(getThumbnail(item.getTemplate().src).image);
		        	timg.setFitWidth(Thumbnail.SIZE);
		        	timg.setFitHeight(Thumbnail.SIZE);
					timg.setSmooth(true);
					timg.setCache(true);
		        	Label name = new Label();
		        	name.setText(item.getTemplate().title +
							String.format(" @ %d, %d", item.getTemplate().x, item.getTemplate().y));
		        	name.setMaxWidth(Double.MAX_VALUE);
		        	//name.prefWidthProperty().bind(templates_list.prefWidthProperty());
		        	
		        	Button button = new Button();
		        	button.setId("button_delete");
		        	button.setOnAction(event -> {
		        		bots.remove(item);
		        		Loader.deleteTemplate(item.getTemplate()._id);
		        		//loadTemplates();
		        	});
		        	HBox template_pane = new HBox(timg, name, button);
		        	template_pane.setAlignment(Pos.CENTER_LEFT);
					template_pane.setSpacing(5);
		        	HBox.setHgrow(name, Priority.ALWAYS);
					VBox pane = new VBox(template_pane);
					pane.setSpacing(3);
					for (Placer p : item.getPlacers()) {
						if (!p.isWorking()) continue; // skip lazy placers
						Label d = new Label("Delay: "+p.getSettings().delay);
						Label t = new Label("Threads: "+p.getSettings().threads);
						Label dir = new Label("Mode: "+options.get(p.getTemplate().direction));
						Pane spacer = new Pane();
						spacer.setMaxWidth(Double.MAX_VALUE);
						Button stop = new Button();
						stop.setId("button_stop");
						stop.setOnAction(event -> {
							p.kill();
							bot_list.refresh();
						});
						stop.setMaxSize(12, 12);
						stop.setPrefSize(12, 12);
						HBox proc = new HBox(d, t, dir, spacer, stop);
						proc.setId("process");
						proc.setAlignment(Pos.CENTER_LEFT);
						proc.setSpacing(4);
						//proc.setPadding(new Insets(0, 5, 0, 5));
						proc.setOnMouseClicked(e -> {
							selected_placer = p;
							direction.getSelectionModel().select(selected_placer.getTemplate().direction);
							bot_list.refresh();
						});
						HBox.setHgrow(spacer, Priority.ALWAYS);
						if (selected_placer == p)
							proc.setStyle("-fx-background-color: #16a085;-fx-background-radius: 3.0;");
						pane.getChildren().add(proc);
					}
		            setGraphic(pane);
		        }
			}
		});
		bot_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			selected_placer = null;
			selected_bot = newValue;
			title.setText(selected_bot.getTemplate().title);
			image.setText(selected_bot.getTemplate().src);
			xpos.setText(String.valueOf(selected_bot.getTemplate().x));
			ypos.setText(String.valueOf(selected_bot.getTemplate().y));
			direction.getSelectionModel().select(selected_bot.getTemplate().direction);
		});
		status.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				notifier.message("Ayumish popavsia!", NotificationType.DIALOG);
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URL("https://goo.gl/Pd360A").toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		loadTemplates();
	}
	@FXML
	protected void buttonStart() {
		BotTemplate template = new BotTemplate(title.getText(),
				image.getText(),
				Integer.parseInt(xpos.getText()),
				Integer.parseInt(ypos.getText()),
				direction.getSelectionModel().getSelectedIndex());
		BotSettings settings = new BotSettings("",
				15000,
				Integer.parseInt(delay.getText()),
				Integer.parseInt(threads.getText()),
				pixelize.isSelected(),
				false);
		Bot bot = new Bot(template, notifier);
		if (!bots.stream()
				.map(e -> e.getTemplate())
				.anyMatch(e->e.src.equals(template.src) && e.x == template.x && e.y == template.y)) { // add it if not present
			addTemplate(template);
			bots.add(bot);
			bot_list.getSelectionModel().select(bot);
			selected_bot = bot;
		}
		selected_bot.getTemplate().direction = direction.getSelectionModel().getSelectedIndex();
		selected_bot.start(settings);
		bot_list.refresh();
	}
	@FXML
	protected void buttonStop() {
		if (selected_bot != null) {
			selected_bot.kill();
			bot_list.refresh();
		}
	}
	@FXML
	protected void buttonOpen() {
		File f = chooser.showOpenDialog(null);
		image.setText(f.toURI().toString());
	}

	void dragAndDropFiles(List<File> files) {
		File f = files.get(0);
		if (!isImagePath(f.getName()))
			return;
		image.setText(f.toURI().toString());
	}
	void dragAndDropString(String s) {
		if (!isImagePath(s))
			return;
		image.setText(s);
	}
	void loadTemplates() {
		bots.clear();
		List<BotTemplate> templates = Loader.loadTemplateList();
		cacheTemplates(templates);
		templates.stream()
				.map(t -> new Bot(t, notifier))
				.forEach(bots::add);
	}
	void addTemplate(BotTemplate template) {
		Loader.addTemplate(template);
		cached_images.add(new Thumbnail(template.src));
	}
	void cacheTemplates(List<BotTemplate> b) {
		cached_images = b.stream()
				.map(e -> new Thumbnail(e.src)).collect(Collectors.toList());
	}
	Thumbnail getThumbnail(String src) {
		return cached_images.stream()
				.filter(t -> t.src.equals(src))
				.findFirst().get();
	}
	private boolean isImagePath(String name) {
		return (Arrays.stream(IMAGE_EXTENSION)
				.anyMatch(e -> getFileExtension(name).toLowerCase().equals(e)));
	}
	private String getFileExtension(String name) {
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}
}
