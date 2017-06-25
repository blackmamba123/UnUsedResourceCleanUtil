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
	/** ���ɵ�����ļ���·��  (���� ÿ��module ��Ч��Դ���嵥 �Լ� ����) **/
	public static String DES_DIR = "D:/UnUsedResourceBackup";
	
	/** ��������Դ�ļ��嵥 ����Ĭ�ϻ�� DES_DIR ��Ŀ¼��ȥ���� */
	public static final String IGNORE_FILE_NAME = "ignore_delete_resource.txt";

	/** ���̸�Ŀ¼ */
	private static String PROJECT_ROOT_PATH = "C:/project_solution/trunk/"; 
	private static final String OUTPUT_LINT_RESULT_PATH = "/build/outputs/lint-results-debug.xml";
	
	/** ģ������ */
	//private static final String[] MODULE_NAMES = new String[] {"CCVoice", "cc-common", "cc-library", "cc-player","cc-record", "cc-widget", "mpay-lib"};
	private static final String[] MODULE_NAMES = new String[] {"CCVoice"};

	public static void cleanUnUsedResource() {
		for(int i = 0 ; i< MODULE_NAMES.length ; i++){
			cleanUnUseResourceByModuleName(MODULE_NAMES[i],true);//����Ϊtrue  ��ɾ�������е���Ч��Դ ��false ֻ�Ǳ��ݺ��г���Ч��Դ�嵥
		}
	}
	
	public static void checkImageFormat(){
		for(int i = 0 ; i< MODULE_NAMES.length ; i++){
			ImageResourceFormatCheck.checkImageFormat(PROJECT_ROOT_PATH  + MODULE_NAMES[i] + "/" + "res" + "/");
		}
	}
	
	/**
	 * ��ģ������ ����ģ����Ч��Դ
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
			if(model.mResourceType == ResourceModel.TYPE_FILE){//layout drawable�� ����ֱ��ɾ��ͼƬ
				copyAndDeleteFile(moduleName, model.mResourceAbsolutePath, isDelete);
			}else{
				
				if(!model.mResourceAbsolutePath.contains("styles.xml")){
					copyAndDeleteValuesResource(model, moduleName,isDelete);
				}
			}
		}
	}
	
	/**
	 * ���ݲ�ɾ����Դ�ļ�
	 */
	private static void copyAndDeleteFile(String moduleName, String resourcePath, boolean isDelete){
		  String desFilePath = getAndCreateDesFileBySourcePath(moduleName,resourcePath);
          FileUtils.copyFile(resourcePath, desFilePath);
          
          if(isDelete){
             FileUtils.deleteFile(resourcePath);
          }
	}
	
	/**
	 * ���ݲ�ɾ�� value ������Դ
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
	 * �ж�values��Դ�ļ� �Ƿ�
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
	 * ����Դ�ļ�·�� ����Ŀ���ļ�·��
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
