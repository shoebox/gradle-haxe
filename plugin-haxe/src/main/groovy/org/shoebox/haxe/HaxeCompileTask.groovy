package org.shoebox.haxe;

import org.haxe.gradle.flavor.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import org.shoebox.haxe.HaxePlugin;
import org.shoebox.haxe.HaxeResourceTask;
import org.gradle.api.tasks.ParallelizableTask;

@ParallelizableTask
public class HaxeCompileTask extends SourceTask
{
	@Optional
	@OutputFile
	File output;

	@Optional
	@OutputDirectory
	File outputDirectory;

	@Input
	String configurationHash;

	HaxeVariant variant;
	
	public HaxeCompileTask()
	{
		super();
		this.include("**/*.hx");
	}

	@TaskAction
	public void run()
	{	
		compile();
	}

	void compile()
	{
		HaxeExec task = prepareExec();
		task.redirectErrorStream()
			.build()
			.start()
			.waitForFinish()
			.assertNormalExitValue();
	}

	HaxeExec prepareExec()
	{
		File output = new File(outputDirectory, variant.outputFileName);
		List<String> args = ["-" + variant.platform, output];

		args.addAll(["-main", variant.main]);
		variant.src.unique().each{ args.addAll(["-cp", it.absolutePath]) };
		variant.resource.each{ args.addAll(["-resource", it]); }
		variant.macro.each{ args.addAll(["--macro", it]); }
		variant.haxelib.each{ args.addAll(["-lib", it]); }
		variant.flag.each{ args.addAll(["-D", it]); }
		variant.compilerFlag.each{ args.push(it); }

		if (variant.debug)
			args.push("-debug");
		
		if (variant.verbose)
			args.push("-v");

		HaxeExec exec = new HaxeExec();
		exec.arguments = args;
		return exec;
	}
}
