import groovy.util.GroovyCollections;
import org.gradle.api.*;
import org.gradle.internal.HasInternalProtocol;
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.*;
import org.gradle.model.*;
import org.gradle.platform.base.*;

class HaxePlugin extends RuleSource
{
	@Mutate 
	void createCompileTasks(ModelMap<Task> tasks, Haxe haxe)
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
					task.flavors = comboClone;
					task.haxe = haxe;
					task.platform = platformClone;
					task.outputDirectory = project.file(task.outputPath);
				}
			}
		}
	}

	@Model
	void haxe(Haxe haxe) {}
	
	@Model
	void platforms(@Path("haxe.platforms") HaxePlatform p) {}
}

@Managed 
interface Haxe extends HaxePlatform
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

@Managed
interface HaxeFlavor extends Named
{
	void setCp(List<String> list);
	List<String> getCp();

	void setFlag(List<String> flags);
	List<String> getFlag();

	@Nullable
	void setDebug(Boolean debug);
	Boolean getDebug();

	void setMain(String main);
	String getMain();

	void setFileName(String name);
	String getFileName();

	void setDimension(String dimension);
	String getDimension();

	void setMacro(List<String> macro);
	List<String> getMacro();
}
