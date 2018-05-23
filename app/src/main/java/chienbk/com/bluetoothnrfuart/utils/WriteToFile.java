package chienbk.com.bluetoothnrfuart.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class WriteToFile {
	public static void writeLogCatToFile() {
		Utils.createLogFolder();
		writeToFile(getLogFromLogCat());
	}

	private static void writeToFile(String logData) {
		InputStream fileInputStream = null;
		FileOutputStream fileOutpurStream = null;
		File file = new File(Contans.PATH_LOG_FOLDER + File.separator
				+ "logger.txt");
		try {
			if (file.exists()) {
				fileInputStream = new FileInputStream(file);
				fileOutpurStream = new FileOutputStream(file);
				int ch;
				StringBuffer buffer = new StringBuffer();
				while ((ch = fileInputStream.read()) != -1) {
					buffer.append((char) ch);
				}
				byte data[] = new byte[(int) file.length()];
				fileInputStream.read(data);
				fileOutpurStream.write(data);
				fileOutpurStream.write(logData.getBytes(), 0,
						logData.getBytes().length);
				fileOutpurStream.flush();
			} else {
				file.createNewFile();
				fileInputStream = new FileInputStream(file);
				fileOutpurStream = new FileOutputStream(file);
				fileOutpurStream.write(logData.getBytes(), 0,
						logData.getBytes().length);
				fileOutpurStream.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
					fileOutpurStream.flush();
					fileOutpurStream.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static String getLogFromLogCat() {
		try {
			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder log = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line);
			}
			return log.toString();
		} catch (Exception e) {
			return "";
		}
	}

}