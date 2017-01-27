package org.shoebox.haxe

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

public class HaxeCheckVersion extends DefaultTask {
    @Input
    @Optional
    String requiredVersion;

    @OutputDirectory
    File outputDir;

    public HaxeCheckVersion() {
        super();
    }

    @TaskAction
    public void run() {
        if (requiredVersion == null) {
            return;
        }

        Logger.info("Checking required haxe version");
        String version = getLocalVersion();
        if (version != requiredVersion) {
            Logger.error("The current version installed is invalid");
            Logger.error("Download and install the version : '${requiredVersion}'");
        }
    }

    String getLocalVersion() {
        String result = null;
        Properties prop = getProperties();
        if (prop != null) {
            result = prop.getProperty("version", null);
        }

        return result;
    }

    Properties getProperties() {
        new File(".haxe").mkdirs();

        Properties result = null;
        File file = new File('.haxe/gradle.properties');
        if (file.exists()) {
            result = new Properties();
            result.load(file.newDataInputStream());
        } else {
            file.createNewFile();
        }

        return result;
    }
}
