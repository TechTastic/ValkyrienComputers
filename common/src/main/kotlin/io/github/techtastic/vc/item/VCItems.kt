package io.github.techtastic.vc.item

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import io.github.techtastic.vc.ValkyrienComputersMod
import io.github.techtastic.vc.registry.CreativeTabs
import io.github.techtastic.vc.registry.DeferredRegister
import io.github.techtastic.vc.registry.RegistrySupplier

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

    fun register() {
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
