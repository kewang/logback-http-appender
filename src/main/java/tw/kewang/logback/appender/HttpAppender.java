package tw.kewang.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class HttpAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private LayoutWrappingEncoder<ILoggingEvent> encoder;
    private Layout<ILoggingEvent> layout;
    private String method;
    private String url;
    private String contentType;
    private String body;
    private String headers;

    @Override
    public void start() {
        normalizeMethodName();
        normalizeContentType();

        if (!checkProperty()) {
            addError("No set method / url / contentType / body / headers [" + name + "].");

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

    private void normalizeContentType() {
        if (contentType.equalsIgnoreCase("json")) {
            contentType = "application/json";
        } else if (contentType.equalsIgnoreCase("xml")) {
            contentType = "application/xml";
        }
    }

    private void normalizeMethodName() {
        method = method.toUpperCase();
    }

    private boolean checkProperty() {
        return true;
    }

    @Override
    public void append(ILoggingEvent event) {
        createIssue(event);
    }

    private void createIssue(ILoggingEvent event) {
        HttpURLConnection conn = null;

        try {
            URL urlObj = new URL(url);

            addInfo("URL: " + url);

            conn = (HttpURLConnection) urlObj.openConnection();

            conn.setRequestMethod(method);

            transformHeaders(conn);

            boolean isOk = false;

            if (method.equals("GET") || method.equals("DELETE")) {
                isOk = sendNoBodyRequest(conn);
            } else if (method.equals("POST") || method.equals("PUT")) {
                isOk = sendBodyRequest(conn);
            }

            if (!isOk) {
                addError("Not OK");

                return;
            }
        } catch (Exception e) {
            addError("Exception", e);

            return;
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                addError("Exception", e);

                return;
            }
        }
    }

    private void transformHeaders(HttpURLConnection conn) {
        JSONObject jObj = new JSONObject(headers);

        for (String key : jObj.keySet()) {
            String value = (String) jObj.get(key);

            conn.setRequestProperty(key, value);
        }

        conn.setRequestProperty("Content-Type", contentType);
    }

    private boolean sendNoBodyRequest(HttpURLConnection conn) throws IOException {
        return showResponse(conn);
    }

    private boolean sendBodyRequest(HttpURLConnection conn) throws IOException {
        conn.setDoOutput(true);

        addInfo("Body: " + body);

        IOUtils.write(body, conn.getOutputStream(), Charset.defaultCharset());

        return showResponse(conn);
    }

    private boolean showResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            return false;
        }

        String response = IOUtils.toString(conn.getInputStream(), Charset.defaultCharset());

        addInfo(response);

        return true;
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    public LayoutWrappingEncoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(LayoutWrappingEncoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}