import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.fz.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {Application.class})
public class H2Test {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void createTableTest() throws InterruptedException {
        String createTableSql = "CREATE TABLE TEST(ID BIGINT AUTO_INCREMENT(0, 1) PRIMARY KEY, NAME VARCHAR(255));";
        jdbcTemplate.execute(createTableSql);
        Thread.sleep(2000L);
    }

    @Test
    public void deleteTableTest() throws InterruptedException {
        String deleteTableSql = "DROP TABLE IF EXISTS TEST;";
        jdbcTemplate.execute(deleteTableSql);
        Thread.sleep(2000L);
    }

    @Test
    public void insertTableTest() throws InterruptedException {
        String insertSql = "INSERT INTO TEST(NAME) VALUES('abc123');";
        jdbcTemplate.execute(insertSql);
        Thread.sleep(2000L);
    }

    @Test
    public void queryTableTest() throws InterruptedException {
        String queryTableSql = "SELECT * FROM TEST ORDER BY ID;";
        System.out.println(jdbcTemplate.queryForList(queryTableSql));
        Thread.sleep(2000L);
    }
}
