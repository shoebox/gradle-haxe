package org.shoebox;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;

class TestFlavorParameters extends Specification
{
	@Rule
	final TemporaryFolder testProjectDir = new TemporaryFolder();

	File buildFile;
	File propertiesFile;
	List pluginClasspath;

	void setup()
	{
		buildFile = testProjectDir.newFile('build.gradle');

		propertiesFile = testProjectDir.newFile('gradle.properties')
        pluginClasspath = getClass().classLoader
        	.findResource('plugin-classpath.txt')
        	.readLines()
        	.collect { new File(it) };
	}

	def "Flavor without Main class should fail to compile"()
	{
		given:
		buildFile << """
			plugins {
                id 'org.shoebox.haxe'
            }

			import org.shoebox.haxe.HaxeFlavor;

			model {
				haxe {
					flavors {
						test1(HaxeFlavor) {}
					}
				}
			}
		"""

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withPluginClasspath(pluginClasspath)
			.withArguments('tasks')
			.buildAndFail();

		then:
		result.output.contains('The target : test1 has no defined target platform');
	}
}
