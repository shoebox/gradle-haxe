package org.shoebox.haxe;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;

class TestFlavor extends Specification
{
	@Rule
	final TemporaryFolder testProjectDir = new TemporaryFolder();

	File buildFile;
	List pluginClasspath;

	void setup()
	{
		buildFile = testProjectDir.newFile('build.gradle');
	    pluginClasspath = getClass().classLoader.findResource('plugin-classpath.txt').readLines().collect { new File(it) }
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

	def "Flavors without dimensions"()
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
						all {
							set ->
							set.main = "Main";
							set.platform "js"
						}

						test1(HaxeFlavor) {}
						test2(HaxeFlavor) {}
					}
				}
			}
		"""

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withPluginClasspath(pluginClasspath)
			.withArguments('tasks')
			.build();

		then:
		result.output.contains('haxeTest1');
		result.output.contains('haxeTest2');
	}

	def "Flavors with dimensions"()
	{

		given:
		buildFile << """
			plugins {
                id 'org.shoebox.haxe'
            }

			import org.shoebox.haxe.HaxeFlavor;

			model {
				haxe {
					dimensions = ["test", "hello"]

					flavors {
						all {
							set ->
							set.main = "Main";
							set.platform "js"
						}

						test1(HaxeFlavor) {
							dimension "test"
						}

						test2(HaxeFlavor) {
							dimension "test"
						}

						hello1(HaxeFlavor) {
							dimension "hello"
						}

						hello2(HaxeFlavor) {
							dimension "hello"
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
			.build();

		then:
		result.output.contains('haxeTest1Hello1');
		result.output.contains('haxeTest2Hello2');
	}

	def "Flavor with undefined dimension should throw error"()
	{

		given:
		buildFile << """
			plugins {
                id 'org.shoebox.haxe'
            }

			import org.shoebox.haxe.HaxeFlavor;

			model {
				haxe {
					dimensions = ["test"]

					flavors {
						all {
							set ->
							set.main = "Main";
							set.platform "js"
						}

						test1(HaxeFlavor) {
							dimension "test"
						}

						hello1(HaxeFlavor) {
							dimension "hello"
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
		result.output.contains("contains a unknow flavor dimension : 'hello'");
	}
}
