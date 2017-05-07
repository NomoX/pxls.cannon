package application;

import java.io.IOException;

import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

public class Bot {
	final static String WS_SEVER = "ws://78.155.217.180:1488/ws"; 
	
	WebSocketFactory webSocketFactory;
	WebSocket webSocket;
	BotSettings settings;
	BotTemplate template;
	Placer placer;
	public Bot(BotSettings settings, BotTemplate template) {
		this.settings = settings;
		this.template = template;
		this.placer = new Placer(template);
	}
	public void start() {
		try {
			System.out.println("Init sockets..");
			initWebSockets();
			System.out.println("Succes !");
			placer.delay = settings.delay;
			placer.threads = settings.threads;
			placer.pixelize = settings.pixelize;
			placer.start(webSocket);
		} catch (IOException | WebSocketException e) {
			e.printStackTrace();
		}
	}
	
	private void initWebSockets() throws IOException, WebSocketException {
		webSocketFactory = new WebSocketFactory();	
		if (!settings.proxy.equals("")) {
			ProxySettings proxySettings = webSocketFactory.getProxySettings();
			proxySettings.setServer(settings.proxy);
		}
		webSocketFactory.setConnectionTimeout(settings.timeout);
		webSocket = webSocketFactory
			.createSocket(WS_SEVER)
			.addListener(new WebSocketAdapter() {
				/*
				@Override
				public void onTextMessage(WebSocket websocket, String message) {
					System.out.println(message);
				}*/
				@Override
				public void onBinaryMessage(WebSocket websocket, byte[] binary) {
					placer.receivedData(binary);
					//System.out.print("len " + binary.length);
					//System.out.println(" " + binary[0]);
				}
			})
			.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
		webSocket.connect();
	}
}
