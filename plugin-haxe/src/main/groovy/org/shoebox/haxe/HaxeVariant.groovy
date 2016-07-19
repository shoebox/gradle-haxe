package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.model.*;
import org.shoebox.haxe.HaxeDefaultConfig;

import java.io.Serializable;
import java.security.MessageDigest;

public class HaxeVariant implements Serializable
{
	Boolean debug;
	Boolean verbose;
	File output = null;
	List<BuildType> buildTypes;
	List<File> src = [];
	List<String> compilerFlag = [];
	List<String> components;
	List<String> flag = [];
	List<String> haxelib = [];
	List<String> macro = [];
	List<String> resource = [];
	String main;
	String name;
	String outputFileName;
	String platform;
	String target;

	public HaxeVariant(HaxeDefaultConfig value)
	{
		if (value != null)
		{
			copyProperties(value, this);
		}
	}

	void copyProperties(source, target)
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

	public final String getResourceTaskName()
	{
		return "haxeRes" + name.capitalize();
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
