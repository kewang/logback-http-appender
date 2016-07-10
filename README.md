# logback-http-appender

## How to use

```xml
<appender name="HTTP" class="tw.kewang.logback.appender.HttpAppender">
  <method>post</method>
  <url>http://example.com/issues.json</url>
  <contentType>json</contentType>
  <body>{"issue": {"subject": "$subject", "project_id": 22, "description": "$event"}}</body>
  <headers>{"X-Redmine-API-Key": "hello-this-is-a-key", "Another": "also-key"}</headers>
  <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
    <pattern>${PATTERN}</pattern>
    <charset>${CHARSET}</charset>
  </encoder>
</appender>

```

## References

* [Chapter 4: Appenders](http://logback.qos.ch/manual/appenders.html)
* [logback-redis-appender](https://github.com/kmtong/logback-redis-appender)