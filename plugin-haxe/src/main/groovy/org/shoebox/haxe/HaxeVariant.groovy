package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.model.*;

import java.io.Serializable;
import java.security.MessageDigest;

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
