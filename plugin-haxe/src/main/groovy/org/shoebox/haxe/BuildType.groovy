package org.shoebox.haxe;

import org.gradle.api.*;
import org.gradle.model.*;

@Managed
public interface BuildType extends Named, HaxeCompilerParameters
{

}
