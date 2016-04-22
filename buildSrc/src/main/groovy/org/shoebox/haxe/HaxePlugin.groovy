package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.task.*;
import org.gradle.language.base.*;
import org.gradle.model.*;
import org.gradle.model.internal.core.UnmanagedStruct;

class HaxePlugin implements Plugin<Project>
{
	@Override
	public void apply(Project project)
	{
		project.plugins.apply(org.gradle.language.base.plugins.LanguageBasePlugin);
		project.plugins.apply(HaxePluginRuleSource);
	}	
}

class HaxePluginRuleSource extends RuleSource
{
	@Model
	void haxe(HaxeModel haxe){}

	@Validate
	void validateFlavors(@Each HaxeFlavor flavor)
	{
		flavor.sources.values().each
		{
			it ->
			it.source.srcDirs.each
			{
				file ->
				if (!file.exists())
					throw new RuntimeException("Source path : ${file} on flavor:${flavor} is invalid");
			}
		}		
	}

	@Mutate 
	void createCompileTasks(ModelMap<Task> tasks, HaxeModel model)
	{
		List<HaxeFlavor[]> groups = [];
		model.dimensions.each
		{
			dim ->
			HaxeFlavor[] list = model.flavors.findAll{it.dimension == dim};
			if (list.size() != 0)
				groups.push(list);
		}

		GroovyCollections.combinations(groups).each
		{
			combo ->
			final HaxeVariant variant = new HaxeVariant();
			final String comboName = combo.collect { it.name.capitalize() }.join();
			variant.name = "haxe" + comboName;
			combo.each
			{
				flavor ->
				if (flavor.debug != null)
					variant.debug = flavor.debug;

				if (flavor.verbose != null)
					variant.verbose = flavor.verbose;

				if (flavor.output != null)
					variant.output = flavor.output;

				if (flavor.main != null)
					variant.main = flavor.main;

				if (flavor.target != null)
					variant.target = flavor.target;

				if (flavor.resource != null)
					variant.resource += flavor.resource;

				if (flavor.compilerFlag != null)
					variant.compilerFlag += flavor.compilerFlag;

				if (flavor.flag != null)
					variant.flag += flavor.flag;

				if (flavor.haxelib != null)
					variant.haxelib += flavor.haxelib;

				if (flavor.macro != null)
					variant.macro += flavor.macro;

				if (flavor.outputFileName != null)
					variant.outputFileName = flavor.outputFileName;

				if (flavor.platform != null)
					variant.platform = flavor.platform;

				flavor.sources.values().each
				{
					it ->
					variant.src += it.source.getSrcDirs();
				}
			}

			tasks.create(variant.name,
				HaxeCompileTask.class,
				new Action<HaxeCompileTask>()
				{
					@Override
					public void execute(HaxeCompileTask t)
					{
						t.setGroup("Haxe");
						t.source = t.project.files(variant.src);
						t.outputDirectory = t.project.file("" + t.project.buildDir + "/" + comboName);
						if (variant.outputFileName != null)
						{
							t.output = new File(t.outputDirectory, variant.outputFileName);
						}
						t.variant = variant;
						t.variantHash = t.variant.dump();
					}
				}
			);
		}
	}
}

@Managed
interface HaxeModel
{
	List<String> getDimensions();
	void setDimensions(List<String> value);

	File getRes()
	void setRes(File value);

	ModelMap<HaxeFlavor> getFlavors();
}

@Managed
interface HaxeFlavor extends Named, HaxeCompilerParameters
{
	String getDimension();
	void setDimension(String value);

	String getPlatform();
	void setPlatform(String value);

	void setOutput(File value);
	File getOutput();

	FunctionalSourceSet getSources();
}


interface HaxeResource
{
	File getFile();
	void setFile(File value);

	String getName();
	void setName(String value);
}

@Managed
interface HaxeSourceSet extends LanguageSourceSet
{

}
