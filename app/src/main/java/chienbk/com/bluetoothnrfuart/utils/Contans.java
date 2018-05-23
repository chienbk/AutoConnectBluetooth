package chienbk.com.bluetoothnrfuart.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Chienbk on 12/19/2017.
 */

public class Contans {

    //Tốc độ xe
    public static final String VERHICLE_SPEED = " 01 0D\n";
    public static final String CONS_VERHICLE_SPEED = "410D";
    //Vòng tua máy
    public static final String ENGINE_RPM = "01 0C\n";
    public static final String CONS_ENGINE_RPM = "410C";
    //Nhiệt độ làm mát động cơ
    public static final String ENGINE_COOLANT_TEMPERATURE = "01 05\n";
    public static final String CONS_ENGINE_COOLANT_TEMPERATURE = "4105";
    //Nhiên liệu đầu vào
    public static final String FUEL_TANK_LEVEL_INPUT = "01 2F\n";
    public static final String CONS_FUEL_TANK_LEVEL_INPUT = "412F";

    public static final int CONS_TEMPERATURE = 40;

    public static final String APP_DATA_FOLDER_NAME = "K-Doctor";

    private static final String PATH_SDCARD = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    static final String PATH_LOG_FOLDER = PATH_SDCARD + File.separator
            + APP_DATA_FOLDER_NAME + File.separator + "log";
}
