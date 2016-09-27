package org.shoebox.haxe;

import org.gradle.api.tasks.Exec;

class HaxeExecTask extends Exec
{
	public HaxeExecTask()
	{
		super();
		executable = "haxe";
		setWorkingDir(new File(".").getAbsolutePath());
		errorOutput = new ByteArrayOutputStream();
	}
}
