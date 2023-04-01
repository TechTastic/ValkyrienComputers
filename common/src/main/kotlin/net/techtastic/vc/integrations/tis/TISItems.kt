package net.techtastic.vc.integrations.tis

import li.cil.tis3d.common.item.ModuleItem
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.ValkyrienComputersItems
import net.techtastic.vc.ValkyrienComputersMod
import net.techtastic.vc.item.GyroscopicSensorModuleItem
import net.techtastic.vc.registry.DeferredRegister

object TISItems {
    val ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.ITEM_REGISTRY)

    val GYRO_MODULE = if (ValkyrienComputersConfig.SERVER.TIS3D.disableGyros) {
        ITEMS.register("gyro_tis") { ModuleItem(Item.Properties().tab(ValkyrienComputersItems.TAB)) }
    } else
        null

    fun register() {
        ITEMS.applyAll()
    }
}