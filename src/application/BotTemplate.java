package application;

public class BotTemplate {
	// template
	public int _id;
	public String title;
	public String src;
	public int x, y;
	public int direction;
	public BotTemplate(String title, String src, int x, int y, int direction) {
		this.title = title;
		this.src = src;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	public BotTemplate(BotTemplate t) {
		this._id = t._id;
		this.title = t.title;
		this.src = t.src;
		this.x = t.x;
		this.y = t.y;
		this.direction = t.direction;
	}
}
