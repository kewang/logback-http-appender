package tw.kewang.logback.appender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Provide basic http authentication.
 * 
 * @author thiago
 *
 */
public class HttpAuthenticationAppender extends HttpAppenderAbstract {

	protected Authentication authentication;
	protected String encondigUserPassword;

	@SuppressWarnings("restriction")
	@Override
	public void start() {
		super.start();

		if (authentication == null || authentication.getUsername() == null || authentication.getPassword() == null) {
			addError("No set authentication / username / password [" + name + "].");
		}

		String userPassword = authentication.getUsername() + ":" + authentication.getPassword();
		encondigUserPassword = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());

		openConnection();
		addInfo("Using Basic Authentication");
	}

	@Override
	protected HttpURLConnection openConnection() {
		HttpURLConnection conn = null;
		try {
			URL urlObj = new URL(protocol, url, port, path);
			addInfo("URL: " + urlObj.toString());
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestProperty("Authorization", "Basic " + encondigUserPassword);
			conn.setRequestMethod(method);
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

	@Override
	public void append(ILoggingEvent event) {
		try {
			HttpURLConnection conn = openConnection();
			System.out.println(conn);
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
			}
		} catch (IOException e) {
			addError("Houve um erro na conexão: ", e);
			e.printStackTrace();
			reconnect(event);
		}
	}
	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

}
