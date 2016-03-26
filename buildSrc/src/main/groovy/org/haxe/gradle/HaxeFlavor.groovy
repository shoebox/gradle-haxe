package org.haxe.gradle;

import org.haxe.gradle.flavor.Flavor;

public class HaxeFlavor extends Flavor
{
	boolean debug;
	List<String> cp = [];
	String[] exclude = [];
	String[] flag = [];
	String[] include = [];
	String[] macro;

	public HaxeFlavor(String name)
	{
		super(name);
	}

	public void cp(String[] value)
	{
		this.cp.addAll(value)
	}

	public String[] getCp()
	{
		return this.cp;
	}

	public void flag(String[] value)
	{
		this.flag.addAll(value);
	}

	public String[] getFlag()
	{
		return this.flag;
	}

	public void macro(String[] value)
	{
		this.macro.addAll(value);
	}

	public String[] macro()
	{
		return macro;
	}

	public void debug(boolean debug)
	{
		this.debug = debug;
	}

	public boolean debug()
	{
		return debug;
	}

	public void exclude(String[] value)
	{
		this.exclude.addAll(value);
	}

	public String[] exclude()
	{
		return exclude;
	}

	public void include(String[] value)
	{
		this.include.addAll(value);
	}

	public String[] include()
	{
		return include;
	}

	@Override public String toString()
	{
		return "HaxeFlavor : " + name;
	}

	public void haxelib(String name, String version)
	{
		println "haxelib '$name' : '$version'";
	}
}
