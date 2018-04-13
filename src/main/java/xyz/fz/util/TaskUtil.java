package xyz.fz.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class TaskUtil {

    private ConcurrentHashMap<String, Future> taskMap = new ConcurrentHashMap<>();

    private final ScheduledThreadPoolExecutor taskExecutor;

    @Autowired
    public TaskUtil(ScheduledThreadPoolExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void addTask(Task task) {
        if (taskMap.containsKey(task.getTaskName())) {
            throw new RuntimeException("任务已存在，请修改任务名称");
        }
        Future taskFuture = taskExecutor.scheduleAtFixedRate(task.getRunnable(), 1, task.getPeriodSeconds(), TimeUnit.SECONDS);
        taskMap.put(task.getTaskName(), taskFuture);
    }

    public void removeTask(Task task) {
        if (taskMap.containsKey(task.getTaskName())) {
            Future taskFuture = taskMap.get(task.getTaskName());
            taskFuture.cancel(true);
            taskMap.remove(task.getTaskName());
        }
    }

    public static class Task {

        private Runnable runnable;

        private String taskName;

        private long periodSeconds;

        public Task() {}

        public Task(String taskName) {
            this.taskName = taskName;
        }

        public Task(Runnable runnable, String taskName, long periodSeconds) {
            this.runnable = runnable;
            this.taskName = taskName;
            this.periodSeconds = periodSeconds;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public long getPeriodSeconds() {
            return periodSeconds;
        }

        public void setPeriodSeconds(long periodSeconds) {
            this.periodSeconds = periodSeconds;
        }
    }
}
