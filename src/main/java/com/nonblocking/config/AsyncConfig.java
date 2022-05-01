package com.nonblocking.config;

import static com.nonblocking.util.AppConstants.DEFAULT_KEEP_ALIVE_TIME;
import static com.nonblocking.util.AppConstants.MAX_URL_CONNECTIONS;

import java.time.Duration;
import java.util.concurrent.Executor;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@EnableScheduling
@Configuration
public class AsyncConfig {

  @Bean(name = "asyncRequestExecutor")
  public Executor asyncRequestExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(100);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(10000);//large queue size - 10k
    executor.setKeepAliveSeconds(0);
    executor.setThreadNamePrefix("async-req-exec");
    executor.initialize();
    return executor;
  }

  @Bean(name = "webhookExecutor")
  public Executor webhookExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(100);
    executor.setMaxPoolSize(100);
    executor.setKeepAliveSeconds(0);
    executor.setQueueCapacity(10000);//large queue size - 10k
    executor.setThreadNamePrefix("webhook-exec");
    executor.initialize();
    return executor;
  }

  @Bean
  public RestTemplate restTemplate() {

    //Reuse already opened connections.
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(MAX_URL_CONNECTIONS);
    connectionManager.setDefaultMaxPerRoute(MAX_URL_CONNECTIONS);

    //Use more configurable Apache http client instead of default HttpUrlConnection.
    HttpClient httpClient = HttpClientBuilder.create()
        .setConnectionManager(connectionManager)
        .setKeepAliveStrategy(keepAliveStrategy())
        .build();

    return new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(1000))
        .setReadTimeout(Duration.ofMillis(1000))
        .messageConverters(new StringHttpMessageConverter(), new MappingJackson2HttpMessageConverter())
        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
        .build();
  }

  private ConnectionKeepAliveStrategy keepAliveStrategy() {
    return (httpResponse, httpContext) -> {
      HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
      HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

      while (elementIterator.hasNext()) {
        HeaderElement element = elementIterator.nextElement();
        String param = element.getName();
        String value = element.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          return Long.parseLong(value) * 1000; // convert to ms
        }
      }
      return DEFAULT_KEEP_ALIVE_TIME;
    };
  }
}
