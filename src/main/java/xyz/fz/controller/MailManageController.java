package xyz.fz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.fz.dao.PagerData;
import xyz.fz.domain.MailConfig;
import xyz.fz.domain.MailNotifyMember;
import xyz.fz.model.PageResult;
import xyz.fz.model.Result;
import xyz.fz.service.MailManageService;

import javax.annotation.Resource;

@Controller
@RequestMapping("/mail")
public class MailManageController {

    @Resource
    private MailManageService mailManageService;

    @RequestMapping("/main")
    public String main(Model model) {
        MailConfig mailConfig = mailManageService.config();
        String hostName = "";
        String userName = "";
        String password = "";
        if (mailConfig != null) {
            hostName = mailConfig.getHostName();
            userName = mailConfig.getUserName();
            password = mailConfig.getPassword();
        }
        model.addAttribute("hostName", hostName);
        model.addAttribute("userName", userName);
        model.addAttribute("password", password);
        return "mail/main";
    }

    @RequestMapping("/config/save")
    @ResponseBody
    public Result save(MailConfig mailConfig) {
        mailManageService.save(mailConfig);
        return Result.ofSuccess();
    }

    @RequestMapping("/to/save")
    @ResponseBody
    public Result mailNotifyMemberSave(MailNotifyMember mailNotifyMember) {
        mailManageService.mailNotifyMemberSave(mailNotifyMember);
        return Result.ofSuccess();
    }

    @RequestMapping("/to/delete")
    @ResponseBody
    public Result mailNotifyMemberDelete(long id) {
        mailManageService.mailNotifyMemberDelete(id);
        return Result.ofSuccess();
    }

    @RequestMapping("/to/list")
    @ResponseBody
    public PageResult mailNotifyMemberList(@RequestParam("page") int page, @RequestParam("limit") int pageSize) {
        PagerData<MailNotifyMember> pagerData = mailManageService.mailNotifyMemberList(page, pageSize);
        return PageResult.ofData(pagerData.getTotalCount(), pagerData.getData());
    }
}
