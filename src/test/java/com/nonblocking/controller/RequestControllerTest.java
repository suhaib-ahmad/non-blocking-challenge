package com.nonblocking.controller;

import static com.nonblocking.util.AppConstants.ACCEPT_URL;
import static com.nonblocking.util.AppConstants.API_BASE_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nonblocking.service.impl.RequestServiceImpl;
import com.nonblocking.service.rest.RestClient;
import com.nonblocking.util.FileUtils;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  Executor webhookExecutor;

  @Mock
  RestClient restClient;

  @Mock
  FileUtils fileUtils;

  @InjectMocks
  RequestServiceImpl requestService;

  @Test
  public void uniqueCountRequest() throws Exception {
    this.mockMvc.perform(get(API_BASE_URL+ACCEPT_URL).param("id", "1"))
        .andDo(print())
        .andExpect(status().isOk());
  }
}