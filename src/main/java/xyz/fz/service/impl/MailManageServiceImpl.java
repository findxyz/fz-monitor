package xyz.fz.service.impl;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fz.dao.CommonDao;
import xyz.fz.dao.PagerData;
import xyz.fz.domain.MailConfig;
import xyz.fz.domain.MailNotifyMember;
import xyz.fz.service.MailManageService;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MailManageServiceImpl implements MailManageService {

    @Resource
    private CommonDao db;

    @Override
    public MailConfig config() {
        String sql = "select * from t_mail_config limit 1 ";
        List<MailConfig> mailConfigList = db.queryListBySql(sql, null, MailConfig.class);
        return mailConfigList.size() > 0 ? mailConfigList.get(0) : null;
    }

    @Override
    public void save(MailConfig mailConfig) {
        String sql = "select * from t_mail_config limit 1 ";
        List<MailConfig> mailConfigList = db.queryListBySql(sql, null, MailConfig.class);
        if (mailConfigList != null && mailConfigList.size() > 0) {
            MailConfig nowMailConfig = mailConfigList.get(0);
            mailConfig.setId(nowMailConfig.getId());
            BeanUtils.copyProperties(mailConfig, nowMailConfig);
            db.update(nowMailConfig);
        } else {
            db.save(mailConfig);
        }
    }

    @Override
    public void mailNotifyMemberSave(MailNotifyMember mailNotifyMember) {
        db.save(mailNotifyMember);
    }

    @Override
    public void mailNotifyMemberDelete(long id) {
        MailNotifyMember notifyMember = db.findById(MailNotifyMember.class, id);
        db.delete(notifyMember);
    }

    @Override
    public PagerData<MailNotifyMember> mailNotifyMemberList(int page, int pageSize) {
        String countSql = "select count(0) from t_mail_notify_member ";
        String sql = "select * from t_mail_notify_member order by id desc ";
        return db.queryPagerDataBySql(countSql, sql, null, page, pageSize, MailNotifyMember.class);
    }

    @Override
    public void sendMail(String title, String msg) {

        MailConfig mailConfig = db.querySingleBySql("select * from t_mail_config limit 1 ", null, MailConfig.class);

        String hostName = mailConfig.getHostName();
        DefaultAuthenticator defaultAuthenticator = new DefaultAuthenticator(mailConfig.getUserName(), mailConfig.getPassword());
        String from = "themessage@126.com";

        List<MailNotifyMember> mailNotifyMemberList = db.queryListBySql("select * from t_mail_notify_member ", null, MailNotifyMember.class);
        for (MailNotifyMember mailNotifyMember : mailNotifyMemberList) {
            try {
                Email email = new SimpleEmail();
                email.setHostName(hostName);
                email.setAuthenticator(defaultAuthenticator);
                email.setFrom(from);
                email.setSubject(title);
                email.setMsg(msg);
                email.addTo(mailNotifyMember.getToUserName());
                email.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
