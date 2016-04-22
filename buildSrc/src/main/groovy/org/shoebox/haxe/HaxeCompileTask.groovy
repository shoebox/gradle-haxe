package org.shoebox.haxe;

import org.haxe.gradle.flavor.*;
import org.gradle.api.tasks.*;
import org.gradle.api.file.*;

public class HaxeCompileTask extends SourceTask
{
	@Optional
	@OutputFile
	File output;

	@Optional
	@OutputDirectory
	File outputDirectory;

	@Input 
	String variantHash;
	
	HaxeVariant variant;
	
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
		List<String> args = ["-main", variant.main];
		
		args.push("-" + variant.platform);
		args.push(variant.outputFileName != null 
			? new File(outputDirectory, variant.outputFileName).absolutePath
			: outputDirectory.absolutePath);

		if (variant.debug)
			args.add("-debug");

		if (variant.verbose)
			args.add("-verbose");

		variant.flag.each{args.addAll(["-D", it])};
		variant.src.each{args.addAll(["-cp", it.absolutePath])};
		variant.macro.each{args.addAll(["--macro", it])};
		variant.haxelib.each{args.addAll(["--lib", it])};
		args.addAll(variant.compilerFlag);

    	HaxeExec exec = new HaxeExec();
		exec.arguments = args;
    	return exec;
    }
}
