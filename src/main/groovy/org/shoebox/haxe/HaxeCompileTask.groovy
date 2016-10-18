package org.shoebox.haxe;

import org.haxe.gradle.flavor.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
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

	final static String COMPILE_TASK = "Compile the haxe target sources";

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

	void compile() throws Exception
	{
		validate();

		List<String> args = computeArguments();
		HaxeExecTask task = prepareExec(args);
		try
		{
			task.execute();
		}
		catch (Exception e)
		{
			Logger.error(task.errorOutput.toString());
			throw e
		}
	}

	public void validate()
	{
		if (variant.src.size() == 0)	
		{
			throw new RuntimeException(
				"The target '${variant.name}' should have at least one defined 'sourceSet'");
		}
	}

	List<String> computeArguments()
	{
		File output = new File(outputDirectory, variant.outputFileName);
		List<String> args = ["-" + variant.platform, output];

		args.addAll(["-main", variant.main]);
		variant.src.unique().each { args.addAll(["-cp", it.absolutePath]) };
		variant.resource.each { args.addAll(["-resource", it]); }
		variant.macro.each { args.addAll(["--macro", it]); }
		variant.haxelib.each { args.addAll(["-lib", it]); }
		variant.flag.each { args.addAll(["-D", it]); }
		variant.compilerFlag.each { args.push(it); }

		if (variant.debug)
			args.push("-debug");

		if (variant.verbose)
			args.push("-v");

		return args;
	}

	HaxeExecTask prepareExec(final List<String> arguments)
	{
		HaxeExecTask task = project.tasks.create(
			COMPILE_TASK + " for " +  name,
			HaxeExecTask.class,
			new Action<HaxeExecTask>()
			{
				@Override
				public void execute(HaxeExecTask t)
				{
					t.args = arguments;
				}
			}
		);

		return task;
	}
}
