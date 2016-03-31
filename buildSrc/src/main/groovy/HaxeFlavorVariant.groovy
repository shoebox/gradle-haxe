import org.gradle.api.Project;
import org.gradle.api.Nullable;
import org.gradle.api.Named;

import java.io.Serializable;

public class HaxeFlavorVariant implements Serializable
{
	@Nullable Boolean debug;
	
	List<File> cp;
	List<String> flag;
	List<String> macro;
	String binaryFileName;
	String main;
	String name;
	String platformName;
	String output;
}
