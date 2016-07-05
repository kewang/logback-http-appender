package tw.kewang.redmine.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import org.slf4j.LoggerFactory;

public class RedmineAppender extends AppenderBase<ILoggingEvent> {
    private String url;
    private String apiKey;
    private LoggerContext context;
    private RedmineManager redmineManager;
    private IssueManager issueManager;

    @Override
    public void append(ILoggingEvent event) {
        if (context == null) {
            context = (LoggerContext) LoggerFactory.getILoggerFactory();
        }

        if (url == null || url.length() == 0) {
            url = context.getProperty("REDMINE_URL");
        }

        if (apiKey == null || apiKey.length() == 0) {
            apiKey = context.getProperty("REDMINE_API_KEY");
        }

        createIssue(event);
    }

    private void createIssue(ILoggingEvent event) {
        if (redmineManager == null) {
            redmineManager = RedmineManagerFactory.createWithApiKey(url, apiKey);
        }

        if (issueManager == null) {
            issueManager = redmineManager.getIssueManager();
        }

        Issue issue = IssueFactory.create(22, event.getLoggerName());

        issue.setDescription(event.getFormattedMessage());

        try {
            issueManager.createIssue(issue);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
    }
}