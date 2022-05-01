
Solution:
=========
- SpringBoot tomcat is configured to use 200 connectons. So recv. 10k reqs. using CompletableFuture response to offload the processing to separate thread pool. 
- Also using two separate executors for req. processing & for webhook generation to avoid further blockage. 
- Runing cron job for every minute, to process the unique count stats.
- Storing the unique count stats in Concurrent HashMap & Atomic Long value to avoid any blockage on read/write.
- Also overwrite the existing RestTemplate (backed by HttpUrlConnection) with more configurable & high performance apache http client.
- For ideal solution, Use Message Queuing system (e.g. RabbitMQ) to offload processing to BE consumers to release Tomcat thread ASAP. And use distributed cache (e.g. Redis) to store unique counts to avoid dedups.

Enhacements:
============
- Extension 1 completed. Instead of GET , sending POST request to endpoint.



======================================================================
============== 50 Async Threads, 5k requests, 3473/sec ===============
======================================================================


 % ab -n 5000 -c 50 'http://127.0.0.1:8080/api/smaato/accept?id=0&endpoint=http://www.cnn.com'
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 500 requests
Completed 1000 requests
Completed 1500 requests
Completed 2000 requests
Completed 2500 requests
Completed 3000 requests
Completed 3500 requests
Completed 4000 requests
Completed 4500 requests
Completed 5000 requests
Finished 5000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            8080

Document Path:          /api/smaato/accept?id=0&endpoint=http://www.cnn.com
Document Length:        2 bytes

Concurrency Level:      50
Time taken for tests:   1.439 seconds
Complete requests:      5000
Failed requests:        0
Total transferred:      670000 bytes
HTML transferred:       10000 bytes
Requests per second:    3473.79 [#/sec] (mean)
Time per request:       14.394 [ms] (mean)
Time per request:       0.288 [ms] (mean, across all concurrent requests)
Transfer rate:          454.58 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.4      0       3
Processing:     0   14  40.3      3     521
Waiting:        0   13  38.7      3     521
Total:          1   14  40.3      4     521

Percentage of the requests served within a certain time (ms)
  50%      4
  66%      5
  75%      6
  80%      8
  90%     32
  95%     62
  98%    159
  99%    209
 100%    521 (longest request)



======================================================================
=============== 50 Async Threads, 10k Requests, 1504/sec =============
======================================================================



% ab -n 10000 -c 50 'http://127.0.0.1:8080/api/smaato/accept?id=0&endpoint=http://www.cnn.com'
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            8080

Document Path:          /api/smaato/accept?id=0&endpoint=http://www.cnn.com
Document Length:        2 bytes

Concurrency Level:      50
Time taken for tests:   6.647 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1340000 bytes
HTML transferred:       20000 bytes
Requests per second:    1504.53 [#/sec] (mean)
Time per request:       33.233 [ms] (mean)
Time per request:       0.665 [ms] (mean, across all concurrent requests)
Transfer rate:          196.88 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.4      0       7
Processing:     0   33 107.6      4    1271
Waiting:        0   31 100.5      4    1270
Total:          0   33 107.7      4    1272

Percentage of the requests served within a certain time (ms)
  50%      4
  66%      6
  75%      8
  80%     10
  90%     42
  95%    216
  98%    412
  99%    527
 100%   1272 (longest request)


=============================================================================
===== 50 Async Threads, 10k Requests, 7305/sec (on subsequent retries) ======
=============================================================================



% ab -n 10000 -c 50 'http://127.0.0.1:8080/api/smaato/accept?id=0&endpoint=http://www.cnn.com'
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            8080

Document Path:          /api/smaato/accept?id=0&endpoint=http://www.cnn.com
Document Length:        2 bytes

Concurrency Level:      50
Time taken for tests:   1.369 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1340000 bytes
HTML transferred:       20000 bytes
Requests per second:    7305.36 [#/sec] (mean)
Time per request:       6.844 [ms] (mean)
Time per request:       0.137 [ms] (mean, across all concurrent requests)
Transfer rate:          955.97 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       2
Processing:     0    6  10.4      4     110
Waiting:        0    6  10.4      4     109
Total:          0    7  10.4      4     110

Percentage of the requests served within a certain time (ms)
  50%      4
  66%      5
  75%      5
  80%      6
  90%      9
  95%     26
  98%     42
  99%     60
 100%    110 (longest request)
