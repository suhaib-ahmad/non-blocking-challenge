package com.nonblocking.controller;

import static com.nonblocking.util.AppConstants.ACCEPT_URL;
import static com.nonblocking.util.AppConstants.API_BASE_URL;
import static com.nonblocking.util.AppConstants.MAX_RAND_MILLION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.nonblocking.enums.ResponseCode;
import com.nonblocking.model.AcceptPayload;
import com.nonblocking.service.RequestService;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = API_BASE_URL)
public class RequestController {

  @Autowired
  RequestService requestService;

  @Autowired
  @Qualifier("asyncRequestExecutor")
  Executor asyncRequestExecutor;

  private static final Random random = new Random();

  @GetMapping(value = ACCEPT_URL)
  public CompletableFuture<ResponseEntity<String>> accept(AcceptPayload payload) {

    /*if(payload.getId()==0)
      payload.setId(random.nextInt(MAX_RAND_MILLION));*/

    log.info("REQ_RECV :: {}", payload);
    return CompletableFuture.supplyAsync(() -> requestService.processRequest(payload), asyncRequestExecutor)
        .thenApplyAsync(resp -> new ResponseEntity<>(ResponseCode.ok.name(), OK))
        .exceptionally(resp -> new ResponseEntity<>(ResponseCode.failed.name(), BAD_REQUEST));
  }
}
