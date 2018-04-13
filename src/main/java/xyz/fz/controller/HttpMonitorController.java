package xyz.fz.controller;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.fz.dao.PagerData;
import xyz.fz.domain.HttpMonitorLog;
import xyz.fz.domain.HttpMonitorSetting;
import xyz.fz.model.PageResult;
import xyz.fz.model.Result;
import xyz.fz.service.HttpMonitorService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/http")
public class HttpMonitorController {

    @Resource
    private HttpMonitorService httpMonitorService;

    @RequestMapping("/main")
    public String main() {
        return "http/main";
    }

    @RequestMapping("/add")
    public String add() {
        return "http/add";
    }

    @RequestMapping("/log/{settingId}")
    public String log(@PathVariable("settingId") long settingId, Model model) {
        model.addAttribute("settingId", settingId);
        return "http/log";
    }

    @RequestMapping("/setting/save")
    @ResponseBody
    public Result settingSave(HttpMonitorSetting httpMonitorSetting) {
        httpMonitorService.save(httpMonitorSetting);
        return Result.ofSuccess();
    }

    @RequestMapping("/setting/edit")
    @ResponseBody
    public Result edit(@RequestParam("id") long id) {
        HttpMonitorSetting httpMonitorSetting = httpMonitorService.edit(id);
        return Result.ofData(httpMonitorSetting);
    }

    @RequestMapping("/setting/delete")
    @ResponseBody
    public Result settingDelete(@RequestParam("id") long id) {
        httpMonitorService.delete(id);
        return Result.ofSuccess();
    }

    @RequestMapping("/setting/list")
    @ResponseBody
    public PageResult list() {
        List<HttpMonitorSetting> list = httpMonitorService.list();
        return PageResult.ofData(list.size(), list);
    }

    @RequestMapping("/log/list")
    @ResponseBody
    public PageResult logList(@RequestParam("settingId") long settingId,
                              @RequestParam("page") int page,
                              @RequestParam("limit") int pageSize) {

        PagerData<HttpMonitorLog> pagerData = httpMonitorService.logList(settingId, page, pageSize);
        return PageResult.ofData(pagerData.getTotalCount(), logListFormat(pagerData.getData()));
    }

    private List<Map<String, Object>> logListFormat(List<HttpMonitorLog> list) {
        List<Map<String, Object>> formatList = new ArrayList<>();
        for (HttpMonitorLog log : list) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", log.getId());
            dataMap.put("createTime", new DateTime(log.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"));
            dataMap.put("status", log.getStatus());
            dataMap.put("duration", log.getDuration());
            formatList.add(dataMap);
        }
        return formatList;
    }
}
