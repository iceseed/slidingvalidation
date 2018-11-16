package com.ice.seed.slidingvalidation;

import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SlidingvalidationApplicationTests {

    @Test
    public void contextLoads() throws Exception{
            File file = org.springframework.util.ResourceUtils.getFile("classpath:static/images/cut__bg_19.png");

            //System.out.println(.getServletContext().getRealPath("/"));
    }

}
