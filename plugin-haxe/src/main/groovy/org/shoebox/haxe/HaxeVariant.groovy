package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.model.*;

import java.io.Serializable;

public class HaxeVariant implements Serializable
{
	Boolean debug;
	Boolean verbose;
	File output = null;
	List<String> components;
	List<File> src = [];
	List<String> resource = [];
	List<String> compilerFlag = [];
	List<String> flag = [];
	List<String> haxelib = [];
	List<String> macro = [];
	String main;
	String name;
	String outputFileName;
	String platform;
	String target;

	public final String getResourceTaskName()
	{
		return "res" + platform.capitalize() + name.capitalize();
	}

	public final File getOutputPath(File buildDir)
	{
		String path = (debug ? "debug/" : "release/") + name;
		return new File(buildDir, path);
	}

	public final String getCompileTaskName()
	{
		return "haxe" + name.capitalize();
	}
}
