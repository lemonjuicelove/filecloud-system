package com.github.jfcloud.jos.handler;


import com.github.jfcloud.jos.core.exception.operation.DownloadException;
import com.github.jfcloud.jos.core.exception.operation.UploadException;
import com.github.jfcloud.jos.exception.BizException;
import com.github.jfcloud.jos.util.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zj
 * @date 2021-06-08
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public CommonResult bizExceptionHandler(BizException e){
        log.error("发生业务异常！提示信息：{}", e.getErrorMsg());
        return CommonResult.error().message(e.getMessage());
    }

    // 全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult exceptionHandler(Exception e){
        log.error("全局异常处理方法执行");
        e.printStackTrace();
        return CommonResult.error().message("执行了全局异常处理");
    }

    // 文件下载异常处理
    @ExceptionHandler(DownloadException.class)
    @ResponseBody
    public CommonResult downloadExceptionHandler(DownloadException e){
        log.error("文件下载异常");
        e.printStackTrace();
        return CommonResult.error().message(e.getMessage());
    }

    // 文件上传异常处理
    @ExceptionHandler(UploadException.class)
    @ResponseBody
    public CommonResult uploadExceptionHandler(UploadException e){
        log.error("文件上传异常");
        e.printStackTrace();
        return CommonResult.error().message(e.getMessage());
    }

}
