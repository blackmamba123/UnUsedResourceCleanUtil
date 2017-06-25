package com.blackmamba.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import check.ImageResourceFormatCheck;

public class UnUsedResourceCleanUtil {
	/** 生成的相关文件的路径  (包括 每个module 无效资源的清单 以及 备份) **/
	public static String DES_DIR = "D:/UnUsedResourceBackup";
	
	/** 白名单资源文件清单 程序默认会从 DES_DIR 根目录下去查找 */
	public static final String IGNORE_FILE_NAME = "ignore_delete_resource.txt";

	/** 工程根目录 */
	private static String PROJECT_ROOT_PATH = "C:/project_solution/trunk/"; 
	private static final String OUTPUT_LINT_RESULT_PATH = "/build/outputs/lint-results-debug.xml";
	
	/** 模块名称 */
	//private static final String[] MODULE_NAMES = new String[] {"CCVoice", "cc-common", "cc-library", "cc-player","cc-record", "cc-widget", "mpay-lib"};
	private static final String[] MODULE_NAMES = new String[] {"CCVoice"};

	public static void cleanUnUsedResource() {
		for(int i = 0 ; i< MODULE_NAMES.length ; i++){
			cleanUnUseResourceByModuleName(MODULE_NAMES[i],true);//设置为true  会删掉工程中的无效资源 而false 只是备份和列出无效资源清单
		}
	}
	
	public static void checkImageFormat(){
		for(int i = 0 ; i< MODULE_NAMES.length ; i++){
			ImageResourceFormatCheck.checkImageFormat(PROJECT_ROOT_PATH  + MODULE_NAMES[i] + "/" + "res" + "/");
		}
	}
	
	/**
	 * 按模块名称 清理模块无效资源
	 * @param moduleName
	 */
	private static void cleanUnUseResourceByModuleName(String moduleName, boolean isDelete){
		System.out.println("cleanUnUseResourceByModuleName moduleName == " + moduleName);
		String moduleLintXmlPath = PROJECT_ROOT_PATH + moduleName + OUTPUT_LINT_RESULT_PATH;
		if(!FileUtils.isFileExist(moduleLintXmlPath)){
			System.out.println("cleanUnUseResourceByModuleName moduleLintXmlPath  not exist  " + moduleLintXmlPath);

			return;
		}
		
        List<ResourceModel> unUsedResoureModelList = LintXmlFileParseUtil.getUnUsedResourceModelList(moduleName,moduleLintXmlPath);
		
		for(ResourceModel model : unUsedResoureModelList){
			if(model.mResourceType == ResourceModel.TYPE_FILE){//layout drawable等 可以直接删除图片
				copyAndDeleteFile(moduleName, model.mResourceAbsolutePath, isDelete);
			}else{
				
				if(!model.mResourceAbsolutePath.contains("styles.xml")){
					copyAndDeleteValuesResource(model, moduleName,isDelete);
				}
			}
		}
	}
	
	/**
	 * 备份并删除资源文件
	 */
	private static void copyAndDeleteFile(String moduleName, String resourcePath, boolean isDelete){
		  String desFilePath = getAndCreateDesFileBySourcePath(moduleName,resourcePath);
          FileUtils.copyFile(resourcePath, desFilePath);
          
          if(isDelete){
             FileUtils.deleteFile(resourcePath);
          }
	}
	
	/**
	 * 备份并删除 value 类型资源
	 */
	private static void copyAndDeleteValuesResource(ResourceModel model, String moduleName, boolean isDelete){
		String resourcePreFlag = getPreFlagByReourcePath(model.mFileName);
		if(resourcePreFlag == null){
			System.out.println(String.format("copyAndDeleteValuesResource resource not support to detete resource name  %s", model.toString()));
			return ;
		}
		
		File file = new File(model.mResourceAbsolutePath);
		if(!file.exists()){
			return;
		}
		
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		List<String> readList = new ArrayList<>();
		try{
			bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			while((line = bufferedReader.readLine()) != null){
				System.out.println("read line = " + line);
				if(isReadLineEqualResourceName(line,model.mResourceName,resourcePreFlag)){
					backupFindValueResource(line, getAndCreateDesFileBySourcePath(moduleName, model.mResourceAbsolutePath));
					System.out.println("find -------------------- read line = " + line);
					if(isDelete){
					  continue;
					}
				}
				
				readList.add(line);
			}
		}catch(Exception ex){
			System.out.println("read line exception " + ex.toString());
		}finally {
			if(bufferedReader != null){
				try{
					bufferedReader.close();
				}catch(Exception e){
					
				}
			}
		}
		
		if(!isDelete){
			return;
		}
		
		try{
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			for(String conent : readList){
				bufferedWriter.write(conent);
				bufferedWriter.newLine();
			}
			
			bufferedWriter.flush();
		}catch(Exception ex){
			System.out.println("read line exception " + ex.toString());
		}finally {
			if(bufferedWriter != null){
				try{
					bufferedWriter.close();
				}catch(Exception e){
					
				}
			}
		}
	
}
	
	/**
	 * 判断values资源文件 是否
	 * @param readLine
	 * @param resourceName
	 * @return
	 */
	private static boolean isReadLineEqualResourceName(String readLine, String resourceName, String preFlag){
		if(readLine.trim() == null || resourceName == null || preFlag == null){
			return false;
		}
		
		readLine = readLine.trim();
		int index = readLine.indexOf(preFlag);
		if(index == -1 ){
			return false;
		}
		
		readLine = readLine.substring(index + String.valueOf(preFlag).length());
		index = readLine.indexOf("\">");
		if(index == -1){
			return false;
		}
		
		readLine = readLine.substring(0, index);
		
		return resourceName.equals(readLine);
	}
	
	private static String getPreFlagByReourcePath(String fileName){
		if(fileName == null){
			return null;
		}
		
		if(fileName.contains("strings")){
			return "<string name=\"";
		}else if(fileName.contains("color")){
			return "<color name=\"";
		}else if(fileName.contains("dimens")){
			return "<dimen name=\"";
		}else{
			return null;
		}
	}
	
	private static void backupFindValueResource(String content, String filePath){
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			bufferedWriter.write(content);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			
		}
	}
	

	
	/**
	 * 根据源文件路径 返回目标文件路径
	 * @param sourcePath
	 * @return
	 */
	private static String getAndCreateDesFileBySourcePath(String moduleName, String sourcePath){
		int lastIndex = sourcePath.lastIndexOf('\\');
		String fileName = sourcePath.substring(lastIndex +1);
		sourcePath = sourcePath.substring(0, lastIndex);
		lastIndex = sourcePath.lastIndexOf('\\');
		String desCategroy = sourcePath.substring(lastIndex +1);
		File desCategoryDir = new File(DES_DIR + "\\" + moduleName + "\\" + desCategroy);
		if(!desCategoryDir.exists()){
			desCategoryDir.mkdirs();
		}
		
		String desFilePath = desCategoryDir + "\\" + fileName;
		
		return desFilePath;
	}
}
