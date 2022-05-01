package com.nonblocking.service.rest;

import com.nonblocking.model.AcceptPayload;

public interface RestClient {

  void invokeWebhook(AcceptPayload payload, int currentUniqueCount);
}
