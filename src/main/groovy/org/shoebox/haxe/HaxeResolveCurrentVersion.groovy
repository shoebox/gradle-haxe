package org.shoebox.haxe;

public class HaxeResolveCurrentVersion extends HaxeExecTask
{
	public static final String TASK_DESCRIPTION = "Resolve current haxe version";

	public HaxeResolveCurrentVersion()
	{
		super();
		args = ["-version"];
	}
}
