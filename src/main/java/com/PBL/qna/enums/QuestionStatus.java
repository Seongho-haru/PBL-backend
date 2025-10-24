package com.PBL.qna.enums;

public enum QuestionStatus {
    UNRESOLVED("미해결"),
    RESOLVED("해결");

    private final String description;

    QuestionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
