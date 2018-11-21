package com.ice.seed.slidingvalidation;

import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SlidingvalidationApplicationTests {

    @Test
    public void contextLoads() throws Exception{
            //File file = org.springframework.util.ResourceUtils.getFile("classpath:static/images/cut__bg_19.png");
        //BufferedImage read = ImageIO.read(getClass().getClassLoader().getResource("./resources/static/images/mh.png"));
        URL url = ClassLoader.getSystemResource("");
        System.out.println(url);
        File file = new File(url.getPath()+"/static/images/mh.png");
        System.out.println(file);
    }

}
