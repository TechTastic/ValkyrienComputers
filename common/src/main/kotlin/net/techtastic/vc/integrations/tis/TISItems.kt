package net.techtastic.vc.integrations.tis

import li.cil.tis3d.common.item.ModuleItem
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.ValkyrienComputersItems
import net.techtastic.vc.ValkyrienComputersMod
import net.techtastic.vc.item.AccelerometerSensorModuleItem
import net.techtastic.vc.item.GyroscopicSensorModuleItem
import net.techtastic.vc.item.UltrasonicSensorModuleItem
import net.techtastic.vc.registry.DeferredRegister

object TISItems {
    val ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.ITEM_REGISTRY)

    val GYRO_MODULE = ITEMS.register("gyro_tis") { GyroscopicSensorModuleItem() }

    val ACCEL_MODULE = ITEMS.register("accel_tis") { AccelerometerSensorModuleItem() }

    val SONIC_MODULE = ITEMS.register("sonic_tis") { UltrasonicSensorModuleItem() }

    fun register() {
        ITEMS.applyAll()
    }
}