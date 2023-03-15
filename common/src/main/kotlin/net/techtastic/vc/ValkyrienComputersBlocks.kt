package net.techtastic.vc

import dev.architectury.platform.Platform
import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks
import net.techtastic.vc.registry.DeferredRegister

@Suppress("unused")
object ValkyrienComputersBlocks {
    private val BLOCKS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.BLOCK_REGISTRY)

    fun register() {
        if (Platform.isModLoaded("computercraft")) ComputerCraftBlocks.registerCCBlocks()
        BLOCKS.applyAll()
    }

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(ValkyrienComputersItems.TAB)) }
        }
    }

}
