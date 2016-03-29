import org.haxe.gradle.flavor.*;
import org.gradle.api.tasks.*;
import org.gradle.api.file.*;
import HaxePlugin;
import HaxeExec;

public class HaxeCompileTask extends SourceTask
{
	@Input HaxeFlavorVariant variant;

	@OutputDirectory
	File outputDirectory;
	
	public HaxeCompileTask()
	{
		super();
		this.include("**/*.hx");
	}

	@Input
	void setVariant(HaxeFlavorVariant value)
	{
		this.variant = value;
	}

	@Input setOuputDirectory(File outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}

	@TaskAction
	public void generate()
	{
		println "generate : " + source;

		List<String> args = [
			"-${variant.platformName}", outputDirectory.path + "/" + variant.binaryFileName,
			"-main", variant.main,
		];

		if (variant.debug)
			args.add("-debug");
		
		// Cp
		source.asFileTrees.collect{ it.dir }.unique().each
		{
			it ->
			args.addAll(["-cp", it]);
		};
		
		println args;
		// Run
		FileCollection s = this.source;
		HaxeExec execTask = project.tasks.create("CompileHaxe", HaxeExec)
		{
			setArgs(args);
		}
		execTask.exec();
	}
}
