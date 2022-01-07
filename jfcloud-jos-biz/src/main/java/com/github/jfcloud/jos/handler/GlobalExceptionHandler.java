package com.github.jfcloud.jos.handler;


//import com.github.jfcloud.common.core.util.R;
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
     *
     * @param e   异常
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public CommonResult bizExceptionHandler(BizException e){
        log.error("发生业务异常！提示信息：{}", e.getErrorMsg());
        return CommonResult.error().message(e.getMessage());
    }


}
