package org.shoebox.haxe;

public class Util
{
	public static void copyProperties(source, target)
	{
		source.properties.each
		{
			key, value ->
			if (value != null 
				&& target.hasProperty(key) 
				&& !(key in ['class', 'metaClass'])) 
			{
				target[key] = value
			}
		}
	}

	public static void copyDefault(Object from, Object to)
	{
		["debug",
			"group",
			"main",
			"output",
			"outputDirectoryName",
			"outputFileName",
			"platform",
			"target",
			"verbose"].each
		{
			if (from."$it" != null)
			{
				to."$it" = from."$it";
			}
		};

		["compilerFlag","flag","haxelib","macro","resource"].each
		{
			if (from."$it" != null)
			{
				to."$it" += from."$it";
			}
		};
	}
}
