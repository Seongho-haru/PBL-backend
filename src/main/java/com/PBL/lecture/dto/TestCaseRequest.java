package com.PBL.lecture.dto;

/**
 * 테스트케이스 요청 DTO
 */
public class TestCaseRequest {
    private String input;
    private String expectedOutput;

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
}
