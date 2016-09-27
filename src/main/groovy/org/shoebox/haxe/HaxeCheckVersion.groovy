package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import java.util.zip.*
import java.nio.file.*;

public class HaxeCheckVersion extends DefaultTask
{
	@Input
	@Optional
	String requiredVersion;

	@OutputDirectory
	File outputDir;

	public HaxeCheckVersion()
	{
		super();
	}

	@TaskAction
	public void run()
	{
		if (requiredVersion == null)
		{
			Logger.info("No required version");
		}
		else
		{
			resolveVersion();
		}
	}

	void resolveVersion()
	{
		Logger.info("Checking required haxe version");

		HaxeResolveCurrentVersion task = project.tasks.create(
			HaxeResolveCurrentVersion.TASK_DESCRIPTION,
			HaxeResolveCurrentVersion.class
		);

		String currentVersion;

		try
		{
			task.execute();
			currentVersion = task.errorOutput.toString().trim();
		}
		catch (TaskExecutionException e)
		{
			e.printStackTrace();
		}

		if (currentVersion == requiredVersion)
		{
			Logger.info("Haxe version '${currentVersion}' is already installed.");
		}
		else
		{
			String error = "The current haxe version is : '${currentVersion}'\n";
			error += "The project require Haxe version : '${requiredVersion}' \n";
			error += "Download and install the required version from here :\n";
			error += "http://haxe.org/download/version/${requiredVersion}/\n";
			throw new Exception(error);
		}
	}
}
