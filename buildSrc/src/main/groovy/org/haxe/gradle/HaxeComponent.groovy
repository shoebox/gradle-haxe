package org.haxe.gradle;

import org.gradle.platform.base.GeneralComponentSpec;
import org.gradle.model.Managed;
import org.gradle.platform.base.component.*;
import org.gradle.platform.base.ComponentSpec;

interface IHaxeComponent extends ComponentSpec {}
class HaxeComponent extends BaseComponentSpec implements IHaxeComponent{}
