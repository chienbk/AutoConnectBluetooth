package chienbk.com.bluetoothnrfuart.utils;

import java.io.File;

/**
 * Created by ChienNV9 on 1/2/2018.
 */

public class Utils {

    /**
     *
     * @param hex
     * @return
     */
    public static int convertHexToDecimal(String hex) {

        return Integer.parseInt(hex, 16);
    }

    /**
     *
     * @param hex
     * @return
     */
    public static float convertHexToFloat(String hex) {
        Long i = Long.parseLong(hex, 16);
        return Float.intBitsToFloat(i.intValue());
    }


    /**
     * The method used to convert integer to temperature
     * @param data
     * @return temperature °C
     */
    public static int convertIntegerToCoolantTemperature(String data) {
        return convertHexToDecimal(data) - Contans.CONS_TEMPERATURE;
    }

    /**
     * The method used to convert integer to vehicle speed
     * @param data
     * @return speed (km/h)
     */
    public static int convertIntegerToVehicleSpeed(String data) {
        return convertHexToDecimal(data);
    }

    /**
     * The method used to convert integer to vehicle speed
     * @param data
     * @return rpm
     */
    public static int convertIntegerToEngineRPM(String data){
        return convertHexToDecimal(data)/4;
    }

    /**
     * The method used to convert integer to fuel tank
     * @param data
     * @return percent (%)
     */
    public static int convertIntegerToFuelTank(String data) {
        return convertHexToDecimal(data)*100/255;
    }

    public static String convertDataReceiveToString(String data) {
        String newData = data.replaceAll(">", "");
        newData = newData.replaceAll("\\s+","");
        return newData.substring(4, newData.length());
    }

    static boolean createLogFolder() {
        try {
            String logFolder = Contans.PATH_LOG_FOLDER;
            // delete if exist
            // deleteFileOrFolder(new File(logFolder));
            return createFolder(logFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createFolder(String path) {
        try {
            File folder = new File(path);
            if (!folder.exists()) {
                return folder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
