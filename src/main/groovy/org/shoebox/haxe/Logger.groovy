package org.shoebox.haxe;

import static org.gradle.logging.StyledTextOutput.Style;

import org.gradle.logging.StyledTextOutput;
import org.gradle.logging.StyledTextOutputFactory;
import org.gradle.api.Project;

public class Logger
{
	final static String TAG = "shoebox-haxe-log";

	static StyledTextOutput output;

	public static void error(String value)
	{
		output.text("").withStyle(Style.Failure).println(value);
	}

	public static void progress(String value)
	{
		output.text("").withStyle(Style.ProgressStatus).println(value);
	}

	public static void success(String value)
	{
		output.text("").withStyle(Style.Success).println(value);
	}

	public static void setup(Project project)
	{
		output = project.services.get(StyledTextOutputFactory).create(TAG);
  		output.style(Style.Normal);
	}
}
