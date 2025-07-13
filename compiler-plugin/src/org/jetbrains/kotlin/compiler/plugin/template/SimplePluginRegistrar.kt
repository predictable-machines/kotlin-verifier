package org.jetbrains.kotlin.compiler.plugin.template

import org.jetbrains.kotlin.compiler.plugin.template.fir.KotlinVerifierAdditionalCheckers
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class SimplePluginRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::KotlinVerifierAdditionalCheckers
    }
}
