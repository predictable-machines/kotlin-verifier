package org.jetbrains.kotlin.compiler.plugin.template.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class SimpleIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        // This is a placeholder IR generation extension
        // In a real plugin, this would contain IR transformations
    }
}