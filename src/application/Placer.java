package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
	
	BufferedImage board;
	BufferedImage template;
	
	Pixel pendingPixel;
	
	int delay = 50;
	int threads = 1;
	
	boolean pixelize = true;
	
	public Placer(BotTemplate botTemplate) {
		this.botTemplate = botTemplate;
		System.out.println("Upload board data..");
		board = Board.get();
		try {
			System.out.println("Upload template data..");
			template = ImageIO.read(new URL(botTemplate.src));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
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
			List<Pixel> points = new ArrayList<>();
			for (int x = 0; x < template.getWidth(); x++) {
				for (int y = 0; y < template.getHeight(); y++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel != null)
						points.add(pixel);
				}
			}
			if (points.size() > 0)
				putPixel(points.get(new Random().nextInt(points.size())));
				
			break;
		case 1:
			for (int x = 0; x < template.getWidth(); x++) {
				for (int y = 0; y < template.getHeight(); y++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 2:
			for (int x = template.getWidth() - 1; x > 0 ; x--) {
				for (int y = 0; y < template.getHeight(); y++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 3:
			for (int y = 0; y < template.getHeight(); y++) {
				for (int x = 0; x < template.getWidth(); x++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 4:
			for (int y = template.getHeight() - 1; y > 0 ; y--) {
				for (int x = 0; x < template.getWidth(); x++) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			break;
		case 5:
			for (int x = 0; x < template.getWidth(); x++) {
				for (int y = x % 2; y < template.getHeight(); y+=2) {
					Pixel pixel = checkPixel(x, y);
					if (pixel == null) continue;
					else {putPixel(pixel);return;}
				}
			}
			for (int x = 0; x < template.getWidth(); x++) {
				for (int y = 1-(x % 2); y < template.getHeight(); y+=2) {
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
		int[] pt = getImagePixel(template, x, y);
		int[] pb = getImagePixel(board, bx, by);
		
		if (pt[3] <= 127)
			return null;
		if (pixelize)
			pt = nearesColors(pt);
		if (!RGBEquals(pt, pb)) {
			int c = getColorIndex(pt);
			if (c == -1) {
				System.err.println("Incorrect color !");
				return null;
			}
			return new Pixel(bx, by, (byte) c);
		}
		return null;
	}
	private int[] getImagePixel(BufferedImage img, int x, int y) {
		int rgb = img.getRGB(x, y);
		Color c = new Color(rgb, true);
		//return new int[] {(rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF};
		return new int[] {c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()};
	}
	private void putPixel(Pixel p) {
		updatePixel(p); // TODO test
		byte[] d = new byte[10];
		Utils.setByte(d, 0, (byte) 10);
		Utils.setInt(d, 1, p.x);
		Utils.setInt(d, 5, p.y);
		Utils.setByte(d, 9, p.color);
		ws.sendBinary(d);
	}
	private void updatePixel(Pixel p) {
		WritableRaster raster = board.getRaster();
		raster.setPixel(p.x, p.y, Pixel.PALETTE[p.color]);
	}
	private boolean RGBEquals(int[] a, int[] b) {
		return (a[0] == b[0] &&
				a[1] == b[1] &&
				a[2] == b[2]);
	}
	private int getColorIndex(int[] rgb) {
		for (int i = 0; i < Pixel.PALETTE.length; i++)
			if (RGBEquals(Pixel.PALETTE[i], rgb))
				return i;
		return -1;
	}
	int[] nearesColors(int[] color) {
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
}
