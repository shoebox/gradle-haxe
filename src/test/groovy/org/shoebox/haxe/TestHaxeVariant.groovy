package org.shoebox.haxe;

import org.shoebox.haxe.HaxeCompileTask;
import org.shoebox.haxe.HaxeDefaultConfig;
import org.shoebox.haxe.HaxeVariant;
import spock.lang.Specification;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

class TestHaxeVariant extends Specification
{
	@Rule final static TemporaryFolder testProjectDir1 = new TemporaryFolder();
	@Rule final static TemporaryFolder testProjectDir2 = new TemporaryFolder();
	@Rule static File dir1;
	@Rule static File dir2;
	@Rule static File testFile1;
	@Rule static File testFile2;
	@Rule static String path1;
	@Rule static String path2;

	void setup()
	{
		testProjectDir1.create();
		testProjectDir2.create();
		testFile1 = new File("/test1");
		testFile2 = new File("/test2");
		dir1 = new File(testProjectDir1.getRoot(), "/test1");
		dir2 = new File(testProjectDir2.getRoot(), "/test2");
		path1 = dir1.getAbsolutePath();
		path2 = dir2.getAbsolutePath();
	}

	def "Hash should be unique"()
	{
		setup:

		when:
			HaxeVariant v1 = new HaxeVariant();
			HaxeVariant v2 = new HaxeVariant();

		then:
			v1.haxelib << haxelib1;
			v2.haxelib << haxelib2;
			(v1.hash() == v2.hash()) == result;

		where:
			haxelib1					|| haxelib2						|| result 
			["haxelib1", "haxelib2"] 	|| ["haxelib1", "haxelib2"] 	|| true
			["haxelib1", "haxelib2"] 	|| ["haxelib3"] 				|| false
			[] 							|| ["haxelib3"] 				|| false
			["haxelib3"] 				|| ["haxelib3"] 				|| true
	}

	def "Parameters should be applied from the default configuration"()
	{
		setup:
			HaxeDefaultConfig cfg1 = Mock(HaxeDefaultConfig);
			HaxeDefaultConfig cfg2 = Mock(HaxeDefaultConfig);

		when:
			cfg1.haxelib >> haxelib1
			cfg2.haxelib >> haxelib2

			HaxeVariant v1 = new HaxeVariant(cfg1);
			HaxeVariant v2 = new HaxeVariant(cfg2);
		
		then:
			(v1.haxelib == v2.haxelib) == result;

		where:
			haxelib1					|| haxelib2						|| result
			["haxelib1", "haxelib2"] 	|| ["haxelib1", "haxelib2"] 	|| true
			["haxelib1", "haxelib2"] 	|| ["haxelib1", "haxelib3"] 	|| false
	}

	def "Arguments haxelib '-lib' should not be duplicated \
		and respect the injection order"()
	{
		setup:
			HaxeVariant variant = new HaxeVariant();
			variant.platform = "js";
			variant.main = "Main";
			variant.outputFileName = "test.js";

		when:
			variant.haxelib = haxelib;

		then:
			variant.haxelibArgs.join(" ").contains(result);

		where:
			haxelib 								|| result
			["haxelib1"]							|| "-lib haxelib1"
			["haxelib1", "haxelib1"]				|| "-lib haxelib1"
			["haxelib1", "haxelib1", "haxelib1"]	|| "-lib haxelib1"
			["haxelib1", "haxelib2"]				|| "-lib haxelib1 -lib haxelib2"
			["haxelib3", "haxelib1", "haxelib2",]	|| "-lib haxelib3 -lib haxelib1 -lib haxelib2"
	}

	def "Arguments source folder '-cp' should not be duplicated \
		and respect the injection order"()
	{
		setup:
			HaxeVariant variant = new HaxeVariant();
			variant.platform = "js";
			variant.main = "Main";
			variant.outputFileName = "test.js";

		when:
			sources.each
			{
				variant.src << it;
			}

		then:
			variant.getCpArgs().join(" ").contains(result);

		where:
			sources 		|| result
			[dir1]			|| "-cp " + path1;
			[dir1, dir1]	|| "-cp " + path1
			[dir2, dir2]	|| "-cp " + path2
			[dir2, dir1]	|| "-cp " + path2 + " -cp " + path1
	}

	def "Arguments 'resources' should not be duplicated \
		and respect the injection order"()
	{
		setup:
			HaxeVariant variant = new HaxeVariant();
			variant.platform = "js";
			variant.main = "Main";
			variant.outputFileName = "test.js";

		when:
			variant.resource = res;

		then:
			variant.resArgs.join(" ").contains(result);

		where:
			res 				|| result
			["test1"]			|| "-resource test1";
			["test1", "test1"]	|| "-resource test1";
			["test2", "test1"]	|| "-resource test2 -resource test1";
	}

	def "Arguments 'macro' should not be duplicated \
		and respect the injection order"()
	{
		setup:
			HaxeVariant variant = new HaxeVariant();
			variant.platform = "js";
			variant.main = "Main";
			variant.outputFileName = "test.js";

		when:
			variant.macro = macros;

		then:
			variant.macroArgs.join(" ").contains(result);

		where:
			macros 				|| result
			["test1"]			|| "--macro test1";
			["test1", "test1"]	|| "--macro test1";
			["test2", "test1"]	|| "--macro test2 --macro test1";
	}

	def "Arguments flags '-D' should not be duplicated \
		and respect the injection order"()
	{
		setup:
			HaxeVariant variant = new HaxeVariant();
			variant.platform = "js";
			variant.main = "Main";
			variant.outputFileName = "test.js";

		when:
			variant.flag = flag;

		then:
			variant.flagArgs.join(" ").contains(result);

		where:
			flag 				|| result
			["test1"]			|| "-D test1";
			["test1", "test1"]	|| "-D test1";
			["test2", "test1"]	|| "-D test2 -D test1";
	}

	def "Test computed arguments"()
	{
		setup:
			HaxeVariant variant = new HaxeVariant();
			variant.platform = "js";
			variant.main = "Main";
			variant.outputDirectoryName = "test";
			variant.outputFileName = "test.js";

		when:
			variant.flag = flag;
			variant.haxelib = haxelib;
			variant.src = sources;

		then:
			variant.computeArguments(new File("test/")).join(" ") == result;

		where:
			flag 						|| haxelib 			|| sources 					|| result			
			["haxelib1"]				|| ["lib1", "lib2"]	|| [testFile1]				|| "-js test/test.js -main Main -cp ${testFile1} -lib lib1 -lib lib2 -D haxelib1"
			["haxelib1"]				|| ["lib2", "lib1"]	|| [testFile1, testFile2]	|| "-js test/test.js -main Main -cp ${testFile1} -cp ${testFile2} -lib lib2 -lib lib1 -D haxelib1"
			["haxelib1", "haxelib1"]	|| ["lib2", "lib2"]	|| [testFile1, testFile2]	|| "-js test/test.js -main Main -cp ${testFile1} -cp ${testFile2} -lib lib2 -D haxelib1"
			["haxelib2", "haxelib1"]	|| ["lib1"]			|| [testFile1, testFile2]	|| "-js test/test.js -main Main -cp ${testFile1} -cp ${testFile2} -lib lib1 -D haxelib2 -D haxelib1"
			["haxelib1", "haxelib2"]	|| ["lib2"]			|| [testFile1]				|| "-js test/test.js -main Main -cp ${testFile1} -lib lib2 -D haxelib1 -D haxelib2"
	}
}

