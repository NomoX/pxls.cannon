package application;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Board {
	final static String BOARD_URL = "http://78.155.217.180:1488/boarddata";
	final static int BOARD_WIDTH = 3600;
	final static int BOARD_HEIGHT = 2200;
	
	public static BufferedImage cachedBoard = null;
	
	public static BufferedImage get() {
		if (cachedBoard == null) {
			cachedBoard = load();
		}
		return cachedBoard;
	}
	public static BufferedImage load() {
		BufferedImage board = null;
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

			FileOutputStream fos = new FileOutputStream(new File("D:/zzzzdata"));
			fos.write(board_data, 0, board_data.length);
			fos.flush();
			fos.close();
			
			int[] data = new int[board_data.length*3];
			for (int i = 0; i < board_data.length; i++) {
				int[] p = Pixel.PALETTE[board_data[i]];
				//System.out.println(String.format("R: %d G: %d B: %d Idx: %d", p[0], p[1], p[2], board_data[i]));
				for (int j = 0; j < 3; j++) {
					data[i*3+j] = p[j];
				}
			}
			board = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
			WritableRaster raster = board.getRaster();
			raster.setPixels(0, 0, BOARD_WIDTH, BOARD_HEIGHT, data);
			//board.setRGB(0, 0, BOARD_WIDTH, BOARD_HEIGHT, data, 0, BOARD_WIDTH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return board;
	}
}
