package org.shoebox.haxe;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;
import static org.gradle.testkit.runner.TaskOutcome.*

class TestFlavorParameters extends Specification
{
	@Rule
	final static TemporaryFolder testProjectDir = new TemporaryFolder();

	File buildFile;
	File propertiesFile;
	List pluginClasspath;

	static String Header = """
		plugins {
            id 'org.shoebox.haxe'
        }

		import org.shoebox.haxe.HaxePlugin;
		import org.shoebox.haxe.HaxeFlavor;
		import org.shoebox.haxe.HaxeSourceSet;
		import org.shoebox.haxe.HaxeBuildType;

		model {
			haxe {
				defaultConfig {
					main "Main"
					platform "js"
				}

				buildTypes {
					debug(HaxeBuildType) {}
				}
		""";

	static String CaseValidSourceSet;
	static String CaseInvalidSourceSet;
	static String CaseVariantWithoutMain = """
			plugins { id 'org.shoebox.haxe' }

			import org.shoebox.haxe.HaxeFlavor;

			model {
				haxe {
					flavors {
						testValid(HaxeFlavor) {
							main = "Main"
							platform "js"
						}
						testInvalid(HaxeFlavor) {
							platform "js"
						}
					}
				}
			}
		"""

	void setup()
	{
		testProjectDir.create();
		String pathRoot = testProjectDir.getRoot();

		CaseValidSourceSet = """${Header}
				flavors
				{
					test(HaxeFlavor)
					{
						sources
						{
							haxe(HaxeSourceSet)
							{
								source.srcDir file("${pathRoot}")
							}
						}
					}
				}
			}
		}
		""";

		CaseInvalidSourceSet = """${Header}
				flavors
				{
					test(HaxeFlavor)
					{
						sources
						{
							haxe(HaxeSourceSet)
							{
								source.srcDir file("/toto/tata/42")
							}
						}
					}
				}
			}
		}
		""";


		buildFile = testProjectDir.newFile('build.gradle');

		propertiesFile = testProjectDir.newFile('gradle.properties')
		pluginClasspath = getClass().classLoader
        	.findResource('plugin-classpath.txt')
        	.readLines()
        	.collect { new File(it) };
	}

	def "Flavor without target platform should fail"()
	{
		given:
		buildFile << """
			plugins { id 'org.shoebox.haxe' }

			import org.shoebox.haxe.HaxeFlavor;

			model {
				haxe {
					flavors {
						testInvalid(HaxeFlavor) {}
						testValid(HaxeFlavor) {
							platform "js"
						}
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
		result.getOutput().contains('The target : testInvalid has no defined target platform');
		!result.getOutput().contains('The target : testValid has no defined target platform');
	}

	def "Variant without Main class defined should fail to compile"()
	{
		given:
		buildFile << CaseVariantWithoutMain

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withPluginClasspath(pluginClasspath)
			.withArguments('tasks')
			.buildAndFail();

		then:
		!result.getOutput().contains('The target : testValid has no defined main class');
		result.getOutput().contains('The target : testInvalid has no defined main class');
	}

	def "Flavor with invalid sourcePath define should fail"()
	{
		setup:
			GradleRunner runner = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withPluginClasspath(pluginClasspath);

		when:
			buildFile << content;
			def run = runner.withArguments("haxeTestDebug");
			def runResult = result ? run.build() : run.buildAndFail();
		
		then:
			if (msg != null)
				runResult.getOutput().contains(msg);

		where:
			content					|| result 	|| msg
			CaseValidSourceSet 		|| true   	|| null
			CaseInvalidSourceSet 	|| false   	|| "Source path : '/toto/tata/42' on flavor:test is invalid"
	}
}
