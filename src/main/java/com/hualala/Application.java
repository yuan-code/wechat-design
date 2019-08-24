package com.hualala;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@SpringBootApplication
@MapperScan("com.hualala.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    /**
     * 用于普通页面的跳转
     *
     * @param path
     * @param modelMap
     * @return
     */
    @RequestMapping("/do/{path}")
    public String doPath(@PathVariable("path") String path, ModelMap modelMap) {
        resolveParams(modelMap);
        return path;
    }

    /**
     * 用于过passport拦截器的跳转
     *
     * @param rootPath
     * @param servicePath
     * @return
     */
    @RequestMapping("/passport/{rootPath}/{servicePath}")
    public String passport(@PathVariable("rootPath") String rootPath,
                           @PathVariable("servicePath") String servicePath,
                           ModelMap modelMap) {
        resolveParams(modelMap);
        return rootPath + "/" + servicePath;
    }


    /**
     * 用于过微信网页授权拦截器的跳转
     *
     * @param rootPath
     * @param servicePath
     * @return
     */
    @RequestMapping("/auth/{rootPath}/{servicePath}")
    public String auth(@PathVariable("rootPath") String rootPath,
                       @PathVariable("servicePath") String servicePath,
                       ModelMap modelMap) {
        resolveParams(modelMap);
        return rootPath + "/" + servicePath;
    }


    /**
     * 解析请求参数 原样返回
     * @param modelMap
     */
    private void resolveParams(ModelMap modelMap) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            modelMap.addAttribute(param.getKey(), param.getValue());
        }
    }


}
