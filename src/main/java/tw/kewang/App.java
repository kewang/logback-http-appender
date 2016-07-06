package tw.kewang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        String abc = null;

        try {
            if (abc.length() == 0) {
                LOG.info("No String");
            }
        } catch (Exception e) {
            LOG.error("Caught Exception: ", e);
        }
    }
}
