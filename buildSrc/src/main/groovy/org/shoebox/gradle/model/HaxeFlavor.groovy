package org.shoebox.gradle.model;

import org.gradle.model.*;
import org.gradle.api.Named;

@Managed
interface HaxeFlavor
{
	void setDimension(String dimension);
	String getDimension();
}

/*
public class Flavor implements Named
{
	String dimension;
	final String name;

	public Flavor()
	{
		
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
*/
