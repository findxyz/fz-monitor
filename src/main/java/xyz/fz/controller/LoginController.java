package xyz.fz.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        Subject subject = SecurityUtils.getSubject();
        model.addAttribute("userName", subject.getPrincipal());
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/doLogin")
    @ResponseBody
    public String doLogin(@RequestParam("userName") String userName, @RequestParam("passWord") String passWord) {
        UsernamePasswordToken token = new UsernamePasswordToken(userName, passWord);
        try {
            SecurityUtils.getSubject().login(token);
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"用户名密码不正确\"}";
        }
    }

    @RequestMapping("/doLogout")
    @ResponseBody
    public String doLogout() {
        try {
            SecurityUtils.getSubject().logout();
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"当前账户尚未登录\"}";
        }
    }
}
