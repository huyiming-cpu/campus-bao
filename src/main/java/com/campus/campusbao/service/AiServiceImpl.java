package com.campus.campusbao.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    // 千帆平台API Key
    private static final String QIANFAN_API_KEY = "bce-v3/ALTAK-SW2rqzEKFTkYmcujp5GBB/7565fe3562a72c733f15331e0df9cb57583cd962";

    @Override
    public String chat(String message) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 千帆标准接口
            String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";

            // 构造请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + QIANFAN_API_KEY);
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            // 构造请求体
            Map<String, Object> body = new HashMap<>();
            body.put("messages", Collections.singletonList(
                    Map.of("role", "user", "content", "你是校园二手交易平台「校园宝」的AI客服，只回答二手交易相关问题，回答简洁、口语化，符合学生语境：" + message)
            ));

            // 发送请求，用JSONObject接收响应（
            org.springframework.http.HttpEntity<Map<String, Object>> request = new org.springframework.http.HttpEntity<>(body, headers);
            org.springframework.http.ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);

            // 直接从result字段取AI回复
            JSONObject resp = response.getBody();
            if (resp == null) {
                return "AI 暂时无法回复，请稍后再试~";
            }
            // 千帆标准返回
            return resp.getString("result");

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 暂时无法回复，请稍后再试~";
        }
    }
}