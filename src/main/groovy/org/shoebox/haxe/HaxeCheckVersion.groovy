package org.shoebox.haxe

import org.gradle.api.DefaultTask
import org.gradle.api.Nullable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecException

public class HaxeCheckVersion extends DefaultTask {
    @Input
    @Optional
    String requiredVersion

    public HaxeCheckVersion() {
        super()
    }

    @TaskAction
    public void run() {
        Logger.info("Checking required haxe version")
        String version = null
        try {
            version = getLocalVersion()
        } catch (Exception exception) {
            Logger.error("Exception while trying to retrieve the current haxe version: '${exception}'" )
            version = null;
        }

        if (version == null || !version.equals(requiredVersion)) {
            throw new Error("The current version installed is invalid. " +
                    "\nDownload and install the Haxe compiler version : '${requiredVersion}'")
        }
    }

    @Nullable
    String getLocalVersion() throws ExecException {
        OutputStream errorStream = new ByteArrayOutputStream()
        HaxeExec.run(project, ["-version"], errorStream)
        return errorStream.toString().trim()
    }
}
