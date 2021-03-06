package application;

public class BotSettings {
	public String proxy;
	public int timeout;
	public int delay;
	public int threads;
	public boolean pixelize;
	public boolean defend;
	public BotSettings(String proxy, int timeout, int delay, int threads, boolean pixelize, boolean defend) {
		this.proxy = proxy;
		this.timeout = timeout;
		this.delay = delay;
		this.threads = threads;
		this.pixelize = pixelize;
		this.defend = defend;
	}
}
