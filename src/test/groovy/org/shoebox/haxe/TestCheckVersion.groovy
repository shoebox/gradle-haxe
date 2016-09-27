package org.shoebox;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;

class TestCheckVersion extends Specification
{
	@Rule
	final TemporaryFolder testProjectDir = new TemporaryFolder();

	File buildFile;
	List pluginClasspath;

	void setup()
	{
		buildFile = testProjectDir.newFile('build.gradle');
	    pluginClasspath = getClass()
	    	.classLoader
	    	.findResource('plugin-classpath.txt')
	    	.readLines()
	    	.collect { new File(it) };
	}

	def "Contain task 'CheckHaxeVersion'"()
	{
		given:
		buildFile << """
			plugins {
                id 'org.shoebox.haxe'
            }

			model {
				haxe {

				}
			}
		"""

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('tasks')
			.withPluginClasspath(pluginClasspath)
			.build();

		then:
		result.output.contains('CheckHaxeVersion');
	}
}
