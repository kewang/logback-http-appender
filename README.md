# logback-http-appender

[![Build Status](https://travis-ci.org/kewang/logback-http-appender.svg?branch=master)](https://travis-ci.org/kewang/logback-http-appender)

## How to use

```xml
<appender name="STASH" class="com.uppoints.logback.http.appender.HttpAuthenticationAppender">
				<protocol>https</protocol>
				<url>localhost</url>
				<port>443</port>
				<path>/logs/logstash</path>
				<authentication>
					<username>username</username>
					<password>senha</password>
				</authentication>
				<reconnectDelay>10</reconnectDelay>
				
				<encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">        
					<providers>
						<mdc/> <!-- MDC variables on the Thread will be written as JSON fields--> 
						<context/> <!--Outputs entries from logback's context -->                               
						<version/> <!-- Logstash json format version, the @version field in the output-->
						<logLevel/>
						<loggerName/>
						<pattern>
							<pattern> <!-- we can add some custom fields to be sent with all the log entries make filtering easier in Logstash   -->               
								{
								"appName": "upp-quality-control-framework-ws"	<!--or searching with Kibana-->
								}
							</pattern>
						</pattern>
		
						<threadName/>
						<message/>
						
						<logstashMarkers/> <!-- Useful so we can add extra information for specific log lines as Markers--> 
						<arguments/> <!--or through StructuredArguments-->
						
						<stackTrace/>
					</providers>
				</encoder>
			</appender>

```

## References

* [Chapter 4: Appenders](http://logback.qos.ch/manual/appenders.html)
* [logback-redis-appender](https://github.com/kmtong/logback-redis-appender)
