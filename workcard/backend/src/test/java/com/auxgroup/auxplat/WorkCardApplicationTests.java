package com.auxgroup.auxplat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.auxgroup.auxplat.workcard.util.FileUtil;
import com.auxgroup.auxplat.workcard.util.WorkCardUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkCardApplicationTests {

	@Test
	public void contextLoads() throws IOException {
		
		File backfile = new File("C:/Users/gufan/Desktop/明州医院新.jpg");
		File file = new File("C:/Users/gufan/Desktop/待制作一线/170331030.JPG");
		
		byte[] b = WorkCardUtil.getCardByte("160801018", "辜凡", "IT管理部", file, backfile);

		
		FileUtil.byteStringToImage(b, "160801018", "C:/Users/gufan/Desktop/font/");
		
	}

}
