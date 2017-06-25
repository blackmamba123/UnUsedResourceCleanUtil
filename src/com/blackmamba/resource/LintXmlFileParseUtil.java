package com.blackmamba.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LintXmlFileParseUtil {
	/** lint 扫描出的 无用的资源文件的标记 */
	private static final String UNUSE_RESOURCE_TAG = "UnusedResources";
	
	private static List<String> mIgnoreList;

	public static List<ResourceModel> getUnUsedResourceModelList(String moduleName, String filePath) {
		List<ResourceModel> resourceModelList = new ArrayList<>();
		if (mIgnoreList == null) {
			mIgnoreList = getIgnoreFileList();
		}

		BufferedWriter bufferedWriter = null;
		try {

			File desDir = new File(UnUsedResourceCleanUtil.DES_DIR + "\\" + moduleName);
			if (!desDir.exists()) {
				desDir.mkdirs();
			}

			File unUsedResourceListFile = new File(UnUsedResourceCleanUtil.DES_DIR + "\\" + moduleName,
					moduleName + "_unused_resource.txt");
			if (!unUsedResourceListFile.exists()) {
				unUsedResourceListFile.createNewFile();
			}

			bufferedWriter = new BufferedWriter(new FileWriter(unUsedResourceListFile));

			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream input = new FileInputStream(filePath);
			Document document = docBuilder.parse(input);
			Element rootElement = document.getDocumentElement();
			NodeList rootNodeList = rootElement.getChildNodes();

			if (rootNodeList != null) {
				System.out.println(String.format("rootNode child size = %s", rootNodeList.getLength()));
				int size = rootNodeList.getLength();
				for (int i = 0; i < size; i++) {
					Node node = rootNodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						String message = node.getAttributes().getNamedItem("message").getNodeValue();
						String id = node.getAttributes().getNamedItem("id").getNodeValue();
						if (!UNUSE_RESOURCE_TAG.equals(id)) {
							continue;
						}

						NodeList childNodeList = node.getChildNodes();
						if (childNodeList != null) {
							for (int j = 0; j < childNodeList.getLength(); j++) {
								Node childNode = childNodeList.item(j);
								if (childNode.getNodeType() == Node.ELEMENT_NODE) {
									String unUsedFilePath = childNode.getAttributes().getNamedItem("file")
											.getNodeValue();

									String resourceName = parseResourceNameFromMessageAttribute(message);
									ResourceModel model = ResourceModel.createResourceModelResourceAbsolutePath(unUsedFilePath, resourceName);
									if (model == null) {
										System.out.println("createResourceModelResourceAbsolutePath null path = "
												+ unUsedFilePath);
										continue;
									}

									if (canAddToDeleteList(model)) {
										resourceModelList.add(model);
										bufferedWriter.write(unUsedFilePath);
									} else {
										bufferedWriter.write(unUsedFilePath + "----------------ignore can not delete");
									}

									bufferedWriter.newLine();

								}
							}
						}
					}
				}
			}

			bufferedWriter.flush();

		} catch (Exception ex) {
			System.out.println(ex.toString());
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return resourceModelList;
	}

	/**
	 * 获取白名单资源列表
	 * 
	 * @return
	 */
	private static List<String> getIgnoreFileList() {
		List<String> ignoreList = new ArrayList<>();
		String ignoreFilePath = UnUsedResourceCleanUtil.DES_DIR + "\\" + UnUsedResourceCleanUtil.IGNORE_FILE_NAME;
		File ignoreFile = new File(ignoreFilePath);
		if (!ignoreFile.exists()) {
			return ignoreList;
		}

		BufferedReader bufferedReader = null;
		try {
			InputStreamReader inputReader = new InputStreamReader(new FileInputStream(ignoreFile));
			bufferedReader = new BufferedReader(inputReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println("line = " + line);
				if (line.startsWith("#") || line.length() == 0) {
					continue;
				}

				ignoreList.add(line);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
		}

		for (String ignorePath : ignoreList) {
			System.out.println("ignorePath = " + ignorePath);
		}

		return ignoreList;

	}

	private static boolean canAddToDeleteList(ResourceModel model) {
		if (mIgnoreList == null) {
			return false;
		}

		for (int i = 0; i < mIgnoreList.size(); i++) {
			if (model.mResourceAbsolutePath.contains(mIgnoreList.get(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 从xml message 属性值中 获取资源文件的名称
	 * 
	 * @return
	 */
	private static String parseResourceNameFromMessageAttribute(String message) {

		if (message == null || message.trim() == "") {
			return "";
		}

		int index = message.indexOf('`');
		message = message.substring(index + 1);
		index = message.indexOf('`');
		message = message.substring(0, index);

		index = message.lastIndexOf('.');
		message = message.substring(index + 1);

		// System.out.println("message = " + message);

		return message;
	}
}
