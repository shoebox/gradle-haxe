package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import org.gradle.internal.os.OperatingSystem;
import java.util.zip.*
import java.nio.file.*;
import org.gradle.logging.ProgressLogger;
import org.gradle.process.internal.ExecException;

public class HaxeCheckVersion extends ProgressTask
{
	@Input
	@Optional
	String requiredVersion;

	@OutputDirectory
	File outputDir;

	public HaxeCheckVersion()
	{
		super();
		outputs.upToDateWhen { false }
	}

	@TaskAction
	public void run()
	{
		if (requiredVersion == null)
		{
			return;
		}

		ProgressLogger logger = getProgressLogger();
		logger.start("Checking required haxe version", "Check version");
		String version = getLocalVersion();
		if (version != null)
		{
			Logger.progress("Installed version is : " + version);
			boolean ok = version.equals(requiredVersion);
			if (ok)
			{
				Logger.progress("Installed version is up to date");
			}
			else
			{
				Logger.error("Please install the version ${requiredVersion}");
				throw "Invalid version";
			}
		}
		else
		{	
			Logger.error("The project require haxe ${requiredVersion}");	
		}
	}

	String getLocalVersion()
	{
		HaxeExec exec = new HaxeExec();
		exec.standardOutput = new ByteArrayOutputStream();
		exec.arguments = ["-version"];

		String result = null;

		try
		{
			exec.redirectErrorStream()
				.build()
				.start()
				.waitForFinish();

			result = exec.standardOutput.toString().trim();
		}
		catch (ExecException e)
		{
			Logger.error("Haxe is not installed, or not added into PATH");
		}

		return result;
	}
}
