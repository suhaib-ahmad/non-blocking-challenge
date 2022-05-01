package com.nonblocking.exception;

import com.nonblocking.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice(basePackages = {"com.nonblocking"})
public class ExceptionHandlerAdvice {

  @ExceptionHandler
  @ResponseBody
  public ResponseEntity<String> handleProcessingException(Throwable throwable) {
    log.error("ERROR ::", throwable);
    return new ResponseEntity<>(ResponseCode.failed.name(), HttpStatus.BAD_REQUEST);
  }
}
