package vn.edu.fpt.HRVinaPortal.services.recruitment;

import java.util.List;

public interface MailService{

    void sendHtml(String to, String subject, String htmlBody);

    void sendHtmlBulk(List<MailItem> items);

    class MailItem {
        public final String to;
        public final String subject;
        public final String html;

        public MailItem(String to, String subject, String html) {
            this.to = to; this.subject = subject; this.html = html;
        }
    }
}
