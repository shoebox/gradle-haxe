package org.shoebox;

import spock.lang.Specification;
import org.gradle.api.Project;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testfixtures.ProjectBuilder;

import org.gradle.api.Project;
import org.junit.Test;

import org.shoebox.haxe.HaxeCompileTask;

class TestDefaultConfig extends Specification
{
	@Rule
	final TemporaryFolder testProjectDir = new TemporaryFolder();

	File buildFile;
	Project project;
	List pluginClasspath;

	void setup()
	{
		buildFile = testProjectDir.newFile('build.gradle');

		project = ProjectBuilder.builder().build();
		project.buildDir = buildFile.directory;

	    pluginClasspath = getClass().classLoader.findResource('plugin-classpath.txt').readLines().collect { new File(it) }
	}

	def "Check than the defaultConfig is applied to the flavors and can be properly overrided"()
	{
		given:
		buildFile << """
			plugins
			{
                id 'org.shoebox.haxe'
            }

            import org.shoebox.haxe.HaxeFlavor;

			model
			{
				haxe
				{
					defaultConfig
					{
						main = "Main"
					}

					flavors
					{
						test1(HaxeFlavor)
						{
							main = "MainTest1"
							platform "js"
						}

						test2(HaxeFlavor)
						{
							platform "js"
						}
					}
				}
			}
		"""

		when:
		BuildResult result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('haxeTest1')
			.withPluginClasspath(pluginClasspath)
			.build();

		then:

		println project;
		println project.getTasks();

		println "t ::: " + result.task(":haxeTest1");
	}
}
