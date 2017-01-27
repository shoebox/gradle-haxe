package org.shoebox.haxe;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;

public class Logger
{
	static Project project;

	static void setup(Project project)
	{
		Logger.project = project;
	}

	public static void debug(String value)
	{
		project.getLogger().log(LogLevel.DEBUG, value);
	}

	public static void error(String value)
	{
		project.getLogger().log(LogLevel.ERROR, value);
	}

	public static void info(String value)
	{
		project.getLogger().log(LogLevel.INFO, value);
	}

	public static void lifecycle(String value)
	{
		project.getLogger().log(LogLevel.LIFECYCLE, value);
	}

	public static void quiet(String value)
	{
		project.getLogger().log(LogLevel.QUIET, value);
	}

	public static void warn(String value)
	{
		project.getLogger().log(LogLevel.WARN, value);
	}
}
