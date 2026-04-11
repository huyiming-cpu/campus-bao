package com.campus.campusbao.common;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String msg;
    private Object data;

    // 成功响应（带数据）
    public static Result success(Object data) {
        Result r = new Result();
        r.setCode(200);
        r.setData(data);
        return r;
    }

    // 成功响应（无数据）
    public static Result success() {
        return success(null);
    }

    // 失败响应（带提示）
    public static Result error(String msg) {
        Result r = new Result();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}