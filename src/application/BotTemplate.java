package application;

public class BotTemplate {
	// template
	public String title;
	public String src;
	public int x, y;
	public int direction;
	public BotTemplate(String title, String src, int x, int y, int direction) {
		super();
		this.title = title;
		this.src = src;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
}
