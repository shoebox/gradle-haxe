package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.api.task.*;
import org.gradle.language.base.*;
import org.gradle.model.*;
import org.shoebox.haxe.HaxeResourceTask;
import org.gradle.model.internal.core.UnmanagedStruct;
import org.apache.log4j.LogManager;
import java.math.BigInteger;
import java.security.MessageDigest
import org.codehaus.groovy.util.HashCodeHelper;
import groovy.json.*;

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
	void validateFlavorDimension(@Each HaxeFlavor flavor, 
		@Path("haxe.dimensions") List<String> dimensions)
	{
		if (flavor.dimension != null)
		{
			if (flavor.dimension == null || !dimensions.contains(flavor.dimension))
			{
				throw new RuntimeException("The flavor : " + flavor 
					+ "contains a unknow flavor dimension : '${flavor.dimension}'");
			}
		}
	}

	@Validate
	void validateFlavorSourceSet(@Each HaxeFlavor flavor)
	{
		flavor.sources.values().each
		{
			it ->
			it.source.srcDirs.each
			{
				file ->
				if (!file.exists())
				{
					throw new RuntimeException("Source path : ${file} on flavor:${flavor} is invalid");
				}
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
			{
				groups.push(list);
			}
		}

		String checkVersionTaskName = "CheckHaxeVersion";
		HaxeCheckVersion checkVersion = tasks.create(checkVersionTaskName,
			HaxeCheckVersion.class,
			new Action<HaxeCheckVersion>()
			{
				@Override
				public void execute(HaxeCheckVersion t)
				{
					t.requiredVersion = model.version;
					t.outputDir = t.project.file(".haxe/");
				}
			}
		);

		GroovyCollections.combinations(groups).each
		{
			combo ->
			final HaxeVariant variant = new HaxeVariant();
			final String comboName = combo.collect { it.name.capitalize() }.join();
			variant.name = "haxe" + comboName;
			combo.each
			{
				flavor ->

				["debug", "verbose", "output", "main", "target", "outputFileName", 
					"platform"].each
				{
					it ->
					if (flavor."$it" != null)
					{
						variant."$it" = flavor."$it";
					}
				};

				["resource", "compilerFlag", "flag", "haxelib", "macro"].each
				{
					if (flavor."$it" != null)
					{
						variant."$it" += flavor."$it";
					}
				};

				flavor.sources.values().each
				{
					it ->
					variant.src += it.source.getSrcDirs();
				};
			}

			if (variant.platform == null)
			{
				throw new RuntimeException("The target : ${variant.name} has no defined target platform");
			}
			else if (variant.main == null)
			{
				throw new RuntimeException("The target : ${variant.name} has no defined main class");
			}

			final List<String> components = combo.collect{it.name};
			final String syncName = "haxeRes" + comboName;

			tasks.create(variant.name,
				HaxeCompileTask.class,
				new Action<HaxeCompileTask>()
				{
					@Override
					public void execute(HaxeCompileTask t)
					{
						File output = new File(t.project.buildDir, 
							(variant.debug ? "debug/" : "release/") + comboName);
						t.setGroup("Haxe");
						t.dependsOn = [checkVersionTaskName, syncName];
						t.components = components;
						t.outputDirectory = output;
						t.source = t.project.files(variant.src);
						t.variant = variant;
						if (variant.outputFileName != null)
						{
							t.output = new File(t.outputDirectory,
								comboName + variant.outputFileName);
						}
					}
				}
			);

			tasks.create(syncName, HaxeResourceTask.class,
				new Action<HaxeResourceTask>()
				{
					@Override
					public void execute(HaxeResourceTask t)
					{
						File output = new File(t.project.buildDir, 
							(variant.debug ? "debug/" : "release/") + comboName);
						t.components = components;
						t.outputDirectory = output;
						t.resDirectory = model.res;
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

	String getVersion();
	void setVersion(String value);

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
