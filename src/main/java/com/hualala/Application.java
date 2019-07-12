package com.hualala;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@SpringBootApplication
@MapperScan("com.hualala.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @RequestMapping("/do/{path}")
    public String doPath(@PathVariable("path") String path, HttpServletRequest request, ModelMap modelMap) {
        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            modelMap.addAttribute(param.getKey(), param.getValue());
        }
        return path;
    }

}
