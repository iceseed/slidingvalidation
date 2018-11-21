package com.ice.seed.slidingvalidation.Controller;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : IceSeed
 * @version : v0.0.1
 * @since : 2018/11/9
 */
public class SlidingValidation {
    // 滑块边长
    private Integer l;
    // 滑块倍数
    private Double multiple;
    // 图片x位置
    private Integer x;
    // 图片y位置
    private Integer y;
    // 图片路径
    private String srcImg;
    // 生成图片路径
    private String destImg;
    // 白底图片路径
    private final String whiteImg = "/static/images/mh.png";


    // 生成图片前缀
    private static String DEFAULT_CUT_PREVFIX = "cut_";


    public SlidingValidation(String srcImg, String destImg, int x, int y, Integer l) {
        this.srcImg = srcImg;
        this.destImg = destImg;
        this.x = x;
        this.y = y;
        this.l = l;
        this.multiple = (double) l / 18;
    }

    public SlidingValidation(String srcImg, int x, int y, Integer l) {
        this.srcImg = srcImg;
        this.x = x;
        this.y = y;
        this.l = l;
        this.multiple = (double) l / 18;
    }

    public void cutSliderImage() {
        try {
            URL url = ClassLoader.getSystemResource("");
            File file = new File(url.getPath()+srcImg);
            cutSliderImage(file, destImg, new Rectangle(x, y, l, l));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String cutSliderImageByBase64() {
        URL url = ClassLoader.getSystemResource("");
        File file = new File(url.getPath()+srcImg);
        return cutImageByBase64(file, new Rectangle(x, y, l, l));
    }

    //裁剪滑块图片
    public void cutSliderImage(File srcImg, String destImgPath, Rectangle rect) throws Exception {
        File destImg = new File(destImgPath);
        if (destImg.exists()) {
            String p = destImg.getPath();
            try {
                if (!destImg.isDirectory())
                    p = destImg.getParent();
                if (!p.endsWith(File.separator))
                    p = p + File.separator;

                BufferedImage image = getImage(srcImg, rect);
                //生成图片
                OutputStream output = new FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_slider_" + srcImg.getName());
                ImageIO.write(image, "PNG", output);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("图片不存在！");
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
        BufferedImage image = getImage(srcImg, rect);
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
     * 获取特定形状的图片
     *
     * @param srcImg
     * @param rect
     * @return
     */
    public BufferedImage getImage(File srcImg, Rectangle rect) {
        //主体正方形
        BufferedImage square = cutImage(srcImg, rect);
        return processImage(square, l);

    }

    /**
     * 裁剪区域内图片
     *
     * @param srcImg
     * @param rect
     * @return
     */
    public BufferedImage cutImage(File srcImg, Rectangle rect) {
        BufferedImage image = null;
        if (srcImg.exists()) {
            FileInputStream fis = null;
            ImageInputStream iis = null;
            try {
                fis = new FileInputStream(srcImg);
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG,
                // JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");

                String suffix = null;
                // 获取图片后缀
                if (srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
               /* if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
                    return image;
                }*/
                // 将FileInputStream 转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);
                // 根据图片类型获取该种类型的ImageReader

                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix.trim()).next();
                reader.setInput(iis, true);
                ImageReadParam param = reader.getDefaultReadParam();

                param.setSourceRegion(rect);
                image = reader.read(0, param);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                    if (iis != null)
                        iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    /**
     * 加工指定形状的图片
     *
     * @param image      目标图片
     * @param targetSize 生成图片边长
     * @return
     */
    public BufferedImage processImage(BufferedImage image, int targetSize) {
        BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);

        g2.fill(getCutPath());
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.setColor(new Color(255,193,37));
        g2.setStroke(new BasicStroke(3f));
        g2.draw(getCutPath());
        g2.dispose();
        return outputImage;
    }

    /**
     * 获取裁剪路径
     *
     * @return
     */
    public GeneralPath getCutPath() {
        GeneralPath path = new GeneralPath();
        path.moveTo(0 * multiple, 0 * multiple);
        path.lineTo(0 * multiple, 5 * multiple);
        path.curveTo(5 * multiple, 3 * multiple, 5 * multiple, 11 * multiple, 0 * multiple, 9 * multiple);
        path.lineTo(0 * multiple, 14 * multiple);
        path.lineTo(5 * multiple, 14 * multiple);
        path.curveTo(3 * multiple, 19 * multiple, 11 * multiple, 19 * multiple, 9 * multiple, 14 * multiple);

        path.lineTo(14 * multiple, 14 * multiple);
        path.lineTo(14 * multiple, 9 * multiple);
        path.curveTo(19 * multiple, 11 * multiple, 19 * multiple, 3 * multiple, 14 * multiple, 5 * multiple);
        path.lineTo(14 * multiple, 0 * multiple);
        path.lineTo(9 * multiple, 0 * multiple);
        path.curveTo(11 * multiple, 5 * multiple, 3 * multiple, 5 * multiple, 5 * multiple, 0 * multiple);
        path.lineTo(0 * multiple, 0 * multiple);
        return path;
    }


    /**
     * 合并图片
     *
     * @param image1
     * @param image2
     * @param x
     * @param y
     * @return
     */
    public BufferedImage jointImage(BufferedImage image1, BufferedImage image2, int x, int y, Integer l) {
        BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(image1, 0, 0, null);
        g.drawImage(image2, x, y, null);
        return combined;
    }


    public void cutBgImage(){
        try {
            URL url = ClassLoader.getSystemResource("");
            File file = new File(url.getPath()+srcImg);
            jointImage(file, destImg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String cutBgImageByBase64(){
        URL url = ClassLoader.getSystemResource("");
        File file = new File(url.getPath()+srcImg);
        return jointImageByBase64(file, destImg);
    }


    /**
     * 生成背景图片
     * @param srcImg
     * @param destImgPath
     * @throws Exception
     */
    public void jointImage(File srcImg, String destImgPath) throws Exception {
        File destImg = new File(destImgPath);
        if (destImg.exists()) {
            String p = destImg.getPath();
            try {
                if (!destImg.isDirectory())
                    p = destImg.getParent();
                if (!p.endsWith(File.separator))
                    p = p + File.separator;

                BufferedImage image1 = ImageIO.read(srcImg);
                URL url = ClassLoader.getSystemResource("");
                File file = new File(url.getPath()+whiteImg);
                BufferedImage image2 = getImage(file, new Rectangle(0, 0, l, l));

                // 转成图片
                OutputStream output = new FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_bg_" + srcImg.getName());
                ImageIO.write(jointImage(image1, image2, x, y, l), "PNG", output);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            throw new Exception("图片不存在！");
        }

    }


    /**
     * 生成背景图片base64码
     * @param srcImg
     * @param destImgPath
     * @return
     */
    public String jointImageByBase64(File srcImg, String destImgPath){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage image1 = ImageIO.read(srcImg);
            URL url = ClassLoader.getSystemResource("");
            File file = new File(url.getPath()+whiteImg);
            BufferedImage image2 = getImage(file, new Rectangle(0, 0, l, l));
            // base64图片
            ImageIO.write(jointImage(image1, image2, x, y, l), "PNG", outputStream);
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
    public static Map<String, String> getComponent(String srcImg, int x, int y, Integer l){
        Map<String, String> imageMap = new HashMap();
        SlidingValidation slidingValidation = new SlidingValidation(srcImg, x, y, l);
        imageMap.put("slider", slidingValidation.cutSliderImageByBase64());
        imageMap.put("background", slidingValidation.cutBgImageByBase64());
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
    public static void getComponent(String srcImg, String destImg, int x, int y, Integer l){
        Map<String, String> imageMap = new HashMap();
        SlidingValidation slidingValidation = new SlidingValidation(srcImg, destImg, x, y, l);
        slidingValidation.cutSliderImage();
        slidingValidation.cutBgImage();
    }


}
