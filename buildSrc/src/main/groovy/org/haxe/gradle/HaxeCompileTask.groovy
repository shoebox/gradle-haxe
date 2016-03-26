package org.haxe.gradle;

import org.haxe.gradle.flavor.*;
import org.gradle.api.tasks.*;
import org.gradle.api.file.*;

public class HaxeCompileTask extends SourceTask
{
	String binFolder;
	@Input List<IFlavor> flavors;

	@Input
	void setBinFolder(String binFolder)
	{
		this.binFolder = binFolder;
	}

	String getBinFolder()
	{
		return binFolder;
	}

	@Input
	void setFlavors(List<IFlavor> flavors)
	{
		this.flavors = flavors;
		computeFlavors();
	}

	List<IFlavor> getFlavors()
	{
		return flavors;
	}

	void computeFlavors()
	{
		setGroup("Haxe");
		for (flavor in flavors)
		{
			for (cp in flavor.cp)
			{
				File file = new File(cp);
				if (file.exists())
				{
					source(file);
				}
				else
				{
					throw new RuntimeException("File '$file' is not a valid source path.");
				}
			}
		}
	}

	@TaskAction
	public void generate()
	{
		println "generate";
		source.asFileTrees.each
		{
			src ->
			println "src : " + src;
		}
	}
}
