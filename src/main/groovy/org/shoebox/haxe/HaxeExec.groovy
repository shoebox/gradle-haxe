package org.shoebox.haxe

import org.gradle.api.Project
import org.gradle.process.ExecSpec

class HaxeExec {
    public static run(Project project, List<Object> arguments) {
        run(project, arguments, null);
    }

    public static run(Project project, List<Object> arguments, OutputStream errorStream) {
        project.exec({ ExecSpec spec ->
            spec.setExecutable("haxe")
            spec.args(arguments)
            spec.setIgnoreExitValue(true)

            if (errorStream)
                spec.setErrorOutput(errorStream)
        });
    }
}
