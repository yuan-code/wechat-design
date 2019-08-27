package com.hualala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Controller
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replaceAll("-","").toUpperCase().substring(16));
//        SpringApplication.run(Application.class, args);
    }


    /**
     * 用于页面的跳转
     *
     * @param rootPath
     * @param servicePath
     * @return
     */
    @RequestMapping("/{rootPath}/{servicePath}")
    public String doPath(@PathVariable("rootPath") String rootPath,
                         @PathVariable("servicePath") String servicePath,
                         HttpServletRequest request,
                         ModelMap modelMap) {
        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            modelMap.addAttribute(param.getKey(), param.getValue());
        }
        return rootPath + "/" + servicePath;
    }


}
