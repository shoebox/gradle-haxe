package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.tasks.*;

public class HaxeTaskGroupTask extends DefaultTask
{
	List<String> dependencies;

	public HaxeTaskGroupTask()
	{
		super();
	}

	public void setTest(List<String> value)
	{
		this.dependencies = value;
	}

	@TaskAction
	public void run()
	{
		String variants = dependencies.collect { "\t - " + it }.join("\n");
		String message = "No build type specified.\n" +
			"Please run one of the following build types variants : \n" +
			variants;

		throw new Exception(message);
	}
}
