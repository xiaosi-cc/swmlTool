package util;

public class ConvertUtils {

    public static byte[] short2ByteArr(short num) {
        return new byte[]{(byte) (num >>> 8), (byte) num};
    }

    public static short byteArr2Short(byte[] bArr) {
        short s = 0;
        for (byte b : bArr) {
            s <<= 8;
            s |= (b & (0xff));
        }
        return s;
    }
}
