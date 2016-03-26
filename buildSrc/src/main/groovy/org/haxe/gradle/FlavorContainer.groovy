package org.haxe.gradle;

import org.gradle.api.Incubating;
import org.gradle.api.Named;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;
import org.haxe.gradle.flavor.IFlavorContainer;


public class FlavorContainer 
	extends AbstractNamedDomainObjectContainer<HaxeFlavor> 
	implements IFlavorContainer
{

	public FlavorContainer(Instantiator instantiator)
	{
		super(HaxeFlavor.class, instantiator);
	}

	@Override HaxeFlavor doCreate(String name)
	{
		return getInstantiator().newInstance(HaxeFlavor.class, name);
	}
}
