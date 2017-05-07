package application;

public class Utils {
	public static byte getByte(byte[] d, int o) {
		return d[o];
	}
	public static int getInt(byte[] d, int o) {
		return d[o+0] & 0xFF |
				(d[o+1] & 0xFF) << 8 |
				(d[o+2] & 0xFF) << 16 |
				(d[o+3] & 0xFF) << 24;
	}
	public static void setByte(byte[] d, int o, byte b) {
		d[o] = b;
	}
	public static void setInt(byte[] d, int o, int i) {
		d[o]   = (byte)(0xff & i);
	    d[o+1] = (byte)(0xff & (i >> 8));
	    d[o+2] = (byte)(0xff & (i >> 16));
	    d[o+3] = (byte)(0xff & (i >> 24));
	}
}
