package tw.kewang.logback.appender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

public abstract class HttpAppenderAbstract extends UnsynchronizedAppenderBase<ILoggingEvent> {

	/**
	 * Defines default port to get access.
	 */
	protected final static int DEFAULT_PORT = 8080;

	/**
	 * Defines default protocol to use between HTTP or HTTPS.
	 */
	protected final static String DEFAULT_PROTOCOL = "http";

	/**
	 * Defines default content type to send data.
	 */
	protected final static String DEFAULT_CONTENT_TYPE = "json";

	/**
	 * Defines default URL server.
	 */
	protected final static String DEFAULT_URL = "localhost";
	
	/**
	 * Defines default server path.
	 */
	protected final static String DEFAULT_PATH = "/";
	
	/**
	 * Defines default time in seconds to try to reconnect if connection is lost.
	 */
	protected final static int DEFAULT_RECONNECT_DELAY = 30;
	
	protected final String MSG_USING = "Using %s: %s";
	protected final String MSG_NOT_SET = "Assuming default value for %s: %s";

	protected Encoder<ILoggingEvent> encoder;
	protected Layout<ILoggingEvent> layout;
	protected String url;

	protected String protocol;
	protected String path;
	protected int port;
	protected String contentType;
	protected String body;
	protected String headers;
	protected int reconnectDelay;

	@Override
	public void start() {
		if (encoder == null) {
			addError("No encoder was configured. Use <encoder> to specify the fully qualified class name of the encoder to use");
			return;
		}
		
		checkProperties();
		normalizeContentType();
		
		encoder.start();
		super.start();
	}

	protected void checkProperties() {
		if (isStringEmptyOrNull(protocol)) {
			protocol = DEFAULT_PROTOCOL;
			addInfo(String.format(MSG_NOT_SET, "protocol", protocol));
		} else {
			addInfo(String.format(MSG_USING, "protocol", protocol));
		}

		if (isStringEmptyOrNull(url)) {
			url = DEFAULT_URL;
			addInfo(String.format(MSG_NOT_SET, "url", url));
		} else {
			addInfo(String.format(MSG_USING, "url", url));
		}
		
		if (isStringEmptyOrNull(path)) {
			path = DEFAULT_PATH;
			addInfo(String.format(MSG_NOT_SET, "path", path));
		} else {
			addInfo(String.format(MSG_USING, "path", path));
		}

		if (port == 0) {
			port = DEFAULT_PORT;
			addInfo(String.format(MSG_NOT_SET, "port", port));
		} else {
			addInfo(String.format(MSG_USING, "port", port));
		}

		if (isStringEmptyOrNull(contentType)) {
			contentType = DEFAULT_CONTENT_TYPE;
			addInfo(String.format(MSG_NOT_SET, "contentType", contentType));
		} else {
			addInfo(String.format(MSG_USING, "contentType", contentType));
		}
		
		if (reconnectDelay == 0) {
			reconnectDelay = DEFAULT_RECONNECT_DELAY;
			addInfo(String.format(MSG_NOT_SET, "reconnectDelay", reconnectDelay));
		} else {
			addInfo(String.format(MSG_USING, "reconnectDelay", reconnectDelay));
		}
	}

	protected void normalizeContentType() {
		if (contentType.equalsIgnoreCase("json")) {
			contentType = "application/json";
		} else if (contentType.equalsIgnoreCase("xml")) {
			contentType = "application/xml";
		}
	}

	@Override
	public void append(ILoggingEvent event) {
		try {
			HttpURLConnection conn = openConnection();
			transformHeaders(conn);
			byte[] objEncoded = encoder.encode(event);
			sendBodyRequest(objEncoded, conn);
		} catch (IOException e) {
			addError("Houve um erro na conexão: ", e);
			reconnect(event);
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
		IOUtils.write(objEncoded, conn.getOutputStream());
		return showResponse(conn);
	}
	
	protected void reconnect(ILoggingEvent event) {
		try {
			addInfo(String.format("Trying to reconnect in %s seconds", reconnectDelay));
			Thread.sleep(Duration.ofSeconds(reconnectDelay).toMillis());
			append(event);
		} catch (InterruptedException e1) {
			addError("Erro trying to reconnect: ", e1);
			e1.printStackTrace();
		}	
	}

	protected boolean showResponse(HttpURLConnection conn) throws IOException {
		int responseCode = conn.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			addError(String.format("Error to send logs: %s", conn));
			return false;
		}

		String response = IOUtils.toString(conn.getInputStream(), Charset.defaultCharset());
		addInfo(String.format("Response result: %s", response));
		return true;
	}
	
	protected HttpURLConnection openConnection() {
		HttpURLConnection conn = null;
		try {
			URL urlObj = new URL(protocol, url, port, path);
			addInfo("URL: " + urlObj.toString());
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod("POST");
			return conn;
		} catch (Exception e) {
			addError("Error to open connection Exception: ", e);
			return null;
		} finally {
			try {
				if (conn != null) {
					conn.disconnect();
				}
			} catch (Exception e) {
				addError("Error to open connection Exception: ", e);
				return null;
			}
		}
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
	
	public int getReconnectDelay() {
		return reconnectDelay;
	}
	
	public void setReconnectDelay(int reconnectDelay) {
		this.reconnectDelay = reconnectDelay;
	}
	
	protected static boolean isStringEmptyOrNull(String value){
		return value == null || value.isEmpty();
	}
}