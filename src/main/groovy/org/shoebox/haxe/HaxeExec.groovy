package org.shoebox.haxe;

import org.gradle.process.internal.ExecHandleBuilder;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecHandleListener;

import java.io.ByteArrayOutputStream;

class HaxeExec extends ExecHandleBuilder
{
	public HaxeExec()
	{
		super();
		executable = "haxe";
		setWorkingDir(new File(".").getAbsolutePath());
		errorOutput = new ByteArrayOutputStream();
	}

	public void setArguments(List<String> args)
	{
		this.args = args;
	}

	public List<String> getAllArguments()
	{
		return args;
	}
}
