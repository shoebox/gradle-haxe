import org.gradle.api.Named;
import org.gradle.model.Managed;
import org.gradle.internal.serialize.*;

@Managed
interface HaxeFlavor extends Named, HaxeCompilerParameters
{
	void setBinaryFileName(String name);
	String getBinaryFileName();

	void setDimension(String dimension);
	String getDimension();
}
