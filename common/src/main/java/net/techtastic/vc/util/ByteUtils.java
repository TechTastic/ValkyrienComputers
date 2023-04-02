package net.techtastic.vc.util;

public abstract class ByteUtils {
    public static Byte[] shortToByteArray(short num) {
        Byte[] ret = new Byte[2];
        ret[0] = (byte)(num & 0xff);
        ret[1] = (byte)((num >> 8) & 0xff);
        return ret;
    }
}
