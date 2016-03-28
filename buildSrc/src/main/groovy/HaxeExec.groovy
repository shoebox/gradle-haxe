import org.gradle.api.tasks.Exec

import java.io.ByteArrayOutputStream;

class HaxeExec extends Exec
{
	public HaxeExec()
	{
		setExecutable("haxe");
		standardOutput = new ByteArrayOutputStream()
	}

	@Override public void exec()
	{
		super.exec();
	}
}
