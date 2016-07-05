package tw.kewang.redmine.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;

public class RedmineAppender extends AppenderBase<ILoggingEvent> {
    private String url;
    private String apiKey;
    private RedmineManager redmineManager;
    private IssueManager issueManager;

    @Override
    public void start() {
        String propertyRedmineUrl = getContext().getProperty("REDMINE_URL");
        String propertyRedmineApiKey = getContext().getProperty("REDMINE_API_KEY");

        if (propertyRedmineUrl == null || propertyRedmineUrl.length() == 0 || propertyRedmineApiKey == null
                || propertyRedmineApiKey.length() == 0) {
            System.out.println("No set REDMINE_URL / REDMINE_API_KEY");

            return;
        }

        url = propertyRedmineUrl;
        apiKey = propertyRedmineApiKey;
        redmineManager = RedmineManagerFactory.createWithApiKey(url, apiKey);
        issueManager = redmineManager.getIssueManager();

        super.start();
    }

    @Override
    public void append(ILoggingEvent event) {
        createIssue(event);
    }

    private void createIssue(ILoggingEvent event) {
        Issue issue = IssueFactory.create(22, event.getLoggerName());

        issue.setDescription(event.getFormattedMessage());

        try {
            issueManager.createIssue(issue);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
    }
}