package com.github.jfcloud.jos.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

// 统一的返回结果
@Data
public class CommonResult {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    private static final int SUCCESS = 2000;
    private static final int ERROR = 2001;

    private CommonResult() {
    }

    public static CommonResult ok(){
        CommonResult result = new CommonResult();
        result.setSuccess(true);
        result.setCode(SUCCESS);
        result.setMessage("成功");
        return result;
    }

    public static CommonResult error(){
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setCode(ERROR);
        result.setMessage("失败");
        return result;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public CommonResult success(Boolean success){
        this.setSuccess(success);
        return this;
    }
    public CommonResult message(String message){
        this.setMessage(message);
        return this;
    }
    public CommonResult code(Integer code){
        this.setCode(code);
        return this;
    }

    public CommonResult data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public CommonResult data(Map<String, Object> map){
        this.setData(map);
        return this;
    }

}
