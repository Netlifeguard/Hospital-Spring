package com.nie.common.config;

import com.nie.common.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


//专门处理sql底层的错误
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalSqlExceptionCapture {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//此错误为新增员工时违反唯一性约束错误
    public Result ExceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] strings = exception.getMessage().split(" ");
            String msg = strings[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error("失败");
    }
}
