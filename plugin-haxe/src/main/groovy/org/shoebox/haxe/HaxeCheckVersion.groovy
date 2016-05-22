package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import org.gradle.internal.os.OperatingSystem;
import java.util.zip.*
import java.nio.file.*;
import org.gradle.logging.ProgressLogger;

public class HaxeCheckVersion extends ProgressTask
{
	@Input
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
		ProgressLogger logger = getProgressLogger();
		logger.start("Checking required haxe version", "Check version");
		String version = getLocalVersion();
		if (version != requiredVersion)
		{
			logger.progress("The version is outdated");
			String suffix = getSuffix();
			String extension = getPackageExtension();

			logger.progress("Download the version : '${requiredVersion}'");
			URL url = new URL("http://haxe.org/website-content/downloads/${requiredVersion}/"
				+ "downloads/haxe-${requiredVersion}-${suffix}${extension}");
			
			File pckg = project.file(".haxe/haxe-${requiredVersion}" + getPackageExtension());
			pckg << url.openStream();

			project.tasks.create("Copy binary",
				Copy.class, 
				new Action<Copy>()
				{
					@Override
					public void execute(Copy t)
					{
						logger.progress("Unpacking the package");
						t.from(isWindows() 
							? project.zipTree(pckg)
							: project.tarTree(project.resources.gzip(pckg)));
						t.into(new File(outputDir, "sdk"));
						
						saveVersion();
						createSymLink();
					}
				}
			).execute();			
		}

		logger.completed();
	}

	void saveVersion()
	{
		getProgressLogger().progress("Save the current new version");
		File file = new File('.haxe/gradle.properties');
		Properties prop = getProperties();
		prop.setProperty("version", requiredVersion);
		prop.store(file.newWriter(), null);
	}

	void createSymLink()
	{
		getProgressLogger().progress("Create symbolic links");
		File file = new File("/usr/local/lib/haxe");
		file.deleteDir();
		file.delete();
		
		File sdk = new File(outputDir, "/sdk/haxe-${requiredVersion}");
		
		Path from = Paths.get(file.absolutePath);
		Path to = Paths.get(sdk.absolutePath);
		
		Files.createSymbolicLink(from, to);
	}

	boolean isWindows()
	{
		return OperatingSystem.current().isWindows()
	}

	String getExecutableExtension()
	{
		return isWindows() ? ".exe" : "";
	}

	String getPackageExtension()
	{
		return isWindows() ? ".zip" : ".tar.gz";
	}

	String getSuffix()
	{
		OperatingSystem system = OperatingSystem.current();
		String result = "";
		if (system.isMacOsX())
		{
			result = "osx";
		}
		else if (system.isWindows())
		{
			result = "win.zip";
		}
		else if (system.isLinux())
		{
			result = System.getProperty("os.arch") == "x86_64"
				? "linux64"
				: "linux32";
		}

		return result;
	}

	String getLocalVersion()
	{
		String result = null;
		Properties prop = getProperties();
		if (prop != null)
		{
			result = prop.getProperty("version", null);
		}

		return result;
	}

	Properties getProperties()
	{
		new File(".haxe").mkdirs();

		Properties result = null;
		File file = new File('.haxe/gradle.properties');
		if (file.exists())
		{
			result = new Properties();
			result.load(file.newDataInputStream());
		}
		else
		{
			file.createNewFile();
		}
		
		return result;
	}
}
