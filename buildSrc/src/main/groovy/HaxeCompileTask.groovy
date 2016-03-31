import org.haxe.gradle.flavor.*;
import org.gradle.api.tasks.*;
import org.gradle.api.file.*;
import HaxePlugin;
import HaxeExec;

public class HaxeCompileTask extends SourceTask
{
	@OutputDirectory
	File outputDirectory;

	@Input Boolean debug;
	@Input String binaryFileName;
	@Input String main;
	@Input String platformName;
	@Input List<String> flags;
	
	public HaxeCompileTask()
	{
		super();
		this.include("**/*.hx");
	}

	@TaskAction
    public void run()
    {
    	HaxeExec task = prepareExec();
    	try
    	{
        	task.build().start().waitForFinish().assertNormalExitValue();
        }
        catch (Exception e)
        {
        	println task.errorOutput
        }
    }

    HaxeExec prepareExec()
    {
    	List<String> args = [
			"-${platformName}", outputDirectory.path + "/" + binaryFileName,
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

		// Flags
		flags.unique().each
		{
			it ->
			args.addAll(["-D", it]);
		};
		println args;
		
    	HaxeExec exec = new HaxeExec();
		exec.arguments = args;
    	return exec;
    }
}
