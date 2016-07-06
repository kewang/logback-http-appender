# logback-http-appender

## How to use

```xml
<appender name="REDMINE" class="tw.kewang.logback.appender.RedmineAppender">
  <url>http://example.com</url> <!-- Your Redmine URL -->
  <apiKey>abcdef1234567890</apiKey> <!-- Your Redmine API key-->
  <projectId>5566</projectId> <!-- Your Redmine Project ID -->
  <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
    <pattern>${PATTERN}</pattern>
    <charset>${CHARSET}</charset>
  </encoder>
</appender>
```

## References

* [Chapter 4: Appenders](http://logback.qos.ch/manual/appenders.html)
* [logback-redis-appender](https://github.com/kmtong/logback-redis-appender)
