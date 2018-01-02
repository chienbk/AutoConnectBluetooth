package chienbk.com.bluetoothnrfuart.utils;

/**
 * Created by ChienNV9 on 1/2/2018.
 */

public class Utils {

    public static int convertHexToDecimal(String hex) {

        return Integer.parseInt(hex, 16);
    }

    public static float convertHexToFloat(String hex) {
        Long i = Long.parseLong(hex, 16);
        return Float.intBitsToFloat(i.intValue());
    }


}
