package org.shoebox.haxe

import org.gradle.api.tasks.*
import org.haxe.gradle.flavor.*

@ParallelizableTask
public class HaxeCompileTask extends SourceTask {
    @Optional
    @OutputFile
    File output;

    @Optional
    @OutputDirectory
    File outputDirectory

    @Input
    String configurationHash

    HaxeVariant variant

    public HaxeCompileTask() {
        super();
        this.include("**/*.hx")

        // Cf https://github.com/gradle/gradle/pull/668
        outputs.upToDateWhen { false }
    }

    @TaskAction
    public void run() {
        compile();
    }

    void compile() {
        OutputStream errorStream = new ByteArrayOutputStream()
        List<String> args = prepareExec()
        HaxeExec.run(getProject(), args, errorStream)

        if (errorStream.size() > 0) {
            throw new Error("Compilation Error : " + errorStream.toString())
        }
    }

    List<String> prepareExec() {
        File output = new File(outputDirectory, variant.outputFileName)
        List<String> args = ["-" + variant.platform, output]

        args.addAll(["-main", variant.main])
        variant.src.unique().each { args.addAll(["-cp", it.absolutePath]) }
        variant.resource.each { args.addAll(["-resource", it]) }
        variant.macro.each { args.addAll(["--macro", it]) }
        variant.haxelib.each { args.addAll(["-lib", it]) }
        variant.flag.each { args.addAll(["-D", it]) }
        variant.compilerFlag.each { args.push(it) }

        if (variant.debug)
            args.push("-debug")

        if (variant.verbose)
            args.push("-v")

        return args
    }
}
