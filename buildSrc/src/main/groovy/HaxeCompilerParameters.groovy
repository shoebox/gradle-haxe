import org.gradle.api.Nullable;

interface HaxeCompilerParameters
{
	void setCp(List<File> list);
	List<File> getCp();

	@Nullable
	void setDebug(Boolean debug);
	Boolean getDebug();

	void setMain(String main);
	String getMain();

	void setMacro(List<String> macro);
	List<String> getMacro();

	void setFlag(List<String> flag);
	List<String> getFlag();
}
