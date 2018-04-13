package xyz.fz.cmd;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.fz.dao.CommonDao;
import xyz.fz.domain.HttpMonitorSetting;
import xyz.fz.service.HttpMonitorService;
import xyz.fz.util.SpringContextHelper;
import xyz.fz.util.TaskUtil;

import javax.annotation.Resource;
import java.util.List;

@Order(2)
@Component
public class TaskInitRunner implements CommandLineRunner {

    @Resource
    private CommonDao db;

    @Resource
    private TaskUtil taskUtil;

    @Resource
    private HttpMonitorService httpMonitorService;

    @Override
    public void run(String... strings) throws Exception {
        initHttpMonitorTask();
        initHttpMonitorLogCleanTask();
    }

    private void initHttpMonitorTask() {
        List<HttpMonitorSetting> settingList = db.queryListBySql("select * from t_http_monitor_setting ", null, HttpMonitorSetting.class);
        if (settingList != null && settingList.size() > 0) {
            for (HttpMonitorSetting setting : settingList) {
                taskUtil.addTask(httpMonitorService.task(setting));
            }
        }
    }

    private void initHttpMonitorLogCleanTask() {
        Runnable cleanRunnable = new Runnable() {
            @Override
            public void run() {
                HttpMonitorService httpMonitorService = SpringContextHelper.getBean("httpMonitorServiceImpl", HttpMonitorService.class);
                httpMonitorService.logClean();
            }
        };
        taskUtil.addTask(new TaskUtil.Task(cleanRunnable, "logCleanTask", 60 * 60 * 24));
    }
}
