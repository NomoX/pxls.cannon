package application;

public class Pixel {
	static int[][] PALETTE = {
	           	{255,255,255},
	           	{228,228,228},
	           	{136,136,136},
	           	{34,34,34},
	           	{255,167,209},
	           	{229,0,0},
	           	{229,149,0},
	           	{160,106,66},
	           	{229,217,0},
	           	{148,224,68},
	           	{2,190,1},
	           	{0,211,221},
	           	{0,131,199},
	           	{0,0,234},
	           	{207,110,228},
	           	{130,0,128}
	};
	
	public Pixel(int x, int y, byte color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	public int x, y;
	public byte color;
}
