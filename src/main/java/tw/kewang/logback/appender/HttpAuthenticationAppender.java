package tw.kewang.logback.appender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;

/**
 * Provide basic http authentication.
 * 
 * @author Thiago Diniz da Silveira<thiagods.ti@gmail.com>
 *
 */
public class HttpAuthenticationAppender extends HttpAppenderAbstract {

	private static final String SEPARATOR_BASIC_AUTHENTICATION = ":";
	protected Authentication authentication;
	protected String encondedUserPassword;
	protected SSLConfiguration sslConfiguration;

	@SuppressWarnings("restriction")
	@Override
	public void start() {
		super.start();
		
		if (authentication == null || authentication.isConfigured() == false) {
			addError("No authentication was configured. Use <authentication> to specify the <username> and the <password> for Basic Authentication.");
		}

		String userPassword = authentication.getUsername() + SEPARATOR_BASIC_AUTHENTICATION + authentication.getPassword();
		encondedUserPassword = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());

		openConnection();
		addInfo("Using Basic Authentication");
	}

	@Override
	protected HttpURLConnection openConnection() {
		HttpURLConnection conn = null;
		try {
			URL urlObj = new URL(protocol, url, port, path);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestProperty("Authorization", "Basic " + encondedUserPassword);
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

	
	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
	
	public SSLConfiguration getSsl() {
		return sslConfiguration;
	}
	
	public void setSsl(SSLConfiguration sslConfiguration) {
		this.sslConfiguration = sslConfiguration;
	}

}
