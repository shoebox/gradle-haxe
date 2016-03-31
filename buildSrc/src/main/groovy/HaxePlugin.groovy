import groovy.util.GroovyCollections;
import org.gradle.internal.HasInternalProtocol;
import org.gradle.internal.reflect.Instantiator
import org.gradle.api.*;
import org.gradle.language.base.*;
import org.gradle.model.*;
import org.gradle.platform.base.*;

class HaxePlugin extends RuleSource
{
	@Mutate 
	void createCompileTasks(ModelMap<Task> tasks, HaxeModel haxe)
	{
		for (p in haxe.platforms)
		{
			List<HaxeFlavor> df;
			Map<String, HaxeFlavor[]> flavorsByDimensions = new HashMap<String, HaxeFlavor[]>();
			
			for (f in p.flavors)
			{
				if (f.name == "all")
					continue;

				if (f.dimension == null && p.dimensions != null)
					throw new RuntimeException("Flavor '$f' has no flavor dimension.");
				else if (!p.dimensions.contains(f.dimension))
					throw new RuntimeException("Flavor '$f' has unknow dimension '" + f.dimension + "'");	
				
				df = flavorsByDimensions.get(f.dimension);
				if (df == null)
					df = [f];
				else
					df.add(f);
				flavorsByDimensions.put(f.dimension, df);
			}

			HaxeFlavor[] flavorList;
			List groups = [];
			List names;
			for (d in p.dimensions)
			{
				names = flavorsByDimensions.get(d).findAll();
				if (names.size() > 0)
				groups.push(names);
			}

			final HaxePlatform platformClone = p;
			for (c in GroovyCollections.combinations(groups))
			{
				final List comboClone = c.clone();
				final String name = p.name + c.collect { it.name.capitalize() }.join();
				tasks.create(name, HaxeCompileTask.class)
				{
					task ->
						
					HaxeFlavorVariant variant = createVariant(project, 
						haxe, 
						platformClone, 
						comboClone, 
						name);

					String debugMode = variant.debug ? "debug" : "release";
					File file = project.file(haxe.binFolder + "/$debugMode/$name/");
					task.flags = variant.flag;
					task.debug = variant.debug;
					task.platformName = variant.platformName;
					task.binaryFileName = variant.binaryFileName;
					task.main = variant.main;
					task.outputDirectory = file;

					for (cp in variant.cp)
						task.source(cp);
				}
			}
		}
	}

	public static HaxeFlavorVariant createVariant(Project project,
		HaxeModel model, 
		HaxePlatform platform, 
		List<HaxeFlavor> flavors,
		String variantName)
	{
		HaxeFlavorVariant result = new HaxeFlavorVariant();
		result.name = variantName;
		List f = flavors.collect{it};

		// Cp
		result.cp = [];
		for (value in f.collect{it.cp}.flatten().unique())
			result.cp.add(new File(value));

		// D flags
		result.flag = f.collect{it.flag}.flatten().unique();
		result.flag.removeAll([null]);

		// Macros
		result.macro = f.collect{it.macro}.flatten().unique();

		// 
		result.debug = f.collect{it.debug}.max();

		// 
		result.binaryFileName = f.collect{it.binaryFileName}.max();

		//
		result.platformName = platform.name;
		result.main = f.collect{it.main}.max();

		
		/*
		result.output = project.file(model.binFolder + "/$debugMode/$variantName/");
		println result.cp;
		println result.flag;
		println result.macro;
		println result.debug;
		println result.main;
		println result.output;
		*/

		return result;
	}

	@Model
	void haxe(HaxeModel haxe) {}
	
	@Model
	void platforms(@Path("haxe.platforms") HaxePlatform p) {}
}

@Managed 
interface HaxeModel extends HaxePlatform
{
	void setBinFolder(String folder);
	String getBinFolder();

	ModelMap<HaxePlatform> getPlatforms()
}

@Managed 
interface HaxePlatform extends Named
{
	void setDimensions(List<String> dimensions);
	List<String> getDimensions();

	ModelMap<HaxeFlavor> getFlavors();
	ModelMap<HaxeLib> getHaxelibs();
}

@Managed
interface HaxeLib
{
	void setVersion(String value);
	String getVersion();
}

