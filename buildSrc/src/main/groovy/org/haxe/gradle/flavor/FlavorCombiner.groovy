package org.haxe.gradle.flavor;

import groovy.util.GroovyCollections;

public class FlavorCombiner
{
	public static List combine(List dimensions, IFlavorContainer flavors)
	{
		List<IFlavor> dimensionFlavors;
		Map<String, Flavor[]> flavorsByDimension = new HashMap<String, IFlavor[]>();
		for (flavor in flavors)
		{
			//
			String flavorDimension = flavor.getDimension();	
			if (flavorDimension == null)
			{
				throw new RuntimeException("Flavor '$flavor' has no flavor dimension.");
			}
			else if (!dimensions.contains(flavorDimension))
			{
				throw new RuntimeException("Flavor '$flavor' has unknow dimension '$flavorDimension'");
			}


			dimensionFlavors = flavorsByDimension.get(flavorDimension);
			if (dimensionFlavors == null)
				dimensionFlavors = [];
			dimensionFlavors.add(flavor);
			flavorsByDimension.put(flavorDimension, dimensionFlavors);
		}

		Flavor[] flavorList;
		List groups = [];
		List names;
		for (dimension in dimensions)
		{
			names = flavorsByDimension.get(dimension).findAll();
			groups.push(names);
		}

		return GroovyCollections.combinations(groups);
	}
}
