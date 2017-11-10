package com.auxgroup.auxplat.workcard.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class FileUtil {

	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	/**
	 * base64转化成图片压缩到指定目录下
	 * 
	 * @param base64String
	 * @param fileName
	 *            文件名
	 */
	public static void base64StringToImageRar(String base64String, String badge, String path) {

		byte[] bytes1 = null;
		try {
			bytes1 = decoder.decodeBuffer(base64String);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		File folder = new File(path);

		if (!(folder.exists() && folder.isDirectory())) {
			folder.mkdirs();
		}

		String time = getNewTime();
		String zipName = time + PictureConstant.COMPRESSED_PACKET_SUFFIX;

		String zipFilePathName = path + zipName;

		File file2 = new File(zipFilePathName);
		FileOutputStream fstream2 = null;
		ZipOutputStream zos = null;
		try {
			fstream2 = new FileOutputStream(file2);
			zos = new ZipOutputStream(fstream2);
			zos.putNextEntry(new ZipEntry(badge + PictureConstant.PICTURE_SUFFIX));
			zos.write(bytes1);
			zos.closeEntry();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fstream2 != null) {
					fstream2.close();
				}
				if (null != zos) {
					zos.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * byte转化成图片放到指定目录下
	 */
	public static void byteStringToImage(byte[] bytes1, String badge, String path) {

		String picPath = path + badge + PictureConstant.PICTURE_SUFFIX;
		File file = new File(picPath);

		BufferedOutputStream stream = null;
		FileOutputStream fstream = null;
		try {
			fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(bytes1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
				if (null != fstream) {
					fstream.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * base64转化成图片放到指定目录下
	 * 
	 * @param base64String
	 * @param fileName
	 *            文件名
	 */
	public static void base64StringToImage(String base64String, String badge, String path) {

		try {
			byte[] bytes1 = decoder.decodeBuffer(base64String);

			String picPath = path + badge + PictureConstant.PICTURE_SUFFIX;
			File file = new File(picPath);

			BufferedOutputStream stream = null;
			FileOutputStream fstream = null;
			try {
				fstream = new FileOutputStream(file);
				stream = new BufferedOutputStream(fstream);
				stream.write(bytes1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (stream != null) {
						stream.close();
					}
					if (null != fstream) {
						fstream.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将file放入指定目录
	 * 
	 * @throws IOException
	 */
	public static void uploadFileImage(MultipartFile file, String name, String path) throws IOException {

		String imagePath = path + name;
		byte[] b = file.getBytes();
		File file1 = new File(imagePath);

		FileOutputStream fos = new FileOutputStream(file1);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(b);
		if (null != bos)
			bos.close();
	}

	/**
	 * 将文件转化为base64
	 * 
	 * @throws IOException
	 */
	public static String getImageBinary(File file) throws IOException {

		InputStream in = null;
		byte[] data = null;
		in = new FileInputStream(file);
		data = new byte[in.available()];
		in.read(data);
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}

	public static String getBufferedImageBinary(byte[] b) {
		return encoder.encodeBuffer(b);
	}

	/**
	 * 把工号数组中对应的文件压缩到pictures/下 不按层级分
	 * 
	 * @param set
	 * @return 压缩名
	 */
	public static String uploadcompress(Set<String> set) {
		String outputFile = PictureConstant.WORKCARD_PICTURES_PATH;

		if (set.size() == 1)
			return set.iterator().next() + PictureConstant.PICTURE_SUFFIX;

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte[] buf = new byte[1024];
		int len;

		String time = getNewTime();
		String zipName = time + PictureConstant.COMPRESSED_PACKET_SUFFIX;

		try {
			File file = null;
			fos = new FileOutputStream(outputFile + zipName);
			bos = new BufferedOutputStream(fos);
			zos = new ZipOutputStream(bos);
			for (String badge : set) {
				String path = PictureConstant.WORKCARD_PICTURES_PATH + badge + PictureConstant.PICTURE_SUFFIX;
				file = new File(path);
				if (file.isFile()) {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);

					ZipEntry ze = new ZipEntry(badge + PictureConstant.PICTURE_SUFFIX);
					zos.putNextEntry(ze);

					while ((len = bis.read(buf)) != -1) {
						zos.write(buf, 0, len);
						zos.flush();
					}
				} else {
					System.out.println(path + ">>无效路径");
				}

			}

		} catch (IOException e) {
			System.out.println("压缩文件过程出错！");
			e.printStackTrace();
		} finally {
			try {
				if (null != bis)
					bis.close();

				if (null != zos)
					zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return zipName;
	}

	/**
	 * 把工号数组中对应的文件压缩到pictures/下 按层级分
	 * 
	 * @param set
	 * @param map
	 * @return 压缩名
	 */
	public static String uploadcompress(Set<String> set, Map<String, String> map) {
		String outputFile = PictureConstant.WORKCARD_PICTURES_PATH;

		if (set.size() == 1)
			return set.iterator().next() + PictureConstant.PICTURE_SUFFIX;

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte[] buf = new byte[1024];
		int len;

		String time = getNewTime();
		String zipName = time + PictureConstant.COMPRESSED_PACKET_SUFFIX;

		try {
			File file = null;
			fos = new FileOutputStream(outputFile + zipName);
			bos = new BufferedOutputStream(fos);
			zos = new ZipOutputStream(bos);
			for (String badge : set) {
				String entryPathPre = map.get(badge);
				String path = PictureConstant.WORKCARD_PICTURES_PATH + badge + PictureConstant.PICTURE_SUFFIX;
				file = new File(path);
				if (file.isFile()) {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);

					ZipEntry ze = new ZipEntry(entryPathPre + badge + PictureConstant.PICTURE_SUFFIX);
					zos.putNextEntry(ze);

					while ((len = bis.read(buf)) != -1) {
						zos.write(buf, 0, len);
						zos.flush();
					}
				} else {
					System.out.println(path + ">>无效路径");
				}

			}

		} catch (IOException e) {
			System.out.println("压缩文件过程出错！");
			e.printStackTrace();
		} finally {
			try {
				if (null != bis)
					bis.close();

				if (null != zos)
					zos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return zipName;
	}

	/**
	 * 把inputFile中的文件压缩到outputFile
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @throws IOException
	 */
	public static void compress(String inputFile, String outputFile) throws IOException {
		File file = new File(inputFile);
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte[] buf = new byte[1024];
		int len;

		String time = getNewTime();
		String zipName = time + PictureConstant.COMPRESSED_PACKET_SUFFIX;

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			fos = new FileOutputStream(outputFile + zipName);
			bos = new BufferedOutputStream(fos);
			zos = new ZipOutputStream(bos);

			for (File file2 : files) {
				fis = new FileInputStream(file2);
				bis = new BufferedInputStream(fis);
				// buf = new byte[bis.available()];
				buf = new byte[1024];
				ZipEntry ze = new ZipEntry(file2.getName());
				System.out.println(file2.getName());
				zos.putNextEntry(ze);
				// int count = 0;
				while ((len = bis.read(buf)) != -1) {
					zos.write(buf, 0, len);
					zos.flush();
					// count++;
				}
				// System.out.println(count);
			}
		} else {

			String name = file.getName();
			String name2 = name.substring(0, name.indexOf("."));
			fos = new FileOutputStream(outputFile + name2 + ".rar");
			bos = new BufferedOutputStream(fos);
			zos = new ZipOutputStream(bos);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);

			ZipEntry ze = new ZipEntry(file.getName());
			System.out.println(file.getName());
			zos.putNextEntry(ze);

			while ((len = bis.read(buf)) != -1) {
				zos.write(buf, 0, len);
				zos.flush();
			}
		}

		if (null != bis)
			bis.close();

		if (null != zos)
			zos.close();
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getNewTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String time = df.format(new Date());
		return time;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 *            需要删除的文件路径
	 * @return
	 */
	public static boolean deleteFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取文件夹下的所有rar文件
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> listAllRarPath(String path) {
		List<String> list = new ArrayList<>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				if (file2.getName().contains(".rar")) {
					list.add(path + file2.getName());
				}
			}
		}
		return list;
	}

	/**
	 * 获取文件夹下的所有文件名
	 */
	public static List<String> listAllFileName(String path) {
		List<String> list = new ArrayList<>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				list.add(file2.getName());
			}
		}
		return list;
	}
	
	/**
	 * 文件转二进制
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getFileToByte(File file) {

		byte[] by = new byte[(int) file.length()];

		try {

			InputStream is = new FileInputStream(file);

			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();

			byte[] bb = new byte[2048];

			int ch;

			ch = is.read(bb);

			while (ch != -1) {

				bytestream.write(bb, 0, ch);

				ch = is.read(bb);

			}

			by = bytestream.toByteArray();

			if (is != null)
				is.close();

		} catch (Exception ex) {

			ex.printStackTrace();

		}

		return by;

	}
	
	/**
	 * 获取文件的base64和名称
	 * Map<名称,base64>
	 */
	public static Map<String,String> getBase64AndNameByPath(String path) {
		
		File file = new File(path);
		Map<String,String> map = new HashMap<>();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				String name = file2.getName();
				String picName = name.substring(0,name.indexOf("."));
				String suffix = name.substring(name.indexOf("."));
				if(PictureConstant.PICTURE_SUFFIX.equals(suffix)){
					String base64 = encoder.encodeBuffer(getFileToByte(file2));
					map.put(picName,base64);
				}
			}
		}
		return map;
	}

}
