import HaxePlugin
import org.gradle.internal.HasInternalProtocol
import org.gradle.language.base.DependentSourceSet
import org.gradle.language.base.FunctionalSourceSet
import org.gradle.language.base.LanguageSourceSet
import org.gradle.language.base.internal.LanguageSourceSetInternal
import org.gradle.language.base.sources.BaseLanguageSourceSet
import org.gradle.model.Managed
import org.gradle.platform.base.DependencySpecContainer
import org.gradle.platform.base.TransformationFileType
import org.gradle.platform.base.internal.DefaultDependencySpecContainer
import org.gradle.platform.base.internal.PlatformRequirement

@HasInternalProtocol
public interface HaxeSourceSet extends LanguageSourceSet, DependentSourceSet, TransformationFileType
{
	HaxePlatform getTargetPlatform();
	void platform(Object platformRequirements);
}

@Managed
public interface HaxeSourceSetInternal extends HaxeSourceSet, LanguageSourceSetInternal
{
	void setTargetPlatform(HaxePlatform platform);
	PlatformRequirement getPlatformRequirement();
}

public class DefaultHaxeSourceSet extends BaseLanguageSourceSet implements HaxeSourceSetInternal
{
	HaxePlatform platform;
	PlatformRequirement platformRequirement;

	final DefaultDependencySpecContainer dependencies = new DefaultDependencySpecContainer();

	@Override
	public void platform(Object platformRequirements)
	{
		PlatformRequirement requirement = HaxePlatformNotationParser.getInstance().parseNotation(platformRequirements);

		if(requirement != null)
			this.platformRequirement = requirement;
	}

	PlatformRequirement getPlatformRequirement()
	{
		return platformRequirement
	}

	void setTargetPlatform(HaxePlatform platform)
	{
		this.platform = platform;
	}

	HaxePlatform getTargetPlatform()
	{
		return this.platform;
	}

	@Override
	public DependencySpecContainer getDependencies()
	{
		return dependencies;
	}
}
