package com.worli.chatbot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSenderRequest {
    private String toEmail;
    private String subject;
    private String emailBody;
    private boolean isHtmlContent;
}
