package org.haxe.gradle;

import javax.inject.Inject;
import org.gradle.api.*;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.model.*;
import org.gradle.platform.base.*;
import org.gradle.platform.base.DependencySpecContainer;
import org.haxe.gradle.flavor.*;

@Managed
interface HaxeModel
{
	void setBinFolder(String folder);
	String getBinFolder();

	void setFlavorDimensions(List<String> name);
	List<String> getFlavorDimensions();
}

public class HaxePlugin implements Plugin<Project>
{
	final Instantiator instantiator;

	@Inject public HaxePlugin(Instantiator instantiator)
	{
		this.instantiator = instantiator;
	}

	void apply(Project project)
	{
		project.extensions.create("flavors", FlavorContainer.class, instantiator);
	}

	@SuppressWarnings("UnusedDeclaration")
	static class Rules extends RuleSource
	{
		@ComponentType
	    void registerComponent(TypeBuilder<HaxeComponent> builder)
		{
			builder.defaultImplementation(HaxeComponent);
		}

		@ComponentType
		void registerHaxeLanguage(TypeBuilder<HaxeSourceSet> builder){}

		@Model("haxe")
		void haxe(HaxeModel haxe)
		{

		}

		@Model
		FlavorContainer flavors(ExtensionContainer extensionContainer)
		{
			return extensionContainer.getByType(FlavorContainer.class);
		}

		@Mutate void createCompileTasks(ModelMap<Task> tasks, 
			HaxeModel haxe, 
			FlavorContainer flavorContainer)
		{
			List<IFlavor> combos = FlavorCombiner.combine(haxe.flavorDimensions, flavorContainer);
			for (combo in combos)
			{
				final List comboClone = combo.clone();
				final String name = "haxe" + combo.collect { it.name.capitalize() }.join();
				tasks.create(name, HaxeCompileTask.class)
				{
					task ->
					task.flavors = comboClone;
				}
			}
		}
	}
}
