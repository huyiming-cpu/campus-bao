package com.campus.campusbao.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;


@Service
public class AiServiceImpl implements AiService {

    // 千帆平台API Key
    private static final String QIANFAN_API_KEY = "bce-v3/ALTAK-SW2rqzEKFTkYmcujp5GBB/7565fe3562a72c733f15331e0df9cb57583cd962";

 /*   @Override
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
}*/
 @Override
 public String chat(String message) {
     try {
         RestTemplate restTemplate = new RestTemplate();

         String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";

         org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
         headers.set("Authorization", "Bearer " + QIANFAN_API_KEY);
         headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

         // ✅ 规则写在这里，每次提问都会带上
         String rules =
                 "你是校园二手交易平台「校园宝」的AI客服。\n" +
                         "规则如下：\n" +
                         "1. 发布商品：在个人中心-我的宝贝里发布，填写名称、价格、分类、描述，上传图片即可发布\n" +
                         "2. 购买流程：浏览商品 → 加入购物车 → 选地址 → 提交订单 → 支付，同时商品详情页有更多好物也可以买，开学季/毕业季活动也有礼包打折更划算，也可以在首页搜索商品再点击详情购买，" +
                         "可以线上购买，也可以线下购买，创建订单，双方确定好自提点\n" +
                         "3. 订单状态：待付款 → 待发货 → 待收货 → 已完成\n" +
                         "4. 退款流程：买家申请 → 卖家同意 → 钱款原路返回\n" +
                         "5. 评价系统：交易完成后互评，好评增加信用分\n" +
                         "6. 信用等级：极好(≥100)、优秀(80-99)、良好(60-79)、一般(40-59)、较差(≤39)，每次交易之后双方互评都可以加信用分，" +
                         "5星加3分，4星加2分，3星加1分，2星不加分，1星减一分\n" +
                         "7. 礼包功能：毕业季8.5折、开学季9折，\n" +
                         "8. 优惠券：在毕业季或开学季栏目有入口，抽奖获得，购买礼包时可用，有机会抽到免单券！\n" +
                         "9.需求广场：在这里可以发表我想要/我有闲置/以物换物，可以联系发言者\n"+
                         "请根据以上规则回答用户问题，语气友好、简洁、口语化。\n\n" +
                         "用户问题：" + message;

         Map<String, Object> body = new HashMap<>();
         body.put("messages", Collections.singletonList(
                 Map.of("role", "user", "content", rules)
         ));

         org.springframework.http.HttpEntity<Map<String, Object>> request = new org.springframework.http.HttpEntity<>(body, headers);
         org.springframework.http.ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);

         JSONObject resp = response.getBody();
         if (resp == null) {
             return "AI 暂时无法回复，请稍后再试~";
         }

         String result = resp.getString("result");
         return result != null ? result : "AI 回复失败，请稍后再试~";

     } catch (Exception e) {
         e.printStackTrace();
         return "AI 暂时无法回复，请稍后再试~";
     }
 }
}