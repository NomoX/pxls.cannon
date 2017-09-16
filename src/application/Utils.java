package application;

public class Utils {
	public static byte getByte(byte[] data, int offset) {
		return data[offset];
	}
	public static int getInt(byte[] data, int offset) {
		return data[offset+0] & 0xFF |
				(data[offset+1] & 0xFF) << 8 |
				(data[offset+2] & 0xFF) << 16 |
				(data[offset+3] & 0xFF) << 24;
	}
	public static void setByte(byte[] data, int offset, byte b) {
		data[offset] = b;
	}
	public static void setInt(byte[] data, int offset, int i) {
		data[offset]   = (byte)(0xff & i);
	    data[offset+1] = (byte)(0xff & (i >> 8));
	    data[offset+2] = (byte)(0xff & (i >> 16));
	    data[offset+3] = (byte)(0xff & (i >> 24));
	}
}
