package com.ice.seed.slidingvalidation.Controller;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 滑动验证工具类
 * @author : IceSeed
 * @version : v0.0.1
 * @since : 2018/11/9
 */
public class SlidingValidation {
    // 滑块边长
    private Integer l;
    // 图片x位置
    private Integer x;
    // 图片y位置
    private Integer y;
    // 图片路径
    private File srcImg;
    // 生成图片路径
    private String destImg;
    // 白底图片路径
    private final String whiteImg = "static/images/mh.png";


    // 生成图片前缀
    private static String DEFAULT_CUT_PREVFIX = "cut_";


    public SlidingValidation(File srcImg, String destImg, int x, int y, Integer l) {
        this.srcImg = srcImg;
        this.destImg = destImg;
        this.x = x;
        this.y = y;
        this.l = l;

    }

    public SlidingValidation(File srcImg, int x, int y, Integer l) {
        this.srcImg = srcImg;
        this.x = x;
        this.y = y;
        this.l = l;
    }



    //裁剪滑块图片
    public void cutSliderImage(File srcImg, String destImgPath, Rectangle rect){
        File destImg = new File(destImgPath);
        if (destImg.exists()) {
            String p = destImg.getPath();
            try {
                if (!destImg.isDirectory())
                    p = destImg.getParent();
                if (!p.endsWith(File.separator))
                    p = p + File.separator;

                BufferedImage image = PictureFactory.getImage(srcImg, rect);
                //生成图片
                OutputStream output = new FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_slider_" + srcImg.getName());
                ImageIO.write(image, "PNG", output);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("图片不存在！");
        }
    }


    /**
     * 生成图片base64码
     *
     * @param srcImg
     * @param rect
     * @return
     */
    public String cutImageByBase64(File srcImg, Rectangle rect) {
        BufferedImage image =  PictureFactory.getImage(srcImg, rect);
        // base64图片
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());
    }

    /**
     * 生成背景图片
     * @param srcImg
     * @param destImgPath
     * @throws Exception
     */
    public void jointImage(File srcImg, String destImgPath){
        File destImg = new File(destImgPath);
        if (destImg.exists()) {
            String p = destImg.getPath();
            try {
                if (!destImg.isDirectory())
                    p = destImg.getParent();
                if (!p.endsWith(File.separator))
                    p = p + File.separator;

                BufferedImage image1 = ImageIO.read(srcImg);

                BufferedImage image2 =  PictureFactory.getImage(PictureFactory.getImgFile(whiteImg), new Rectangle(0, 0, l, l));

                // 转成图片
                OutputStream output = new FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_bg_" + srcImg.getName());
                ImageIO.write( PictureFactory.jointImage(image1, image2, x, y, l), "PNG", output);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("图片不存在！");
        }

    }


    /**
     * 生成背景图片base64码
     * @param srcImg
     * @return
     */
    public String jointImageByBase64(File srcImg){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage image1 = ImageIO.read(srcImg);
            BufferedImage image2 =  PictureFactory.getImage(PictureFactory.getImgFile(whiteImg), new Rectangle(0, 0, l, l));
            // base64图片
            ImageIO.write( PictureFactory.jointImage(image1, image2, x, y, l), "PNG", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(outputStream.toByteArray());
    }


    /**
     * 获取字节码滑块组件
     *
     * @param srcImg：原图路径
     * @param x:          截取位置X坐标
     * @param y:          截取位置Y坐标
     * @param l：滑块周长
     * @return
     * @throws Exception
     */
    public static Map<String, String> getComponent(File srcImg, int x, int y, Integer l){
        Map<String, String> imageMap = new HashMap();
        SlidingValidation slidingValidation = new SlidingValidation(srcImg, x, y, l);
        imageMap.put("slider", slidingValidation.cutImageByBase64(srcImg, new Rectangle(x, y, l, l)));
        imageMap.put("background", slidingValidation.jointImageByBase64(srcImg));
        return imageMap;
    }


    /**
     * 生成图片
     *
     * @param srcImg：原图路径
     * @param destImg：生成图片路径
     * @param x:             截取位置X坐标
     * @param y:             截取位置Y坐标
     * @param l：滑块周长
     * @throws Exception
     */
    public static void getComponent(File srcImg, String destImg, int x, int y, Integer l){
        Map<String, String> imageMap = new HashMap();
        SlidingValidation slidingValidation = new SlidingValidation(srcImg, destImg, x, y, l);
        slidingValidation.cutSliderImage(srcImg, destImg, new Rectangle(x, y, l, l));
        slidingValidation.jointImage(srcImg, destImg);
    }



}
