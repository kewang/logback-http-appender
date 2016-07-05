package tw.kewang.redmine.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;

public class RedmineAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private String url;
    private String apiKey;
    private int projectId;
    private RedmineManager redmineManager;
    private IssueManager issueManager;

    @Override
    public void start() {
        String propertyRedmineUrl = getContext().getProperty("REDMINE_URL");
        String propertyRedmineApiKey = getContext().getProperty("REDMINE_API_KEY");
        int propertyRedmineProjectId = -1;

        try {
            propertyRedmineProjectId = Integer.valueOf(getContext().getProperty("REDMINE_PROJECT_ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkProperty(propertyRedmineUrl, propertyRedmineApiKey, propertyRedmineProjectId)) {
            System.out.println("No set REDMINE_URL / REDMINE_API_KEY / REDMINE_PROJECT_ID");

            return;
        }

        url = propertyRedmineUrl;
        apiKey = propertyRedmineApiKey;
        projectId = propertyRedmineProjectId;
        redmineManager = RedmineManagerFactory.createWithApiKey(url, apiKey);
        issueManager = redmineManager.getIssueManager();

        super.start();
    }

    private boolean checkProperty(String propertyRedmineUrl, String propertyRedmineApiKey, int propertyRedmineProjectId) {
        return propertyRedmineUrl == null || propertyRedmineUrl.length() == 0 || propertyRedmineApiKey == null
                || propertyRedmineApiKey.length() == 0 || propertyRedmineProjectId == -1;
    }

    @Override
    public void append(ILoggingEvent event) {
        createIssue(event);
    }

    private void createIssue(ILoggingEvent event) {
        Issue issue = IssueFactory.create(projectId, event.getLoggerName());

        issue.setDescription(event.getFormattedMessage());

        try {
            issueManager.createIssue(issue);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
    }
}