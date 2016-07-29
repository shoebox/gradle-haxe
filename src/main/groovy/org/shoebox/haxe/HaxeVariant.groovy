package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.model.*;
import org.shoebox.haxe.*;

import java.io.Serializable;
import java.lang.Cloneable;
import java.security.MessageDigest;

public class HaxeVariant implements Cloneable, Serializable
{
	Boolean debug;
	Boolean verbose;
	File output = null;
	List<File> src = [];
	List<String> compilerFlag = [];
	List<String> components;
	List<String> flag = [];
	List<String> haxelib = [];
	List<String> macro = [];
	List<String> resource = [];
	String group;
	String main;
	String name;
	String outputDirectoryName;
	String outputFileName;
	String platform;
	String target;

	public HaxeVariant(HaxeDefaultConfig value)
	{
		if (value != null)
		{
			Util.copyProperties(value, this);
		}
	}

	public final String getResourceTaskName()
	{
		return "haxeRes" + name.capitalize();
	}

	public final String getResourceTaskName(HaxeBuildType bt)
	{
		return "haxeRes" + name.capitalize() + bt.name.capitalize();
	}

	public final File getOutputPath(File buildDir)
	{
		String folderName = outputDirectoryName == null ? name : outputDirectoryName;
		String path = (debug ? "debug/" : "release/") + folderName;
		return new File(buildDir, path);
	}

	public final String getCompileTaskName()
	{
		return "haxe" + name.capitalize();
	}

	public final String getCompileTaskName(HaxeBuildType bt)
	{
		return getCompileTaskName() + bt.name.capitalize();
	}

	String md5(String s)
	{
		MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
	}

	public String hash()
	{
		String result = "";
		["debug", "verbose"].each
		{
			it ->
			if (this."$it" != null)
			{
				result += this."$it";
			}
		}

		["components", "src", "resource", "compilerFlag", "flag", "haxelib", 
			"macro"].each
		{
			it ->
			if (this."$it" != null)
				result += this."$it".toListString();
		}

		["main","name","outputFileName","platform","target"].each
		{
			it ->
			if (this."$it" != null)
				result += this."$it";
		}

		return md5(result);
	}
}
