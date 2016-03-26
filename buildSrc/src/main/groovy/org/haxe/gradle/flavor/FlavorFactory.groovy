package org.haxe.gradle.flavor;

import org.gradle.api.*;
import org.gradle.internal.reflect.Instantiator;

public class FlavorFactory implements NamedDomainObjectFactory<Flavor>, Serializable
{
	Project project;

	public FlavorFactory(Project project)
	{
		this.project = project;
	}

	@Override public Flavor create(String name)
	{
		return new Flavor(name);
	}
}
