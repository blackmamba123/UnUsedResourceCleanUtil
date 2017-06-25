package com.blackmamba.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	public static void copyFile(String sourceFilePath, String desFilePath) {
		File sourceFile = new File(sourceFilePath);
		if (!sourceFile.exists()) {
			return;
		}

		InputStream bis = null;
		OutputStream bos = null;
		try {
			File desFile = new File(desFilePath);
			if (!desFile.exists()) {
				desFile.createNewFile();
			}

			bis = new FileInputStream(sourceFile);
			bos = new FileOutputStream(desFile);
			byte[] bytes = new byte[4096];
			int lenth = 0;
			while ((lenth = bis.read(bytes)) != -1) {
				bos.write(bytes, 0, lenth);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}

				if (bos != null) {
					bos.close();
				}
			} catch (Exception e2) {
			}
		}
	}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		boolean deleteResult = file.delete();
		if (deleteResult) {
			System.out.println(String.format("delete file = %s  success",
					filePath));
		}
	}

	public static boolean isFileExist(String filePath) {
		if (filePath == null) {
			return false;
		}

		File file = new File(filePath);
		return file.exists();
	}
}
