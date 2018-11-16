package com.ice.seed.slidingvalidation.Controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.weaver.ast.Test;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.net.www.content.image.png;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.util.Map;

/**
 * @author : IceSeed
 * @version : v0.0.1
 * @since : 2018/11/1
 */
@Controller
public class validationController {

    // 滑块边长
    private final Double L = 62D;
    // 图片宽度
    private final Double W = 310D;
    // 图片高度
    private final Double H = 155D;

    private final Double OFFSET = 5D;
    private final String slidingCode = "slidingCode:";

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(HttpServletRequest request, HttpServletResponse response) {
        return "slidingValidation";
    }




    /**
     * 获取滑动验证码信息
     *
     *
     * @return 视图
     */
    @ResponseBody
    @RequestMapping(value = "/getSliding", method = RequestMethod.POST)
    public String getSliding(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        Long x = getRandomNumberByRange(L+10,  W - L);
        Long y = getRandomNumberByRange(0D, H - L);
        String redisPrefix = slidingCode + request.getSession().getId();
        Long num = getRandomNumberByRange(1D, 28D);
        System.out.println(num);
        String path = "classpath:static/images/"+ num +".png ";

       try {
           Map<String, String> component = SlidingValidation.getComponent("D:/迅雷下载/19.png", x.intValue(), y.intValue(), L.intValue());
           component.put("y", y.toString());
           EhcacheManager.put(redisPrefix, x);
           System.out.println("放入---"+x);
           json.put("component",component);
       }catch (Exception e){
           e.printStackTrace();
       }
        return json.toString();
    }


    /**
     * 获取滑动验证码信息
     *
     * @return 视图
     */
    @ResponseBody
    @RequestMapping(value = "/validateSliding", method = RequestMethod.POST)
    public Boolean validateSliding(HttpServletRequest request, HttpServletResponse response,Double move) {
        String redisPrefix = slidingCode + request.getSession().getId();
        Integer coordinates = Integer.parseInt(String.valueOf(EhcacheManager.get(redisPrefix)));
        EhcacheManager.remove(redisPrefix);
        if(coordinates> (move - OFFSET) && coordinates < (move + OFFSET)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 随机获取区间值
     * @param start
     * @param end
     * @return
     */
    public Long getRandomNumberByRange(Double start, Double end) {
        return Math.round(Math.random() * (end - start) + start);

    }
}
