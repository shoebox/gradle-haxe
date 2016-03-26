package org.haxe.gradle.flavor;

import org.gradle.api.Incubating;
import org.gradle.api.Named;

@Incubating
public interface IFlavor extends Named
{
	
}

public class Flavor implements IFlavor
{
	String dimension;
	final String name;

	public Flavor(String name)
	{
		this.name = name;
	}

	@Override public String getName()
	{
		return name;
	}

	@Override public String toString()
	{
		return "Flavor : " + name;
	}
}
