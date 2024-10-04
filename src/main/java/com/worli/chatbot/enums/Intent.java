package com.worli.chatbot.enums;

import lombok.Getter;

@Getter
public enum Intent {
    SCHEDULE_MEETING(1),
    CANCEL_MEETING(2),
    RESCHEDULE_MEETING(3),
    IRRELEVANT_INTENT(4);

    private Integer intent;
    Intent(Integer intent) {
        this.intent =  intent;
    }
}
