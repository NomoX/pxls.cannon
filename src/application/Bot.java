package application;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot {
	final static String WS_SEVER = "ws://78.155.217.180:1488/ws";

	private WebSocketFactory webSocketFactory;
	private WebSocket webSocket;
	private BotTemplate template;
	private Notifier notifier;

	private List<Placer> placers = new ArrayList<>();

	public Bot(BotTemplate template, Notifier notifier) {
		this.template = template;
		this.notifier = notifier;
	}
	public void start(BotSettings settings) {
		Placer placer = new Placer(webSocket, new BotTemplate(template), settings, notifier);
		Thread t = new Thread(()->{
			init(settings);
			placer.init();
			placer.start(webSocket);
		});
		t.setDaemon(true);
		t.start();
		placers.add(placer);
	}
	public void kill() {
		placers.forEach(p -> p.kill());
	}
	public List<Placer> getPlacers() {
		return placers;
	}

	public BotTemplate getTemplate() {
		return template;
	}

	private void init(BotSettings settings) {
		try {
			notifier.message("Init sockets..", NotificationType.STATUS);
			initWebSockets(settings);
		} catch (IOException | WebSocketException e) {
			notifier.message("Fail..", NotificationType.STATUS);
			e.printStackTrace();
		} finally {
			notifier.message("Succes !", NotificationType.STATUS);
		}
	}
	private void initWebSockets(BotSettings settings) throws IOException, WebSocketException {
		webSocketFactory = new WebSocketFactory();
		if (!settings.proxy.isEmpty()) {
			ProxySettings proxySettings = webSocketFactory.getProxySettings();
			proxySettings.setServer(settings.proxy);
		}
		webSocketFactory.setConnectionTimeout(settings.timeout);
		webSocket = webSocketFactory
			.createSocket(WS_SEVER)
			.addListener(new WebSocketAdapter() {
				@Override
				public void onBinaryMessage(WebSocket websocket, byte[] binary) {
					placers.forEach(p -> p.receivedData(binary));
				}
			})
			.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
		webSocket.connect();
	}
}
