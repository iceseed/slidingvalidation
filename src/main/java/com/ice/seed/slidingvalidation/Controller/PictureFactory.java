package com.ice.seed.slidingvalidation.Controller;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

/**
 * 图片工厂
 */
public class PictureFactory {

    // 生成图片前缀
    private static String DEFAULT_CUT_PREVFIX = "cut_";
    // 滑块倍数
    private static Double multiple;


    /**
     * 根据图片路径获取图片File
     * @param imgPath
     * @return
     */
    public static File getImgFile(String imgPath){
        File imgFile = null;
        try {
            String[]  strs=imgPath.split(",");
            InputStream stream = SlidingValidation.class.getClassLoader().getResourceAsStream(imgPath);
            imgFile = new File(strs[strs.length-1]);
            FileUtils.copyInputStreamToFile(stream, imgFile);
        }catch (IOException e){
            e.printStackTrace();
        }
        return  imgFile;
    }


    /**
     * 获取特定形状的图片
     *
     * @param srcImg
     * @param rect
     * @return
     */
    public static BufferedImage getImage(File srcImg, Rectangle rect) {
        //主体正方形
        BufferedImage square = cutImage(srcImg, rect);
        return processImage(square, square.getWidth());

    }

    /**
     * 裁剪区域内图片
     *
     * @param srcImg
     * @param rect
     * @return
     */
    public static BufferedImage cutImage(File srcImg, Rectangle rect) {
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
    public static BufferedImage processImage(BufferedImage image, int targetSize) {
        BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);

        g2.fill(getCutPath((double) targetSize / 18));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.setColor(new Color(255,193,37));
        g2.setStroke(new BasicStroke(3f));
        g2.draw(getCutPath((double) targetSize / 18));
        g2.dispose();
        return outputImage;
    }

    /**
     * 获取裁剪路径
     *
     * @return
     */
    public static GeneralPath getCutPath(Double multiple) {
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
    public static BufferedImage jointImage(BufferedImage image1, BufferedImage image2, int x, int y, Integer l) {
        BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(image1, 0, 0, null);
        g.drawImage(image2, x, y, null);
        return combined;
    }

}
