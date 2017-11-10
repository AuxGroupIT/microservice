package com.auxgroup.auxplat.workcard.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

public class WorkCardUtil {

	public static byte[] getCardByte(String fontname, String fontsize, String badge, String name, String unit,
			MultipartFile file, File backFile, String dutyName) throws IOException {
		BufferedImage s = null;
		// String name1 = new String(name.getBytes(),"iso8859-1");
		try {
			s = ImageIO.read(backFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedImage sourceImage = null;
		BufferedImage afterAdduser = null;
		if (dutyName == null) {
			sourceImage = modifyImage(fontname, fontsize, s, name, badge, unit);
			afterAdduser = watermark(sourceImage, file, 178, 265, 1);
		} else {
			sourceImage = modifyImage(fontname, fontsize, s, name, badge, unit, dutyName);
			afterAdduser = watermark(sourceImage, file, 165, 220, 1);
		}

		byte[] d = process(afterAdduser, 300);

		// byteToFile(d, "C:\\Users\\gufan\\Desktop\\" ,
		// badge+"-workcard-bold-30-ht.JPG");
		return d;
	}

	public static byte[] getCardByte(String badge, String name, String unit, File file, File backFile)
			throws IOException {
		BufferedImage s = null;
		try {
			s = ImageIO.read(backFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage sourceImage = modifyImage("宋体", "", s, name, badge, unit, "IT初级开发工程师");

		BufferedImage afterAdduser = watermark(sourceImage, file, 165, 220, 1);

		byte[] d = process(afterAdduser, 300);

		// byteToFile(d, "C:\\Users\\gufan\\Desktop\\" ,
		// badge+"-workcard-bold-30-ht.JPG");
		return d;
	}

	private static void byteToFile(byte[] data, String path, String fileName) {

		File folder = new File(path);

		if (!(folder.exists() && folder.isDirectory())) {
			folder.mkdirs();
		}

		String filePathName = path + fileName;

		File file = new File(filePathName);
		BufferedOutputStream stream = null;
		FileOutputStream fstream = null;
		try {
			fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(data);
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

	private static byte[] process(BufferedImage image, int dpi) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		imageWriter.setOutput(ios);

		ImageWriteParam jpegParams = imageWriter.getDefaultWriteParam();
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		// 调整图片质量
		jpegParams.setCompressionQuality(1f);

		IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), jpegParams);
		Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");
		Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
		jfif.setAttribute("Xdensity", Integer.toString(300));
		jfif.setAttribute("Ydensity", Integer.toString(300));
		jfif.setAttribute("resUnits", "1");
		data.mergeTree("javax_imageio_jpeg_image_1.0", tree);

		imageWriter.write(data, new IIOImage(image, null, data), jpegParams);
		ios.close();
		imageWriter.dispose();

		return out.toByteArray();

	}

	private static BufferedImage watermark(BufferedImage buffImg, MultipartFile waterFile, int x, int y, float alpha)
			throws IOException {
		// 获取层图
		BufferedImage waterImg = ImageIO.read(waterFile.getInputStream());
		// 创建Graphics2D对象，用在底图对象上绘图
		Graphics2D g2d = buffImg.createGraphics();
		int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
		int waterImgHeight = waterImg.getHeight();// 获取层图的高度
		// 在图形和图像中实现混合和透明效果
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		// 绘制
		g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
		g2d.dispose();// 释放图形上下文使用的系统资源
		return buffImg;
	}

	private static BufferedImage watermark(BufferedImage buffImg, File waterFile, int x, int y, float alpha)
			throws IOException {
		// 获取层图
		BufferedImage waterImg = ImageIO.read(waterFile);
		// 创建Graphics2D对象，用在底图对象上绘图
		Graphics2D g2d = buffImg.createGraphics();
		int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
		int waterImgHeight = waterImg.getHeight();// 获取层图的高度
		// 在图形和图像中实现混合和透明效果
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		// 绘制
		g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
		g2d.dispose();// 释放图形上下文使用的系统资源
		return buffImg;
	}

	private static BufferedImage modifyImage(String fontname, String fontsize, BufferedImage img, String name,
			String badge, String unitName) {

		Graphics2D g = null;
		try {
			g = img.createGraphics();
			/* 设置2D画笔的画出的文字颜色 */
			g.setColor(Color.BLACK);// 设置字体颜色
			/* 设置2D画笔的画出的文字背景色 */
			g.setBackground(Color.white);

			/* --------对要显示的文字进行处理-------------- */
			Integer size = 30;
			try {
				size = Integer.valueOf(fontsize);
			} catch (NumberFormatException e) {
				size = 30;
			}

			Font font = new Font(fontname, Font.CENTER_BASELINE, size);
			g.setFont(font);
			/* 消除java.awt.Font字体的锯齿 */
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// 验证输出位置的纵坐标和横坐标
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			drawString(g, name, 240, 245, 795);
			drawString(g, badge, 240, 245, 857);
			drawString(g, unitName, 240, 245, 917);

			g.dispose();
		} catch (Exception e) {
		}

		return img;
	}

	/**
	 * 增加职务
	 * 
	 * @param fontname
	 * @param fontsize
	 * @param img
	 * @param name
	 * @param badge
	 * @param unitName
	 * @param dutyName
	 * @return
	 */
	private static BufferedImage modifyImage(String fontname, String fontsize, BufferedImage img, String name,
			String badge, String unitName, String dutyName) {

		Graphics2D g = null;
		try {
			g = img.createGraphics();
			/* 设置2D画笔的画出的文字颜色 */
			g.setColor(Color.BLACK);// 设置字体颜色
			/* 设置2D画笔的画出的文字背景色 */
			g.setBackground(Color.white);

			/* --------对要显示的文字进行处理-------------- */
			Integer size = 30;
			try {
				size = Integer.valueOf(fontsize);
			} catch (NumberFormatException e) {
				size = 30;
			}

			Font font = new Font(fontname, Font.CENTER_BASELINE, size);
			g.setFont(font);
			/* 消除java.awt.Font字体的锯齿 */
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// 验证输出位置的纵坐标和横坐标
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			drawString(g, name, 240, 245, 703);
			drawString(g, dutyName, 240, 245, 778);
			drawString(g, unitName, 240, 245, 858);
			drawString(g, badge, 240, 245, 940);

			g.dispose();
		} catch (Exception e) {
		}

		return img;
	}

	private static void drawString(Graphics g, String str, int width, int xPos, int yPos) {
		int strWidth = g.getFontMetrics().stringWidth(str);
		g.drawString(str, xPos + (width - strWidth) / 2, yPos);
	}
}
