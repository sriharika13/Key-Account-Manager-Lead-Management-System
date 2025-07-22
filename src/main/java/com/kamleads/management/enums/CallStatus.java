package com.kamleads.management.enums;

public enum CallStatus {
    PENDING(),
    COMPLETED(),
    NO_ANSWER(),
    BUSY(),
    RESCHEDULED(),
    CANCELLED();

    public boolean requiresReschedule() {
        return this == NO_ANSWER || this == BUSY || this == RESCHEDULED;
    }
}
