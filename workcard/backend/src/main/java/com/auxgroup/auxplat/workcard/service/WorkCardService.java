package com.auxgroup.auxplat.workcard.service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.auxgroup.auxplat.workcard.entity.Result;
import com.auxgroup.auxplat.workcard.util.FileUtil;
import com.auxgroup.auxplat.workcard.util.PictureConstant;
import com.auxgroup.auxplat.workcard.util.WorkCardUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Service
public class WorkCardService {

	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	
	private static final String TEST_VALUE = "测试值";

	public Map<String, String> getBackGroudName() {

		String path = PictureConstant.BACKGROUND_PICTURE_PATH;

		return FileUtil.getBase64AndNameByPath(path);

	}

	/**
	 * 保存加工图片
	 * 
	 * @param backtype
	 * @param fontsize
	 * @param fontname
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Result saveHandlingPicture(String backtype, String fontname, String fontsize, MultipartFile file,
			String radioduty) throws IOException {

		Result result = new Result();
		String picturePath = PictureConstant.WORKCARD_PICTURES_PATH;

		File sourceFile = null;
		String backPic = PictureConstant.BACKGROUND_PICTURE_PATH + backtype + PictureConstant.PICTURE_SUFFIX;
		sourceFile = new File(backPic);

		String fileName = file.getOriginalFilename();

		String badge = TEST_VALUE;
		
		String name = TEST_VALUE;

		String dutyName = TEST_VALUE;

		String deptName = TEST_VALUE;
		
		byte[] b = WorkCardUtil.getCardByte(fontname, fontsize, badge, name, deptName, file, sourceFile, dutyName);

		FileUtil.byteStringToImage(b, badge, picturePath);

		result.setName("图片地址");
		result.setUrl(encoder.encodeBuffer(b));

		return result;
	}

	public ResponseEntity<Resource> getPictureRar(String badges, String level) {

		if (badges == null || badges == "") {
			return null;
		}
		String[] badge = badges.split("&");
		Set<String> set = new HashSet<String>();
		Map<String, String> map = new HashMap<>();// key工号 value 压缩entry路径
		String compressName = "";

		if (PictureConstant.COMPRESS_LEVEL.equals(level)) {
			// 按层级分
			for (String s : badge) {

				String companyName = TEST_VALUE;

				String sysName = TEST_VALUE;

				String departmentName = TEST_VALUE;

				String dname = "";

				if (StringUtils.isEmpty(departmentName))
					dname = sysName;
				else
					dname = departmentName;

				// 去重
				dname = dname.replaceAll(companyName, "");

				map.put(s, companyName + "\\" + dname + "\\");
				set.add(s);
			}
			compressName = FileUtil.uploadcompress(set, map);
		} else {
			// 不按层级分
			for (String s : badge) {
				set.add(s);
			}
			compressName = FileUtil.uploadcompress(set);
		}

		String path = PictureConstant.WORKCARD_PICTURES_PATH + compressName;
		File file = new File(path);
		byte[] bytes = FileUtil.getFileToByte(file);

		return workcardExport(bytes, compressName);
	}

	public ResponseEntity<Resource> workcardExport(byte[] bytes, String name) {
		try {

			Resource resource = new ByteArrayResource(bytes);

			HttpHeaders httpHeader = new HttpHeaders();
			httpHeader.add("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));

			return ResponseEntity.ok().headers(httpHeader).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(resource);
		} catch (Exception e) {
			// log.error(e.getMessage(), e);
			e.printStackTrace();
			return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
