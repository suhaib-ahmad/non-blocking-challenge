package com.nonblocking.service;

import com.nonblocking.enums.ResponseCode;
import com.nonblocking.model.AcceptPayload;
import javax.validation.Valid;

public interface RequestService {

  ResponseCode processRequest(@Valid AcceptPayload acceptPayload);
}
