package application;

public class Pixel {
	static int[][] PALETTE = {
	           	{255,255,255,255},
	           	{228,228,228,255},
	           	{136,136,136,255},
	           	{34,34,34,255},
	           	{255,167,209,255},
	           	{229,0,0,255},
	           	{229,149,0,255},
	           	{160,106,66,255},
	           	{229,217,0,255},
	           	{148,224,68,255},
	           	{2,190,1,255},
	           	{0,211,221,255},
	           	{0,131,199,255},
	           	{0,0,234,255},
	           	{207,110,228,255},
	           	{130,0,128,255}
	};
	
	public Pixel(int x, int y, byte color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	public int x, y;
	public byte color;
}
