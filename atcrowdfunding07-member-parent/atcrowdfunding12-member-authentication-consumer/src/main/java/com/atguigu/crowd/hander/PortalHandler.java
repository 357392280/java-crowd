package com.atguigu.crowd.hander;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PortalHandler {
    @RequestMapping("/")
    public String ShowPortallPage(){
        return "portal";
    }
}
