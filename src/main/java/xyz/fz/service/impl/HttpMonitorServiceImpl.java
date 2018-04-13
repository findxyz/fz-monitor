package xyz.fz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fz.dao.CommonDao;
import xyz.fz.dao.PagerData;
import xyz.fz.domain.HttpMonitorLog;
import xyz.fz.domain.HttpMonitorSetting;
import xyz.fz.service.HttpMonitorService;
import xyz.fz.service.MailManageService;
import xyz.fz.util.BaseUtil;
import xyz.fz.util.HttpUtil;
import xyz.fz.util.SpringContextHelper;
import xyz.fz.util.TaskUtil;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HttpMonitorServiceImpl implements HttpMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMonitorServiceImpl.class);

    @Resource
    private CommonDao db;

    @Resource
    private TaskUtil taskUtil;

    @Resource
    private MailManageService mailManageService;

    @Override
    public void save(HttpMonitorSetting httpMonitorSetting) {
        if (httpMonitorSetting.getId() != null) {
            HttpMonitorSetting nowHttpMonitorSetting = db.findById(HttpMonitorSetting.class, httpMonitorSetting.getId());
            taskUtil.removeTask(new TaskUtil.Task(nowHttpMonitorSetting.getName()));

            BeanUtils.copyProperties(httpMonitorSetting, nowHttpMonitorSetting);

            db.update(nowHttpMonitorSetting);
            taskUtil.addTask(task(nowHttpMonitorSetting));
        } else {
            db.save(httpMonitorSetting);
            taskUtil.addTask(task(httpMonitorSetting));
        }
    }

    @Override
    public HttpMonitorSetting edit(long id) {
        return db.findById(HttpMonitorSetting.class, id);
    }

    @Override
    public void delete(long id) {
        HttpMonitorSetting httpMonitorSetting = db.findById(HttpMonitorSetting.class, id);
        db.delete(httpMonitorSetting);
        Map<String, Object> params = new HashMap<>();
        params.put("settingId", id);
        db.executeBySql("delete from t_http_monitor_log where setting_id = :settingId ", params);
        taskUtil.removeTask(new TaskUtil.Task(httpMonitorSetting.getName()));
    }

    @Override
    public List<HttpMonitorSetting> list() {
        return db.queryListBySql("select * from t_http_monitor_setting ", null, HttpMonitorSetting.class);
    }

    @Override
    public void logSave(long settingId, String status, long duration) {
        HttpMonitorLog httpMonitorLog = new HttpMonitorLog();
        httpMonitorLog.setSettingId(settingId);
        httpMonitorLog.setCreateTime(new Date());
        httpMonitorLog.setDuration((int) duration);
        httpMonitorLog.setStatus(status);
        db.save(httpMonitorLog);
    }

    @Override
    public PagerData<HttpMonitorLog> logList(long settingId, int page, int pageSize) {
        String countSql = "select count(0) from t_http_monitor_log where setting_id = :settingId ";
        String sql = "select * from t_http_monitor_log where setting_id = :settingId order by id desc ";
        Map<String, Object> params = new HashMap<>();
        params.put("settingId", settingId);
        return db.queryPagerDataBySql(countSql, sql, params, page, pageSize, HttpMonitorLog.class);
    }

    @Override
    public void logClean() {
        String sql = "delete from t_http_monitor_log where create_time < :createTime ";
        Map<String, Object> params = new HashMap<>();
        params.put("createTime", new DateTime().minusDays(1).toString("yyyy-MM-dd HH:mm:ss"));
        db.executeBySql(sql, params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskUtil.Task task(HttpMonitorSetting httpMonitorSetting) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpMonitorService httpMonitorService = SpringContextHelper.getBean("httpMonitorServiceImpl", HttpMonitorService.class);
                long settingId = httpMonitorSetting.getId();
                String responseWanted = httpMonitorSetting.getWanted();
                try {
                    long begin = System.currentTimeMillis();
                    String response = doRequest(httpMonitorSetting);
                    long end = System.currentTimeMillis();
                    long duration = end - begin;
                    boolean ok = response.contains(responseWanted);
                    httpMonitorService.logSave(settingId, ok ? "y" : "n", duration);
                    if (!ok) {
                        mailManageService.sendMail("内容不匹配报警日志", "监控项目：" + httpMonitorSetting.getName() + "\n报警原因：" + "内容不符合预期");
                    }
                } catch (Exception e) {
                    mailManageService.sendMail("请求异常报警日志", "监控项目：" + httpMonitorSetting.getName() + "\n报警原因：" + e.getMessage());
                    httpMonitorService.logSave(settingId, "n", -999);
                    LOGGER.error(BaseUtil.getExceptionStackTrace(e));
                }
            }
        };
        return new TaskUtil.Task(runnable, httpMonitorSetting.getName(), httpMonitorSetting.getTime());
    }

    private String doRequest(HttpMonitorSetting httpMonitorSetting) {
        String response;
        String url = httpMonitorSetting.getUrl();
        String params = httpMonitorSetting.getParams();
        switch (httpMonitorSetting.getMethodType()) {
            case MethodType.GET_FORM:
                response = HttpUtil.httpGet(url, processParams(params));
                break;
            case MethodType.POST_FORM:
                response = HttpUtil.httpPost(url, processParams(params));
                break;
            case MethodType.POST_JSON:
                response = HttpUtil.httpPostJson(url, StringUtils.defaultIfBlank(params.replaceAll("\\s+|\u00A0", ""), "{}"));
                break;
            default:
                response = HttpUtil.httpGet(url, null);
                break;
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private LinkedHashMap processParams(String params) {
        LinkedHashMap paramMap = new LinkedHashMap();
        if (StringUtils.isNoneBlank(params)) {
            String[] paramArr = params.split("&");
            for (String param : paramArr) {
                String[] keyValueArr = param.split("=");
                paramMap.put(keyValueArr[0], keyValueArr[1]);
            }
        }
        return paramMap;
    }

    public static class MethodType {
        static final String GET_FORM = "GET_FORM";

        static final String POST_FORM = "POST_FORM";

        static final String POST_JSON = "POST_JSON";
    }
}
