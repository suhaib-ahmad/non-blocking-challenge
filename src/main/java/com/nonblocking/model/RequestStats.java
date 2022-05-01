package com.nonblocking.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum RequestStats {

  INSTANCE;

  Map<Long, Integer> uniqueNumbers = new ConcurrentHashMap<>();

  public Map<Long, Integer> getUniqueMap() {
    return uniqueNumbers;
  }
}
