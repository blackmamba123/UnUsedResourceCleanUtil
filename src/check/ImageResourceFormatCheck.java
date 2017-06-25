package check;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageResourceFormatCheck {
	private static final String[] DRAWABLE_PATHS = new String[] {
			"drawable-hdpi", "drawable-ldpi", "drawable-mdpi",
			"drawable-xhdpi", "drawable-xxhdpi" };

	public static void checkImageFormat(String path) {
		System.out.println("checkImageFormat path == " + path);
		for (int i = 0; i < DRAWABLE_PATHS.length; i++) {
			checkDirImageFormat(path + DRAWABLE_PATHS[i]);
		}
	}

	private static void checkDirImageFormat(String dirPath){
	System.out.println("checkDirImageFormat dirPath == ------------------------------" + dirPath + "--------------------------------------");
	
	File dirFile = new File(dirPath);
	if(!dirFile.exists() || !dirFile.isDirectory()){
		System.out.println("checkDirImageFormat file not exist dirPath == " + dirPath);
		return;
	}
	
	File[] imageFiles = dirFile.listFiles();
	for(File file : imageFiles){
		String imageType = getImageFileFormat(file);
		String fileName = file.getName();
		if(fileName != null && imageType != null){
			if(!fileName.contains(imageType)){
				System.out.println(String.format("::::::: not right image name with type fileName = %s ------- imageType =%s", file.getName(), imageType));
			}
		}else{
			System.out.println(String.format("------------------null type filePath = %s ------- imageType =%s", file.getAbsolutePath(), imageType));

		}
		
		//System.out.println(String.format("checkDirImageFormat fileName = %s ------- imageType =%s", file.getName(), imageType));
		
	}
}

	private static String getImageFileFormat(File file) {
		try {
			return getImageType(file);
		} catch (Exception ex) {

		}

		return "";
	}

	private static String getImageType(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] bytes = new byte[8];
			in.read(bytes);

			String type = getImageType(bytes);
			return type;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static String getImageType(byte[] bytes) {
		if (isJPEG(bytes)) {
			return "jpg";
		}
		if (isGIF(bytes)) {
			return "gif";
		}
		if (isPNG(bytes)) {
			return "png";
		}
		if (isBMP(bytes)) {
			return "bmp";
		}

		return null;
	}

	private static boolean isJPEG(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
	}

	/**
	 * ÅÐ¶ÏÊÇ·ñÎªgifÍ¼Æ¬
	 * 
	 * @param b
	 * @return
	 */
	private static boolean isGIF(byte[] b) {
		if (b.length < 6) {
			return false;
		}
		return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
				&& (b[4] == '7' || b[4] == '9') && b[5] == 'a';
	}

	/**
	 * ÅÐ¶ÏÊÇ·ñÎªpngÍ¼Æ¬
	 * 
	 * @param b
	 * @return
	 */
	private static boolean isPNG(byte[] b) {
		if (b.length < 8) {
			return false;
		}
		return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
				&& b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
				&& b[6] == (byte) 26 && b[7] == (byte) 10);
	}

	/**
	 * ÅÐ¶ÏÊÇ·ñÎªbmpÍ¼Æ¬
	 * 
	 * @param b
	 * @return
	 */
	private static boolean isBMP(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == 0x42) && (b[1] == 0x4d);
	}

}
