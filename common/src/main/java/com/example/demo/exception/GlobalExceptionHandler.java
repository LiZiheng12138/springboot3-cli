package com.example.demo.exception;


import com.example.demo.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("【系统异常】: ", e);
        return Result.fail(500, "服务器内部错误，请联系管理员");
    }

    /**
     * 参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("【参数缺失】: {}", e.getMessage());
        return Result.fail(400, "缺少必要的参数：" + e.getParameterName());
    }

    /**
     * JSON 参数格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleJsonFormat(HttpMessageNotReadableException e) {
        log.warn("【参数格式错误】: {}", e.getMessage());
        return Result.fail(400, "请求参数格式错误");
    }

    /**
     * @Valid / @Validated 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数验证失败";
        log.warn("【参数验证失败】: {}", message);
        return Result.fail(400, message);
    }

    /**
     * 表单绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "表单参数错误";
        log.warn("【参数绑定失败】: {}", message);
        return Result.fail(400, message);
    }

    /**
     * SQL 异常
     */
    @ExceptionHandler(SQLException.class)
    public Result<?> handleSqlException(SQLException e) {
        log.error("【数据库异常】: {}", e.getMessage(), e);
        return Result.fail(500, "数据库执行异常，请联系管理员");
    }

    /**
     * 自定义业务异常（可选）
     */
    @ExceptionHandler(ServiceException.class)
    public Result<?> handleServiceException(ServiceException e) {
        log.warn("【业务异常】: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }
}