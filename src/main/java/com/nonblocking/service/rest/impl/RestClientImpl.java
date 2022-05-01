package com.nonblocking.service.rest.impl;

import com.nonblocking.model.AcceptPayload;
import com.nonblocking.service.rest.RestClient;
import com.nonblocking.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class RestClientImpl implements RestClient {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  FileUtils fileUtils;

  private <T> ResponseEntity<T> handlePostRequest(String url, Object requestBody, Class<T> clazz) {

    log.info("WEBHOOK_URL :: {}", url);
    HttpEntity<Object> entity = new HttpEntity<>(requestBody, buildHeaders());
    return restTemplate.exchange(url, HttpMethod.POST, entity, clazz);
  }

  private HttpHeaders buildHeaders() {
    HttpHeaders headers = new HttpHeaders();
    return headers;
  }

  @Override
  public void invokeWebhook(AcceptPayload payload, int currentUniqueCount) {

    String uri = UriComponentsBuilder
        .fromUriString(payload.getEndpoint())
        .queryParam("uniqueCount", currentUniqueCount)
        .build().toUri().toString();

    ResponseEntity<Void> responseEntity = handlePostRequest(uri, payload, Void.class);
    log.info("RESPONSE_CODE :: code:{}, id:{}, unique.count:{}",
        responseEntity.getStatusCode().value(), payload.getId(), currentUniqueCount);
  }
}
