package org.shoebox;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import spock.lang.Specification;
import static org.gradle.testkit.runner.TaskOutcome.*

class TestBuildType extends Specification
{
	@Rule
	final TemporaryFolder testProjectDir = new TemporaryFolder();

	final static String BUILD_FILE_CONTENT = """
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
					main = "Main"
					platform "js"
				}

				flavors {
					mobile(HaxeFlavor) {}
					tablet(HaxeFlavor) {}
				}

				buildTypes {
					debug(HaxeBuildType) {}
					release(HaxeBuildType) {}
				}
			}
		}
	""";
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

	def "Cannot run a build without specifiy a build type"()
	{
		given:
		buildFile << BUILD_FILE_CONTENT;

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('haxeMobile')
			.withPluginClasspath(pluginClasspath)
			.buildAndFail();

		then:
		assert result.task(":haxeMobile").getOutcome() == FAILED;
		assert result.output.contains('java.lang.Exception: No build type specified');
	}

	def "But can run a sub build type successfully"()
	{
		given:
		buildFile << BUILD_FILE_CONTENT;

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('haxeMobileDebug')
			.withPluginClasspath(pluginClasspath)
			.build();

		then:
		def outcome = result.task(":haxeMobileDebug").getOutcome();
		assert (outcome == SUCCESS || outcome == UP_TO_DATE);
	}
}
