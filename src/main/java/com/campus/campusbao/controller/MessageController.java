package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Message;
import com.campus.campusbao.entity.User;
import com.campus.campusbao.repository.MessageRepository;
import com.campus.campusbao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    MessageRepository msgRepo;

    @Autowired
    UserRepository userRepo;
    @Autowired
    private UserRepository userRepository;
    // 1. 发送消息
    @PostMapping("/send")
    public Result send(
            @RequestParam Integer fromuserid,
            @RequestParam Integer touserid,
            @RequestParam String content,
            @RequestParam(required = false) Integer productid
    ) {
        try {
            Message msg = new Message();
            msg.setFromuserid(fromuserid);
            msg.setTouserid(touserid);
            msg.setContent(content);
            msg.setProductid(productid);
            msg.setCreatetime(new Date());
            msg.setIsread(0);
            msgRepo.save(msg);
            return Result.success("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发送失败：" + e.getMessage());
        }
    }
    // 2.获取我收到的消息+我的聊天列表
    @GetMapping("/my")
    public Result myMsg(@RequestParam Integer userId) {
        try {
            // 查询所有与该用户相关的消息
            List<Message> sendList = msgRepo.findByFromuserid(userId);
            List<Message> receiveList = msgRepo.findByTouserid(userId);

            List<Message> all = new java.util.ArrayList<>();
            all.addAll(sendList);
            all.addAll(receiveList);

            if (all.isEmpty()) {
                return Result.success(new java.util.ArrayList<>());
            }

            // 按时间倒序排序
            all.sort((a, b) -> {
                if (a.getCreatetime() == null || b.getCreatetime() == null) return 0;
                return b.getCreatetime().compareTo(a.getCreatetime());
            });

            // 按聊天对象分组，取最新一条，并统计未读数量
            java.util.Map<Integer, Message> latestMap = new java.util.HashMap<>();
            java.util.Map<Integer, Integer> unreadCountMap = new java.util.HashMap<>();

            for (Message msg : all) {
                int otherId = msg.getFromuserid().equals(userId) ? msg.getTouserid() : msg.getFromuserid();
                if (!latestMap.containsKey(otherId)) {
                    latestMap.put(otherId, msg);
                }
                // 统计未读数量
                if (msg.getFromuserid().equals(otherId) && msg.getTouserid().equals(userId) && msg.getIsread() == 0) {
                    unreadCountMap.put(otherId, unreadCountMap.getOrDefault(otherId, 0) + 1);
                }
            }

            // 转换为前端需要的格式
            List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
            for (java.util.Map.Entry<Integer, Message> entry : latestMap.entrySet()) {
                Integer otherId = entry.getKey();
                Message msg = entry.getValue();

                // 获取对方用户信息
                User otherUser = userRepository.findById(otherId).orElse(null);

                java.util.Map<String, Object> item = new java.util.HashMap<>();
                item.put("id", otherId);
                item.put("username", otherUser != null ? otherUser.getUsername() : "未知用户");
                item.put("avatar", otherUser != null ? otherUser.getAvatar() : "default.jpg");
                item.put("lastMessage", msg.getContent());
                item.put("unreadCount", unreadCountMap.getOrDefault(otherId, 0));
                result.add(item);
            }

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取聊天列表失败：" + e.getMessage());
        }
    }
    // 3.标记消息为已读
    @GetMapping("/markRead")
    public Result markRead(@RequestParam Integer userId, @RequestParam Integer otherId) {
        try {
            // 将所有对方发给我的消息标记为已读
            List<Message> unreadMsgs = msgRepo.findByFromuseridAndTouseridAndIsread(otherId, userId, 0);
            for (Message msg : unreadMsgs) {
                msg.setIsread(1);
                msgRepo.save(msg);
            }
            return Result.success("已读");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记失败：" + e.getMessage());
        }
    }
    // 4. 获取聊天记录
    @GetMapping("/chat")
    public Result chat(@RequestParam Integer from, @RequestParam Integer to) {
        try {
            List<Message> list1 = msgRepo.findByFromuseridAndTouserid(from, to);
            List<Message> list2 = msgRepo.findByFromuseridAndTouserid(to, from);
            List<Message> all = new java.util.ArrayList<>();
            all.addAll(list1);
            all.addAll(list2);
            all.sort((a, b) -> {
                if (a.getCreatetime() == null || b.getCreatetime() == null) return 0;
                return a.getCreatetime().compareTo(b.getCreatetime());
            });
            return Result.success(all);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取聊天记录失败：" + e.getMessage());
        }
    }

    /*// 5. 标为已读
    @GetMapping("/read")
    public Result read(Integer id) {
        Message msg = msgRepo.findById(id).orElse(null);
        if (msg != null) {
            msg.setIsread(1);
            msgRepo.save(msg);
        }
        return Result.success();
    }*/
}