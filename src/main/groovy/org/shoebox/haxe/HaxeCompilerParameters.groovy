package org.shoebox.haxe;

import org.gradle.model.*;
import org.gradle.api.tasks.*;

@Managed
interface HaxeCompilerParameters
{
	String getOutputFileName();
	void setOutputFileName(String value);

	List<String> getHaxelib();
	void setHaxelib(List<String> value);

	List<String> getFlag();
	void setFlag(List<String> value);

	String getMain();
	void setMain(String value);

	String getTarget();
	void setTarget(String value);

	Boolean getDebug();
	void setDebug(Boolean value);

	Boolean getVerbose();
	void setVerbose(Boolean value);

	List<String> getMacro();
	void setMacro(List<String> value);

	List<String> getCompilerFlag();
	void setCompilerFlag(List<String> value);

	List<String> getResource();
	void setResource(List<String> value);
}
