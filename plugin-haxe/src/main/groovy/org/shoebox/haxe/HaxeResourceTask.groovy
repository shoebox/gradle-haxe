package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.*;

public class HaxeResourceTask extends DefaultTask
{
	@Input
	List<String> components;

	@InputDirectory
	File resDirectory;

	@OutputDirectory
	File outputDirectory;

	FileCollection files;

	public HaxeResourceTask()
	{
		super();
	}

	@TaskAction
	public void haxeResouceAction()
	{
		generateFiles();
		copyFiles();
	}

	void generateFiles()
	{
		List<String> combos = ["all"];
		combos.addAll(components);

		List<List<String>> temp = [components];
		components.each
		{
			temp.push(components);
			for (combo in GroovyCollections.combinations(temp))
			{
				String groupName = combo.collect{it}.unique().join("-");
				combos.push(groupName);
			}	
		}

		List<String> valid = combos.findAll{
			new File(resDirectory, it).exists()
		}.unique();

		files = project.files(valid.collect{
			it ->
			new File(resDirectory, it);
		});
	}

	void copyFiles()
	{
		project.tasks.create("Copy files" + name, 
			Copy.class, 
			new Action<Copy>()
			{
				@Override
				public void execute(Copy t)
				{
					t.from(files)
					t.into(outputDirectory);
				}
			}
		).execute();
	}
}
