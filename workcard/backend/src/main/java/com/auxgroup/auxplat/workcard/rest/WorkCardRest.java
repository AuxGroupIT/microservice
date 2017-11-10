package com.auxgroup.auxplat.workcard.rest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.auxgroup.auxplat.workcard.entity.Result;
import com.auxgroup.auxplat.workcard.service.WorkCardService;
import com.auxgroup.auxplat.workcard.util.FileUtil;
import com.auxgroup.auxplat.workcard.util.PictureConstant;

@RestController
public class WorkCardRest {

	@Autowired
	private WorkCardService workCardService;

	private String res = "[";

	/**
	 * 根据所获得的工号取出图片，生成压缩包,如果图片工号数量为一，则返回图片地址
	 * 
	 * @param badges
	 *            格式为（badge&badge&badge）
	 * @param level
	 *            1按层级分，2不按层级分
	 * @return
	 * 
	 */
	@RequestMapping(value = "/getPicturesRar/{badges},{level}", method = RequestMethod.GET)
	public ResponseEntity<Resource> getPictuesRar(@PathVariable("badges") String badges,
			@PathVariable("level") String level) {

		return workCardService.getPictureRar(badges, level);
	}

	/**
	 * 接收上传图片，处理后保存
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadImage/backtype/{backtype}/fontname/{fontname}/fontsize/{fontsize}/radioduty/{radioduty}", method = RequestMethod.POST)
	public Result getImage(@PathVariable String backtype, @PathVariable String fontname, @PathVariable String fontsize,
			@PathVariable String radioduty, @RequestParam("file") MultipartFile file) throws Exception {

		return workCardService.saveHandlingPicture(backtype, fontname, fontsize, file, radioduty);
	}

	/**
	 * 上传背景图片
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/uploadBackImage", method = RequestMethod.POST)
	public Result uploadBackImage(@RequestParam("file") MultipartFile file) {

		Result result = new Result();
		String picturePath = PictureConstant.BACKGROUND_PICTURE_PATH;
		String picName = file.getOriginalFilename();
		String name = picName.substring(0, picName.indexOf("."));
		String changeName = name + PictureConstant.PICTURE_SUFFIX;
		try {
			FileUtil.uploadFileImage(file, changeName, picturePath);
		} catch (IOException e) {

			e.printStackTrace();
		}
		result.setName("背景图上上传成功");
		// result.setUrl(PictureConstant.BACKGROUND_BACK_PATH + name +
		// PictureConstant.PICTURE_SUFFIX);
		return result;
	}

	/**
	 * 获取所有已上传的背景图
	 * 
	 * @return
	 */
	@RequestMapping(value = "/allbackname", method = RequestMethod.GET)
	public Map<String, String> getAllBackName() {

		return workCardService.getBackGroudName();
	}

	/**
	 * 删除背景图
	 */
	@RequestMapping(value = "/deletebackname/{name}", method = RequestMethod.GET)
	public String deleteBackName(@PathVariable String name) {

		String path = PictureConstant.BACKGROUND_PICTURE_PATH + name + PictureConstant.PICTURE_SUFFIX;
		FileUtil.deleteFile(path);
		return "success";
	}

	@RequestMapping(value = "/testDownload", method = RequestMethod.GET)
	public void testDownload(HttpServletResponse res) {
		String fileName = "upload.jpg";
		res.setHeader("content-type", "application/octet-stream");
		res.setContentType("application/octet-stream");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = res.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(new File("d://" + fileName)));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("success");
	}

}
