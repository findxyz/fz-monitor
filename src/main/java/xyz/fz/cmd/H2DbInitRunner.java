package xyz.fz.cmd;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Order(1)
@Component
public class H2DbInitRunner implements CommandLineRunner, DbInit {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {
        dbInit();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dbInit() {
        try {
            String testSql = "SELECT * FROM TEST ORDER BY ID ";
            jdbcTemplate.execute(testSql);
            // already init
        } catch (BadSqlGrammarException badSqlGrammarException) {
            // do init
            doDbInit();
        }
    }

    private void doDbInit() {
        initTestTable();
        initHttpMonitorSettingTable();
        initHttpMonitorLogTable();
        initMailConfigTable();
        initMailNotifyMemberTable();
    }

    private void initTestTable() {
        List<String> testFieldList = new ArrayList<>();
        testFieldList.add("ID BIGINT AUTO_INCREMENT(0, 1) PRIMARY KEY, ");
        testFieldList.add("NAME VARCHAR(255) ");
        initTable("TEST", testFieldList);
    }

    private void initHttpMonitorSettingTable() {
        List<String> httpMonitorSettingFieldList = new ArrayList<>();
        httpMonitorSettingFieldList.add("ID BIGINT AUTO_INCREMENT(0, 1) PRIMARY KEY, ");
        httpMonitorSettingFieldList.add("NAME VARCHAR(255), ");
        httpMonitorSettingFieldList.add("URL VARCHAR(255), ");
        httpMonitorSettingFieldList.add("METHOD_TYPE VARCHAR(255), ");
        httpMonitorSettingFieldList.add("PARAMS VARCHAR(255), ");
        httpMonitorSettingFieldList.add("TIME INT, ");
        httpMonitorSettingFieldList.add("WANTED VARCHAR(255) ");
        initTable("T_HTTP_MONITOR_SETTING", httpMonitorSettingFieldList);
    }

    private void initHttpMonitorLogTable() {
        List<String> httpMonitorLogFieldList = new ArrayList<>();
        httpMonitorLogFieldList.add("ID BIGINT AUTO_INCREMENT(0, 1) PRIMARY KEY, ");
        httpMonitorLogFieldList.add("SETTING_ID BIGINT, ");
        httpMonitorLogFieldList.add("CREATE_TIME DATETIME, ");
        httpMonitorLogFieldList.add("STATUS VARCHAR(1), ");
        httpMonitorLogFieldList.add("DURATION INT ");
        initTable("T_HTTP_MONITOR_LOG", httpMonitorLogFieldList);
    }

    private void initMailConfigTable() {
        List<String> mailConfigFieldList = new ArrayList<>();
        mailConfigFieldList.add("ID BIGINT AUTO_INCREMENT(0, 1) PRIMARY KEY, ");
        mailConfigFieldList.add("HOST_NAME VARCHAR(255), ");
        mailConfigFieldList.add("USER_NAME VARCHAR(255), ");
        mailConfigFieldList.add("PASSWORD VARCHAR(255) ");
        initTable("T_MAIL_CONFIG", mailConfigFieldList);
    }

    private void initMailNotifyMemberTable() {
        List<String> mailNotifyMemberFieldList = new ArrayList<>();
        mailNotifyMemberFieldList.add("ID BIGINT AUTO_INCREMENT(0, 1) PRIMARY KEY, ");
        mailNotifyMemberFieldList.add("TO_USER_NAME VARCHAR(255) ");
        initTable("T_MAIL_NOTIFY_MEMBER", mailNotifyMemberFieldList);
    }

    private void initTable(String tableName, List<String> fieldList) {
        StringBuilder initTableSql = new StringBuilder("CREATE TABLE " + tableName + "(");
        for (String field : fieldList) {
            initTableSql.append(field);
        }
        initTableSql.append(");");
        jdbcTemplate.execute(initTableSql.toString());
    }
}
