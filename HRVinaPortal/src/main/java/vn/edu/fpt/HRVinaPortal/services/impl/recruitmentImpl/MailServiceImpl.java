package vn.edu.fpt.HRVinaPortal.services.impl.recruitmentImpl;


import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.HRVinaPortal.services.recruitment.MailService;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.fromName:HR}")
    private String fromName;

    @Override
    public void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(from, fromName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Send mail failed to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Send mail failed to " + to + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void sendHtmlBulk(List<MailItem> items) {
        for (MailItem it : items) {
            sendHtml(it.to, it.subject, it.html);
        }
    }
}
