package com.blackmamba.resource;

public class ResourceModel {
	public static int TYPE_FILE = 1;//所有可以直接删除的文件类型 (包括图片 selector layout drawable anim)
	public static int TYPE_VALUES = 2;//values 类型资源 包括string color dimens
	
	public int mResourceType;
	public String mFileName;
	public String mCategoryName;
	public String mResourceAbsolutePath;

	public String mResourceName; //values类型才有 对应<string name = "resoure_name"></string> 中resoure_name
	
	/** 可以直接删除文件的资源目录 (drawable layout anim 等) */
	private static String[] TYPE_FILE_CATEGORY = new String[] { "drawable-xhdpi", "drawable-hdpi", "layout", "drawable","animator", "anim", "color" };
	
	/** 不能直接删除文件的资源 values 目录下 string color dimens等 */
	private static String TYPE_VALUES_CATEGORY = "values"; 
	
	public static ResourceModel createResourceModelResourceAbsolutePath(String filePath, String resourceName){
		if(filePath == null ||filePath.trim().length() == 0){
			return null;
		}
		
		ResourceModel model = new ResourceModel();
		model.mResourceAbsolutePath = filePath;
		int lastIndex = filePath.lastIndexOf('\\');
		model.mFileName = filePath.substring(lastIndex +1);
		filePath = filePath.substring(0, lastIndex);
		lastIndex = filePath.lastIndexOf('\\');
		model.mCategoryName = filePath.substring(lastIndex +1);
		
		if(model.mCategoryName.contains(TYPE_VALUES_CATEGORY)){
			model.mResourceType = TYPE_VALUES;
			model.mResourceName = resourceName;
		}else{
			model.mResourceType = TYPE_FILE;
		}
		
		return model;
	}
	
	@Override
	public String toString() {
		return String.format("type = %s  Category = %s fileName = %s ", mResourceType, mCategoryName, mFileName);
	}
}
