package com.nie.common.exception;

import com.nie.common.tools.ResponseData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class SQLGlobalException {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseData handleException(SQLIntegrityConstraintViolationException e) {
        final String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] strings = message.split(" ");
            String msg = strings[2] + "已存在";
            return ResponseData.fail(msg);
        }
        return ResponseData.fail("失败");
    }
}
