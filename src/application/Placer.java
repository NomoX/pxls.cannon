package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.neovisionaries.ws.client.WebSocket;

public class Placer {
	WebSocket ws = null;
	
	BotTemplate botTemplate;
	BotNotifier notifier;
	
	byte[][] board;
	byte[][] template;
	int template_w, template_h;
	
	Pixel pendingPixel;
	
	int delay = 10;
	int threads = 10;
	
	boolean pixelize = true;
	
	public Placer(BotTemplate botTemplate, BotNotifier notifier) {
		this.botTemplate = botTemplate;
		this.notifier = notifier;
		System.out.println("Downloading board data..");
		notifier.status("Downloading board data..");
		board = Board.get();
		try {
			System.out.println("Downloading template data..");
			notifier.status("Downloading template data..");
			BufferedImage template_image = ImageIO.read(new URL(botTemplate.src));
			template_w = template_image.getWidth();
			template_h = template_image.getHeight();
			template = convertImage(template_image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		notifier.status("Done!");
	}

	public void receivedData(byte[] data) {
		switch (Utils.getByte(data, 0)) {
		case 10: // pending pixel
			int x = Utils.getInt(data, 5);
			int y = Utils.getInt(data, 9);
			byte c = Utils.getByte(data, 13);
			updatePixel(new Pixel(x, y, c));
			break;
		default:
			break;
		}
	}
	
	public void start(WebSocket ws) {
		this.ws = ws;
		for (int i = 0; i < threads; i++) {
			Thread t = new Thread(() -> {
				while (true) {
					drawPixel();
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
			System.out.println("Thread started ID: " + t.getId());
		}
	}
	private void drawPixel() {
		switch(botTemplate.direction) {
		case 0:
			Random r = new Random();
			Pixel p = checkPixel(r.nextInt(template_w), r.nextInt(template_h));
			if (p != null) {putPixel(p);return;}
			List<Pixel> points = new ArrayList<Pixel>();
			for (int x = 0; x < template_w; x++) {
				for (int y = 0; y < template_h; y++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel != null)
						points.add(pixel);
				}
			}
			if (points.size() > 0)
				putPixel(points.get(r.nextInt(points.size())));
				
			break;
		case 1:
			for (int x = 0; x < template_w; x++) {
				for (int y = 0; y < template_h; y++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 2:
			for (int x = template_w - 1; x > 0 ; x--) {
				for (int y = 0; y < template_h; y++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 3:
			for (int y = 0; y < template_h; y++) {
				for (int x = 0; x < template_w; x++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 4:
			for (int y = template_h - 1; y > 0 ; y--) {
				for (int x = 0; x < template_w; x++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 5:
			for (int x = 0; x < template_w; x++) {
				for (int y = x % 2; y < template_h; y+=2) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			for (int x = 0; x < template_w; x++) {
				for (int y = 1-(x % 2); y < template_h; y+=2) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		}
	}
	private Pixel checkPixel(int x, int y) {
		int bx = x + botTemplate.x;
		int by = y + botTemplate.y;
		byte pb = board[bx][by];
		byte pt = template[x][y];

		if (pt == -1)
			return null;
		if (pt != pb) {
			return new Pixel(bx, by, pt);
		}
		return null;
	}
	private int[] getImagePixel(BufferedImage img, int x, int y) {
		int rgb = img.getRGB(x, y);
		Color c = new Color(rgb, true);
		return new int[] {c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()};
	}
	private void putPixel(Pixel p) {
		updatePixel(p);
		byte[] d = new byte[10];
		Utils.setByte(d, 0, (byte) 10);
		Utils.setInt(d, 1, p.x);
		Utils.setInt(d, 5, p.y);
		Utils.setByte(d, 9, p.color);
		ws.sendBinary(d);
	}
	private void updatePixel(Pixel p) {
		board[p.x][p.y] = p.color;
	}
	private boolean RGBEquals(int[] a, int[] b) {
		return (a[0] == b[0] &&
				a[1] == b[1] &&
				a[2] == b[2]);
	}
	private byte getColorIndex(int[] rgb) {
		for (byte i = 0; i < Pixel.PALETTE.length; i++)
			if (RGBEquals(Pixel.PALETTE[i], rgb))
				return i;
		return -1;
	}
	int[] nearesColors(int[] color) {
		// TODO ignore alpha
		List<Integer> ar = new ArrayList<>();
		for (int i = 0; i < Pixel.PALETTE.length; i++) {
			int d = colorDistance(Pixel.PALETTE[i], color);
			ar.add(d);
		}
		int m = listMinIndex(ar);
		return Pixel.PALETTE[m];
	}
	int listMinIndex(List<Integer> a) {
		int m = a.get(0);
		int mi = 0;
		for (int i = 0; i < a.size(); i++)
			if (a.get(i) < m) {
				m = a.get(i);
				mi = i;
			}
		return mi;
	}
	int colorDistance(int[] a, int[] b) {
		return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2]);
	}
	private byte[][] convertImage(BufferedImage bi) {
		byte[][] img = new byte[bi.getWidth()][bi.getHeight()];
		for (int x = 0; x < bi.getWidth(); x++) {
			for (int y = 0; y < bi.getHeight(); y++) {
				int[] p = getImagePixel(bi, x, y);
				if (p[3] <= 127) {
					img[x][y] = -1;
					continue;
				}
				if (pixelize)
					p = nearesColors(p);
				byte c = getColorIndex(p);
				if (c == -1)
					System.out.println(String.format("incorrect color at (%d, %d)", x, y));
				img[x][y] = c;
			}
		}
		return img;
	}
}
