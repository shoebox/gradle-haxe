package org.shoebox.haxe;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;
import static org.gradle.testkit.runner.TaskOutcome.*

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

	def "Should always report success if no required version is specified"()
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
			.withArguments('CheckHaxeVersion')
			.withPluginClasspath(pluginClasspath)
			.build();

		then:
		def outcome = result.task(":CheckHaxeVersion").getOutcome();
		assert (outcome == SUCCESS || outcome == UP_TO_DATE);
	}

	def "Should always report success if no required version is specified"()
	{
		given:
		buildFile << """
			plugins {
                id 'org.shoebox.haxe'
            }

			model {
				haxe {
					version "100.0.0"
				}
			}
		"""

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('CheckHaxeVersion')
			.withPluginClasspath(pluginClasspath)
			.buildAndFail();

		then:
		def outcome = result.task(":CheckHaxeVersion").getOutcome();
		result.output.contains("The project require Haxe version : '100.0.0'");
		assert outcome == FAILED;
	}
}
