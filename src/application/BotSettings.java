package application;

public class BotSettings {
	public String proxy;
	public int timeout;
	public int delay;
	public int threads;
	public boolean pixelize;
	public BotSettings(String proxy, int timeout, int delay, int threads, boolean pixelize) {
		this.proxy = proxy;
		this.timeout = timeout;
		this.delay = delay;
		this.threads = threads;
		this.pixelize = pixelize;
	}
}
