package application;

import javafx.scene.image.Image;

public class Thumbnail {
	final static int SIZE = 60;
	public String src;
	public Image image;
	public Thumbnail(String src) {
		this.src = src;
		this.image = new Image(src, SIZE, SIZE, false, false);
	}
}
