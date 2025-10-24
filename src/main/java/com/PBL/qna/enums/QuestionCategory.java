package com.PBL.qna.enums;

public enum QuestionCategory {
    QUESTION("질문"),
    TIP("팁"),
    BUG_REPORT("버그신고"),
    FEATURE_REQUEST("기능요청"),
    GENERAL("일반");

    private final String description;

    QuestionCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
