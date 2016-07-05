package tw.kewang.redmine.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class HttpAppender extends AppenderBase<ILoggingEvent> {
    @Override
    public void append(ILoggingEvent event) {
        try {
            HttpHelper.sendGet();
        } catch (Exception e) {

        }
    }
}