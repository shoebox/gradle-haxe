import org.gradle.api.Project;
import org.gradle.api.Nullable;
import org.gradle.api.Named;

public class HaxeFlavorVariant implements HaxeCompilerParameters, Serializable, Named
{
	@Nullable Boolean debug;
	File output;
	List<File> cp;
	List<String> flag;
	List<String> macro;
	String binaryFileName;
	String main;
	String name;
	String platformName;

	public HaxeFlavorVariant()
	{

	}

	public void setBinaryFileName(String binaryFileName)
	{
		this.binaryFileName = binaryFileName;
	}

	public String getBinaryFileName()
	{
		return binaryFileName;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setCp(List<File> list)
	{
		this.cp = list;
	}
	
	public List<File> getCp()
	{
		return cp;
	}

	public void setFlag(List<String> flag)
	{
		this.flag = flag;
	}

	public List<String> getFlag()
	{
		return flag;
	}

	public void setMacro(List<String> macros)
	{
		this.macro = macro;
	}

	public List<String> getMacro()
	{
		return this.macro;
	}
	
	void setDebug(Boolean debug)
	{
		this.debug = debug;
	}

	Boolean getDebug()
	{
		return debug;
	}

	void setMain(String main)
	{
		this.main = main;
	}

	String getMain()
	{
		return main;
	}

	void setOutput(File output)
	{
		this.output = output;
	}

	File getOutput()
	{
		return output;
	}

	void setPlatformName(String name)
	{
		this.platformName = name;	
	}

	String getPlatformName()
	{
		return platformName;
	}

	public static HaxeFlavorVariant create(Project project,
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

		String debugMode = result.debug ? "debug" : "release";
		result.output = project.file(model.binFolder + "/$debugMode/$variantName/");
		/*
		println result.cp;
		println result.flag;
		println result.macro;
		println result.debug;
		println result.main;
		println result.output;
		*/

		return result;
	}
}
