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
	final static String CheckVersionTaskName = "CheckHaxeVersion";

	@Model
	void haxe(HaxeModel haxe){}

	@Validate
	void validateFlavorDimension(@Each HaxeFlavor flavor, 
		@Path("haxe.dimensions") List<String> dimensions)
	{
		if (dimensions != null)
		{
			if (flavor.dimension == null || !dimensions.contains(flavor.dimension))
			{
				throw new RuntimeException("The flavor : " + flavor 
					+ "contains a unknow flavor dimension : '${flavor.dimension}'");
			}
		}
	}

	@Validate
	void validateDefaultConfigSourceSet(@Path("haxe.defaultConfig")
		HaxeDefaultConfig defaultConfig)
	{
		validateSourceSet(defaultConfig.sources);
	}

	@Validate
	void validateFlavorSourceSet(@Each HaxeFlavor flavor)
	{
		validateSourceSet(flavor.sources);
	}

	private void validateSourceSet(FunctionalSourceSet sourceSet)
	{
		sourceSet.values().each
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

	private void mergeSourceSet(Object from, Object to)
	{
		if (from != null)
		{
			from.sources.values().each
			{
				it ->
				to.src += it.source.getSrcDirs();
			};
		}
	}

	private HaxeVariant createVariantFromFlavor(HaxeDefaultConfig defaultConfig,
		HaxeFlavor flavor)
	{
		HaxeVariant variant = new HaxeVariant(defaultConfig);
		Util.copyDefault(flavor, variant);
		mergeSourceSet(flavor, variant);
		mergeSourceSet(defaultConfig, variant);
		return variant;
	}

	private HaxeVariant createVariantFromCombo(HaxeDefaultConfig defaultConfig, 
		ArrayList combo)
	{
		HaxeVariant variant = new HaxeVariant(defaultConfig);
		combo.each
		{
			flavor ->
			Util.copyDefault(flavor, variant);
			mergeSourceSet(flavor, variant);
			mergeSourceSet(defaultConfig, variant);
		}
		
		return variant;
	}

	private void validateVariant(HaxeVariant variant)
	{
		if (variant.platform == null)
		{
			throw new RuntimeException("The target : ${variant.name} has no defined target platform");
		}
		else if (variant.main == null)
		{
			throw new RuntimeException("The target : ${variant.name} has no defined main class");
		}
	}

	private void createCompileTask(HaxeVariant variant, ModelMap<Task> tasks)
	{
		tasks.create(variant.getCompileTaskName(),
			HaxeCompileTask.class,
			new Action<HaxeCompileTask>()
			{
				@Override
				public void execute(HaxeCompileTask t)
				{
					String groupName = "Haxe" + ((variant.group != null) 
						? " : " + variant.group
						: "");
					
					t.configurationHash = variant.hash();
					t.setGroup(groupName);
					t.dependsOn = [CheckVersionTaskName, variant.getResourceTaskName()];
					t.outputDirectory = variant.getOutputPath(t.project.buildDir);
					t.source = t.project.files(variant.src.unique(false));
					t.variant = variant;
					if (variant.outputFileName != null)
					{
						t.output = new File(variant.getOutputPath(t.project.buildDir), 
							"/" + variant.outputFileName);
					}
				}
			}
		);
	}

	private void createResourceTask(final ModelMap<Task> tasks,
		final HaxeVariant variant,
		final File resDirectory)
	{
		tasks.create(variant.getResourceTaskName(),
			HaxeResourceTask.class,
			new Action<HaxeResourceTask>()
			{
				@Override
				public void execute(HaxeResourceTask t)
				{
					t.configurationHash = variant.hash();
					t.variant = variant;
					t.name = variant.getResourceTaskName();
					t.components = variant.components;
					t.outputDirectory = variant.getOutputPath(t.project.buildDir);
					t.resDirectory = resDirectory;
				}
			}
		);
	}

	@Mutate 
	void createCompileTasks(final ModelMap<Task> tasks, 
		final HaxeModel model,
		final @Path("haxe.dimensions") List<String> dimensions,
		final @Path("haxe.defaultConfig") HaxeDefaultConfig defaultConfig)
	{
		HaxeCheckVersion checkVersion = tasks.create(CheckVersionTaskName,
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

		if (dimensions != null)
		{
			List<HaxeFlavor[]> groups = [];
			dimensions.each
			{
				dim ->
				HaxeFlavor[] list = model.flavors.findAll{it.dimension == dim};
				if (list.size() != 0)
				{
					groups.push(list);
				}
			}

			GroovyCollections.combinations(groups).each
			{
				combo ->
				final HaxeVariant variant = createVariantFromCombo(defaultConfig, combo);
				variant.name = combo.collect { it.name.capitalize() }.join();
				variant.components = combo.collect{it.name};
				validateVariant(variant);

				createCompileTask(variant, tasks);
				createResourceTask(tasks, variant, model.res);
			}
		}
		else
		{
			model.flavors.each
			{
				it ->
				HaxeVariant variant = createVariantFromFlavor(defaultConfig, it);
				variant.name = it.name;
				variant.components = [it.name];
				validateVariant(variant);

				createCompileTask(variant, tasks);
				createResourceTask(tasks, variant, model.res);
			}	
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

	HaxeDefaultConfig getDefaultConfig();

	ModelMap<HaxeFlavor> getFlavors();
}

@Managed
interface HaxeFlavor extends Named, HaxeCompilerParameters, HaxePlatformParameters
{
	String getDimension();
	void setDimension(String value);

	String getOutputDirectoryName();
	void setOutputDirectoryName(String value);

	void setGroup(String value);
	String getGroup();
}

@Managed
interface HaxeSourceSet extends LanguageSourceSet
{

}

@Managed
interface HaxeDefaultConfig extends HaxeCompilerParameters, HaxePlatformParameters
{

}

@Managed
interface HaxePlatformParameters
{
	FunctionalSourceSet getSources();

	String getPlatform();
	void setPlatform(String value);

	void setOutput(File value);
	File getOutput();
}
