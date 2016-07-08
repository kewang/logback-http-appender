package tw.kewang.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.apache.commons.io.FileUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.nio.charset.Charset;

public class HttpAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private LayoutWrappingEncoder<ILoggingEvent> encoder;
    private Layout<ILoggingEvent> layout;
    private String config;

    @Override
    public void start() {
        if (!checkProperty()) {
            addError("No set url / apiKey / projectId / title [" + name + "].");

            return;
        }

        if (encoder == null) {
            addError("No encoder set for the appender named [" + name + "].");

            return;
        }

        try {
            encoder.init(System.out);

            layout = encoder.getLayout();
        } catch (Exception e) {
            addError("Exception", e);
        }

        super.start();
    }

    private boolean checkProperty() {
        return true;
    }

    @Override
    public void append(ILoggingEvent event) {
        createIssue(event);
    }

    @SuppressWarnings("Since15")
    private void createIssue(ILoggingEvent event) {
        try {
            File file = new File(getClass().getClassLoader().getResource(config).getFile());
            String script = FileUtils.readFileToString(file, Charset.defaultCharset());

            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");

            engine.eval(script);
        } catch (Exception e) {
            addError("Exception", e);
        }
    }

    public LayoutWrappingEncoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(LayoutWrappingEncoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}