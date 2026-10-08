package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Result chat(@RequestBody ChatRequest request) {
        String reply = aiService.chat(request.getMessage());
        return Result.success(reply);
    }

    public static class ChatRequest {
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
