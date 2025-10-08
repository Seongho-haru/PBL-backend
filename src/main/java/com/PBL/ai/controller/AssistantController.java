package com.PBL.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AssistantController {
    //1. 코드 실행/제출 에대한 해설
    //2. 강의/커리큘럼 추천(개인화)
    //3. 커리큘럼 생성 또는 조합

    @PostMapping("/chat/grading")
    public String chat(String message) {
        return assistant.chat(message);
    }
}
