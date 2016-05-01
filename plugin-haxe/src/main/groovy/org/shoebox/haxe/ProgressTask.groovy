import javax.inject.Inject;
import org.gradle.logging.ProgressLogger;
import org.gradle.logging.ProgressLoggerFactory;
import org.gradle.api.DefaultTask;

public class ProgressTask extends DefaultTask
{
	ProgressLogger progressLogger;

	public ProgressTask()
	{
		super();
	}

	public ProgressLogger getProgressLogger()
	{
		if (progressLogger == null)
			progressLogger = getProgressLoggerFactory().newOperation(getClass());

		return progressLogger;
	}

	@Inject
	protected ProgressLoggerFactory getProgressLoggerFactory()
	{
		 throw new UnsupportedOperationException();
	}
}
