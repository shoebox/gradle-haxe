package org.shoebox.haxe

import org.gradle.api.*
import org.gradle.api.task.*
import org.gradle.language.base.FunctionalSourceSet
import org.gradle.language.base.LanguageSourceSet
import org.gradle.model.*

class HaxePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        org.shoebox.haxe.Logger.setup(project);
        project.tasks.create("Haxe compile")
        project.plugins.apply(org.gradle.language.base.plugins.LanguageBasePlugin);
        project.plugins.apply(HaxePluginRuleSource);
    }
}

class HaxePluginRuleSource extends RuleSource {
    final static String CheckVersionTaskName = "CheckHaxeVersion";

    @Model
    void haxe(HaxeModel haxe) {}

    @Validate
    void validateFlavorDimension(@Each HaxeFlavor flavor,
                                 @Path("haxe.dimensions") List<String> dimensions) {
        if (dimensions != null) {
            if (flavor.dimension == null || !dimensions.contains(flavor.dimension)) {
                throw new RuntimeException("The flavor : " + flavor
                        + "contains a unknow flavor dimension : '${flavor.dimension}'");
            }
        }
    }

    @Validate
    void validateDefaultConfigSourceSet(@Path("haxe.defaultConfig")
                                                HaxeDefaultConfig defaultConfig) {
        validateSourceSet(defaultConfig.sources);
    }

    @Validate
    void validateFlavorSourceSet(@Each HaxeFlavor flavor) {
        validateSourceSet(flavor.sources);
    }

    private void validateSourceSet(FunctionalSourceSet sourceSet) {
        sourceSet.values().each {
            it ->
                it.source.srcDirs.each {
                    file ->
                        if (!file.exists()) {
                            throw new RuntimeException("Source path : ${file} on flavor:${flavor} is invalid");
                        }
                }
        }
    }

    private void mergeSourceSet(Object from, Object to) {
        if (from != null) {
            from.sources.values().each
                    {
                        it ->
                            to.src += it.source.getSrcDirs();
                    };
        }
    }

    private HaxeVariant createVariantFromFlavor(HaxeDefaultConfig defaultConfig,
                                                HaxeFlavor flavor) {
        HaxeVariant variant = new HaxeVariant(defaultConfig);
        Util.copyDefault(flavor, variant);
        mergeSourceSet(flavor, variant);
        mergeSourceSet(defaultConfig, variant);
        return variant;
    }

    private HaxeVariant createVariantFromCombo(HaxeDefaultConfig defaultConfig,
                                               ArrayList combo) {
        HaxeVariant variant = new HaxeVariant(defaultConfig);
        combo.each { flavor ->
            Util.copyDefault(flavor, variant);
            mergeSourceSet(flavor, variant);
            mergeSourceSet(defaultConfig, variant);
        }

        return variant;
    }

    private void validateVariant(HaxeVariant variant) {
        if (variant.platform == null) {
            throw new RuntimeException("The target : ${variant.name} has no defined target platform");
        } else if (variant.main == null) {
            throw new RuntimeException("The target : ${variant.name} has no defined main class");
        }
    }

    private void createCompileTask(HaxeVariant variantWithoutBuildType,
                                   ModelMap<Task> tasks,
                                   ModelMap<HaxeBuildType> buildTypes) {
        List<String> btContainerDependencies = [];
        buildTypes.each {
            bt ->

                HaxeVariant variant = variantWithoutBuildType.clone();
                Util.copyDefault(bt, variant);
                variant.name = variantWithoutBuildType.name;

                String btVariantName = variant.getCompileTaskName(bt);
                btContainerDependencies.push(btVariantName);

                tasks.create(btVariantName, HaxeCompileTask.class,
                        new Action<HaxeCompileTask>() {
                            @Override
                            public void execute(HaxeCompileTask t) {
                                t.configurationHash = variant.hash();
                                t.dependsOn = [
                                        CheckVersionTaskName,
                                        variant.getResourceTaskName(bt)
                                ];

                                t.outputDirectory = variant.getOutputPath(t.project.buildDir);
                                t.source = t.project.files(variant.src.unique(false));
                                t.variant = variant;

                                if (variant.outputFileName != null) {
                                    String path = variant.getOutputPath(t.project.buildDir);
                                    t.output = new File(path, "/" + variant.outputFileName);
                                }
                            }
                        }
                );
        }

        createBuildTypesContainer(tasks,
                variantWithoutBuildType,
                btContainerDependencies);
    }

    private String getGroupName(HaxeVariant variant) {
        return "Haxe" + ((variant.group != null)
                ? " : " + variant.group
                : "");
    }

    private Task createBuildTypesContainer(ModelMap<Task> tasks,
                                           HaxeVariant variant,
                                           List<String> dependencies) {
        return tasks.create("haxe" + variant.name.capitalize(),
                new Action<DefaultTask>() {
                    @Override
                    public void execute(DefaultTask t) {
                        t.dependsOn = dependencies;
                        t.setGroup(getGroupName(variant));
                    }
                }
        );
    }

    private void createResourceTask(final ModelMap<Task> tasks,
                                    final HaxeVariant variant,
                                    final File resDirectory,
                                    final ModelMap<HaxeBuildType> buildTypes) {
        buildTypes.each { bt ->
            tasks.create(variant.getResourceTaskName(bt),
                    HaxeResourceTask.class,
                    new Action<HaxeResourceTask>() {
                        @Override
                        public void execute(HaxeResourceTask t) {
                            t.components = variant.components;
                            t.configurationHash = variant.hash();
                            t.outputDirectory = variant.getOutputPath(t.project.buildDir, bt.debug);
                            t.resDirectory = resDirectory;
                            t.variant = variant;
                        }
                    }
            );
        };
    }

    @Mutate
    void createCheckVersionTask(final ModelMap<Task> tasks, final HaxeModel model) {
        HaxeCheckVersion checkVersion = tasks.create(CheckVersionTaskName,
                HaxeCheckVersion.class,
                new Action<HaxeCheckVersion>() {
                    @Override
                    public void execute(HaxeCheckVersion t) {
                        t.requiredVersion = model.version;
                    }
                }
        );
    }

    @Mutate
    void createCompileTasks(final ModelMap<Task> tasks,
                            final HaxeModel model,
                            final @Path("haxe.dimensions") List<String> dimensions,
                            final @Path("haxe.defaultConfig") HaxeDefaultConfig defaultConfig,
                            final @Path("haxe.buildTypes") ModelMap<HaxeBuildType> buildTypes) {
        if (dimensions != null) {
            List<HaxeFlavor[]> groups = [];
            dimensions.each { dim ->
                HaxeFlavor[] list = model.flavors.findAll { it.dimension == dim };
                if (list.size() != 0) {
                    groups.push(list);
                }
            }

            GroovyCollections.combinations(groups).each { combo ->
                final HaxeVariant variant = createVariantFromCombo(defaultConfig, combo);
                variant.name = combo.collect { it.name.capitalize() }.join();
                variant.components = combo.collect { it.name };
                validateVariant(variant);

                createCompileTask(variant, tasks, buildTypes);
                createResourceTask(tasks, variant, model.res, buildTypes);
            }
        } else {
            model.flavors.each { it ->
                HaxeVariant variant = createVariantFromFlavor(defaultConfig, it);
                variant.name = it.name;
                variant.components = [it.name];
                validateVariant(variant);

                createCompileTask(variant, tasks, buildTypes);
                createResourceTask(tasks, variant, model.res, buildTypes);
            }
        }
    }
}

@Managed
interface HaxeModel {
    List<String> getDimensions();

    void setDimensions(List<String> value);

    File getRes()

    void setRes(File value);

    String getVersion();

    void setVersion(String value);

    HaxeDefaultConfig getDefaultConfig();

    ModelMap<HaxeFlavor> getFlavors();

    ModelMap<HaxeBuildType> getBuildTypes();
}

@Managed
interface HaxeFlavor extends Groupable, Named, HaxeCompilerParameters, HaxePlatformParameters {
    String getDimension();

    void setDimension(String value);

    String getOutputDirectoryName();

    void setOutputDirectoryName(String value);
}

@Managed
interface HaxeBuildType extends Named, HaxeCompilerParameters {}

@Managed
interface HaxeSourceSet extends LanguageSourceSet {}

@Managed
interface HaxeDefaultConfig extends HaxeCompilerParameters, HaxePlatformParameters {}

@Managed
interface Groupable {
    void setGroup(String value);

    String getGroup();
}

@Managed
interface HaxePlatformParameters {
    FunctionalSourceSet getSources();

    String getPlatform();

    void setPlatform(String value);

    void setOutput(File value);

    File getOutput();
}
