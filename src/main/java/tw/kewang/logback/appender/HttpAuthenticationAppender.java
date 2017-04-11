package tw.kewang.logback.appender;

import java.net.HttpURLConnection;
import java.net.URL;

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

	@SuppressWarnings("restriction")
	@Override
	public void start() {
		super.start();
		if (authentication == null || authentication.isConfigured() == false) {
			addError("No authentication was configured. Use <authentication> to specify the <username> and the <password> for Basic Authentication.");
			return;
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
	
	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

}
