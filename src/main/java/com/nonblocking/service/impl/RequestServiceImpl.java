package com.nonblocking.service.impl;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.nonblocking.enums.ResponseCode;
import com.nonblocking.model.AcceptPayload;
import com.nonblocking.service.RequestService;
import com.nonblocking.service.rest.RestClient;
import com.nonblocking.util.FileUtils;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {

  /**
   * Instead of HashSet (backed by simple HashMap) using ConcurrentHashMap to have high concurrency (default 16) level.
   */
  private static final Map<Long, AtomicLong> uniqueNumbers = new ConcurrentHashMap<>();

  @Autowired
  @Qualifier("webhookExecutor")
  Executor webhookExecutor;

  @Autowired
  RestClient restClient;

  @Autowired
  FileUtils fileUtils;

  @Override
  public ResponseCode processRequest(@Valid AcceptPayload payload) {
    uniqueNumbers.computeIfAbsent(payload.getId(), value -> new AtomicLong(0))
        .incrementAndGet();

    //log.info("PROCESS :: {}", payload);
    if(isNotBlank(payload.getEndpoint())) {
      CompletableFuture.runAsync(
              () -> restClient.invokeWebhook(payload, uniqueNumbers.keySet().size()), webhookExecutor)
          .orTimeout(10, TimeUnit.SECONDS);
    }
    return ResponseCode.ok;
  }

  @Scheduled(cron = "0 * * * * *")//Start of every minute
  void logUniqueCounts() {
    synchronized (uniqueNumbers) {
      int uniqueCounts = uniqueNumbers.keySet().size();
      fileUtils.logToFile("time: "+ISO_DATE_TIME.format(LocalDateTime.now())+", unique.count:"+uniqueCounts);

      log.info("timestamp: {}, unique.numbers.size:{}",
          ISO_DATE_TIME.format(LocalDateTime.now()), uniqueNumbers.keySet().size());
      uniqueNumbers.clear();
    }
  }
}
