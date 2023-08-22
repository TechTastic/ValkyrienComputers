package io.github.techtastic.vc

import com.jozufozu.flywheel.core.PartialModel
import net.minecraft.resources.ResourceLocation

object VCPartials {
    val GYRO_CORE = block("gyro/core")
    val GYRO_OUTER = block("gyro/outer")
    val GYRO_MIDDLE = block("gyro/middle")
    val GYRO_INNER = block("gyro/inner")

    private fun block(path: String): PartialModel {
        return PartialModel(ResourceLocation(ValkyrienComputersMod.MOD_ID, "block/$path"))
    }

    private fun entity(path: String): PartialModel {
        return PartialModel(ResourceLocation(ValkyrienComputersMod.MOD_ID, "entity/$path"))
    }

    private fun item(path: String): PartialModel {
        return PartialModel(ResourceLocation(ValkyrienComputersMod.MOD_ID, "item/$path"))
    }

    fun init() {
        // init static fields
    }
}