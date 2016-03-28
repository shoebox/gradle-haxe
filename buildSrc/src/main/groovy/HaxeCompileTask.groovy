import org.haxe.gradle.flavor.*;
import org.gradle.api.tasks.*;
import org.gradle.api.file.*;
import HaxePlugin;
import HaxeExec;

public class HaxeCompileTask extends SourceTask
{
	@Input List<IFlavor> flavors;
	Boolean debug;
	Haxe haxe;
	HaxeExec haxeExecTask;
	HaxePlatform platform;
	String fileName;
	String main;

	@OutputDirectory
	File outputDirectory;
	
	public HaxeCompileTask()
	{
		super();
		println "HaxeCompileTask";
		this.include("**/*.hx");
	}

	@Input
	void setFlavors(List<IFlavor> flavors)
	{
		println "setFlavors";
		this.flavors = flavors;
		computeFlavors();
	}

	@Input
	void setPlatform(HaxePlatform platform)
	{
		println "setPlatform";
		this.platform = platform;
	}

	@Input
	void setHaxe(Haxe haxe)
	{
		println "setHaxe";
		this.haxe = haxe;
	}

	@Input setOuputDirectory(File outputDirectory)
	{
		println "setOuputDirectory";
		this.outputDirectory = outputDirectory;
	}

	public String getOutputPath()
	{
		String debugMode = debug ? "debug" : "release";
		return haxe.binFolder + "/$debugMode/$name/";
	}

	List<IFlavor> getFlavors()
	{
		return flavors;
	}

	void computeFlavors()
	{
		println "computeFlavors";
		for (flavor in flavors)
		{
			if (flavor.main != null)
				main = flavor.main;

			for (cp in flavor.cp)
			{
				File file = new File(cp);
				if (file.exists() && !source.contains(file))
				{
					source(file);
				}
				else
				{
					throw new RuntimeException("File '$file' is not a valid source path.");
				}

			}

			if (flavor.debug != null)
				debug = flavor.debug;

			fileName = flavor.fileName != null ? flavor.fileName : fileName;
		}

		if (fileName == null && platform != null && platform.name != null)
			throw new RuntimeException("No ouput file named define for platform : ${platform}");
	}

	@TaskAction
	public void generate()
	{
		println "generate : " + outputDirectory.path;

		List<String> args = [
			"-${platform.name}", outputDirectory.path + "/" + fileName,
			"-main", main,
		];

		if (debug)
			args.add("-debug");
		
		// Cp
		source.asFileTrees.collect{ it.dir }.unique().each
		{
			it ->
			args.addAll(["-cp", it]);
		};
		
		// Run
		HaxeExec execTask = project.tasks.create("CompileHaxe", HaxeExec)
		{
			setArgs(args);
		};

		execTask.finalizedBy
		{
			println "after !!!";
		}
		execTask.exec();
	}
}
