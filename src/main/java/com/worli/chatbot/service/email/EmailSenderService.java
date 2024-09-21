package com.worli.chatbot.service.email;

import com.worli.chatbot.constants.ApplicationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSenderService {
    private final ApplicationProperties applicationProperties;
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text, boolean isHtmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(applicationProperties.getSenderEmail());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, isHtmlContent);
            mailSender.send(message);

            log.info("Email sent successfully to {}", to);

        } catch (MessagingException e) {
            log.error("Caught exception in sendEmail with stack_trace : {}", ExceptionUtils.getStackTrace(e));
        }
    }
}
