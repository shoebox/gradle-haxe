package org.shoebox;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;

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

            import org.shoebox.haxe.HaxePlugin;
			import org.shoebox.haxe.HaxeFlavor;
			import org.shoebox.haxe.HaxeSourceSet;
			import org.shoebox.haxe.HaxeBuildType;

			model
			{
				haxe
				{
					defaultConfig
					{
						main = "Main"
						platform "js"
					}

					flavors
					{
						test1(HaxeFlavor) {}
						test2(HaxeFlavor) {}
					}

					buildTypes
					{
						debug(HaxeBuildType) {}
						release(HaxeBuildType) {}
					}
				}
			}
		"""

		when:
		BuildResult result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('haxeTest1Debug')
			.withPluginClasspath(pluginClasspath)
			.build();

		then:
		println project;
		println project.getTasks();
	}
}
