package com.blackmamba.resource;

public class ResourceModel {
	public static int TYPE_FILE = 1;//���п���ֱ��ɾ�����ļ����� (����ͼƬ selector layout drawable anim)
	public static int TYPE_VALUES = 2;//values ������Դ ����string color dimens
	
	public int mResourceType;
	public String mFileName;
	public String mCategoryName;
	public String mResourceAbsolutePath;

	public String mResourceName; //values���Ͳ��� ��Ӧ<string name = "resoure_name"></string> ��resoure_name
	
	/** ����ֱ��ɾ���ļ�����ԴĿ¼ (drawable layout anim ��) */
	private static String[] TYPE_FILE_CATEGORY = new String[] { "drawable-xhdpi", "drawable-hdpi", "layout", "drawable","animator", "anim", "color" };
	
	/** ����ֱ��ɾ���ļ�����Դ values Ŀ¼�� string color dimens�� */
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
