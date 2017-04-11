package tw.kewang.logback.appender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.net.ssl.SSLConfiguration;

public class HttpAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	protected Encoder<ILoggingEvent> encoder;
	protected Layout<ILoggingEvent> layout;
	protected String method;
	protected String url;

	protected String protocol;
	protected String path;
	protected int port;
	protected String contentType;
	protected String body;
	protected String headers;
	protected SSLConfiguration sslConfiguration;

	@Override
	public void append(ILoggingEvent event) {
		createIssue(event);
	}

	public void createIssue(ILoggingEvent event) {
		HttpURLConnection conn = null;

		try {
			URL urlObj = new URL(protocol, url, port, "/");
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod(method);
			transformHeaders(conn);

			boolean isOk = false;
			byte[] objEncoded = encoder.encode(event);
			if (method.equals("GET") || method.equals("DELETE")) {
				isOk = sendNoBodyRequest(conn);
			} else if (method.equals("POST") || method.equals("PUT")) {
				isOk = sendBodyRequest(objEncoded, conn);
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

	protected void transformHeaders(HttpURLConnection conn) {
		conn.setRequestProperty("Content-Type", contentType);
		if (headers == null || headers.isEmpty()) {
			return;
		}

		JSONObject jObj = new JSONObject(headers);
		for (String key : jObj.keySet()) {
			String value = (String) jObj.get(key);
			conn.setRequestProperty(key, value);
		}

	}

	protected boolean sendNoBodyRequest(HttpURLConnection conn) throws IOException {
		return showResponse(conn);
	}

	protected boolean sendBodyRequest(byte[] objEncoded, HttpURLConnection conn) throws IOException {
		conn.setDoOutput(true);

		if (body != null) {
			addInfo("Body: " + body);
			IOUtils.write(body, conn.getOutputStream(), Charset.defaultCharset());
		} else {
			IOUtils.write(objEncoded, conn.getOutputStream());
		}

		return showResponse(conn);
	}

	private boolean showResponse(HttpURLConnection conn) throws IOException {
		int responseCode = conn.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			addError(String.format("Error to send logs: %s", conn));
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

	public Encoder<ILoggingEvent> getEncoder() {
		return encoder;
	}

	public void setEncoder(Encoder<ILoggingEvent> encoder) {
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

	public SSLConfiguration getSsl() {
		return this.sslConfiguration;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}