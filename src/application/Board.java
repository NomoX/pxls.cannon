package application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Board {
	final static String BOARD_URL = "http://78.155.217.180:1488/boarddata";
	final static int BOARD_WIDTH = 3600;
	final static int BOARD_HEIGHT = 2200;
	
	public static byte[][] cachedBoard = null;
	
	public static byte[][] get() {
		if (cachedBoard == null) {
			cachedBoard = load();
		}
		return cachedBoard;
	}
	public static byte[][] load() {
		byte[][] board = new byte[BOARD_WIDTH][BOARD_HEIGHT];
		try {
			URL url = new URL(BOARD_URL);
			//URLConnection connection = url.openConnection();
			InputStream in = url.openStream();
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while ((bytesRead = in.read(buffer)) > 0)
			{
				output.write(buffer, 0, bytesRead);
			}
			byte[] board_data = output.toByteArray();
			/*
			int[] data = new int[board_data.length*3];
			for (int i = 0; i < board_data.length; i++) {
			int[] p = Pixel.PALETTE[board_data[i]];
			for (int j = 0; j < 3; j++) {
				data[i*3+j] = p[j];
			}
			}
			BufferedImage bim = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
			WritableRaster raster = bim.getRaster();
			raster.setPixels(0, 0, BOARD_WIDTH, BOARD_HEIGHT, data);
			*/
			for (int x = 0; x < BOARD_WIDTH; x++) {
				for (int y = 0; y < BOARD_HEIGHT; y++) {
					board[x][y] = board_data[y*BOARD_WIDTH+x];
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return board;
	}
}
