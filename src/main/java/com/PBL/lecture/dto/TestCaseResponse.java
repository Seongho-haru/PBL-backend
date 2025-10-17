package com.PBL.lecture.dto;

import lombok.Data;

/**
 * 테스트케이스 응답 DTO
 */
@Data
public class TestCaseResponse {
    private String input;
    private String expectedOutput;
    private Integer orderIndex;

    public TestCaseResponse() {}

    public TestCaseResponse(String input, String expectedOutput, Integer orderIndex) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.orderIndex = orderIndex;
    }
}
