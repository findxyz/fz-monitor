package xyz.fz.service;

import xyz.fz.dao.PagerData;
import xyz.fz.domain.MailConfig;
import xyz.fz.domain.MailNotifyMember;

public interface MailManageService {

    MailConfig config();

    void save(MailConfig mailConfig);

    void mailNotifyMemberSave(MailNotifyMember mailNotifyMember);

    void mailNotifyMemberDelete(long id);

    PagerData<MailNotifyMember> mailNotifyMemberList(int page, int pageSize);

    void sendMail(String title, String msg);
}
