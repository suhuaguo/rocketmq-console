package com.alibaba.rocketmq.action.dfireadmin;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by xuliping on 15/9/1.
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // 登陆界面
    @RequestMapping(value = "/login.do")
    @ResponseBody
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView();

        if (error != null) {
            model.addObject("error", "用户名或密码出错！！！");
        }

        System.out.println("------进来登陆啦！！！");
        if (logout != null) {
            model.addObject("msg", "退出成功！！！");
        }
        model.setViewName("login");
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String name = authentication.getName();// 获取到用户名
        authentication.getPrincipal();
        model.addObject("name", name);


        System.out.println(JSON.toJSON(model));
        return model;

    }

    @RequestMapping(value = {"/", "/welcome1.do", ""})
    public ModelAndView defaultPage() {
        ModelAndView model = new ModelAndView();

        model.setViewName("index");
        return model;

    }


}
