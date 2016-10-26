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

		List<String> args = variant.computeArguments(outputDirectory);
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
