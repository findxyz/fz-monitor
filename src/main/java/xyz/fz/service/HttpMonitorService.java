package xyz.fz.service;

import xyz.fz.dao.PagerData;
import xyz.fz.domain.HttpMonitorLog;
import xyz.fz.domain.HttpMonitorSetting;
import xyz.fz.util.TaskUtil;

import java.util.List;

public interface HttpMonitorService {

    void save(HttpMonitorSetting httpMonitorSetting);

    HttpMonitorSetting edit(long id);

    void delete(long id);

    List<HttpMonitorSetting> list();

    void logSave(long settingId, String status, long duration);

    PagerData<HttpMonitorLog> logList(long settingId, int page, int pageSize);

    void logClean();

    TaskUtil.Task task(HttpMonitorSetting httpMonitorSetting);
}
