package io.github.techtastic.vc.item

import dev.architectury.platform.Platform
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import io.github.techtastic.vc.ValkyrienComputersMod
import io.github.techtastic.vc.block.VCBlocks
import io.github.techtastic.vc.registry.BlockItemHelper
import io.github.techtastic.vc.registry.CreativeTabs
import io.github.techtastic.vc.registry.DeferredRegister
import io.github.techtastic.vc.registry.RegistrySupplier
import io.github.techtastic.vc.services.ValkyrienComputersPlatformHelper

@Suppress("unused")
object VCItems {
    val ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.ITEM_REGISTRY)

    val TAB: CreativeModeTab = CreativeTabs.create(
        ResourceLocation(
                ValkyrienComputersMod.MOD_ID,
            "vc_tab"
        )
    ) { ItemStack(LOGO.get()) }

    var LOGO: RegistrySupplier<Item> = ITEMS.register("vc_logo") { Item(Item.Properties()) }

    var GYRO = ITEMS.register("gyro") { BlockItemHelper.getNew(VCBlocks.GYRO.get(), Item.Properties().tab(TAB)) }

    fun register() {
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
