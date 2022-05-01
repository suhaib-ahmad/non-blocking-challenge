package com.nonblocking.util;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileUtils {

  private static final String LOG_FILE = "smaato.log";

  public void logToFile(String message) {

    try {
      message = defaultString(message, "")+System.lineSeparator();
      Files.write(Paths.get(LOG_FILE), message.getBytes(), StandardOpenOption.APPEND);
    } catch (Exception ex) {
      log.error("FILE_EX ::", ex);
    }
  }
}
