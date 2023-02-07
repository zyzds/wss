package zyz.wss.service;

public interface MailService {
    /**
     * 
     * @param to 发送方邮箱
     * @param subject   邮件主题
     * @param content   邮件内容
     */
    void sendTemplateMail(String to, String subject, String content);
}