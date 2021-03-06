package cn.imustacm.problem.controller.advice;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.common.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理类
 *
 * @author liandong
 * Date: 2019/08/18
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {


    @ExceptionHandler(Exception.class)
    public Resp exceptionHandler(Exception e) {
        log.info("service err.", e);
        return new Resp(ErrorCodeEnum.SERVER_ERR);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Resp handleBindException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        log.info("参数校验异常: {} {}", fieldError.getDefaultMessage(), fieldError.getField());
        return new Resp(ErrorCodeEnum.BIZ_PARAM_ERR, fieldError.getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    public Resp handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        log.info("必填校验异常: {} {}", fieldError.getDefaultMessage(), fieldError.getField());
        return new Resp(ErrorCodeEnum.BIZ_PARAM_ERR, fieldError.getDefaultMessage());
    }
}
