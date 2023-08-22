package io.github.techtastic.vc.block

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import io.github.techtastic.vc.item.VCItems
import io.github.techtastic.vc.ValkyrienComputersMod
import io.github.techtastic.vc.block.custom.GyroscopicSensorBlock
import io.github.techtastic.vc.registry.DeferredRegister

@Suppress("unused")
object VCBlocks {
    private val BLOCKS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.BLOCK_REGISTRY)
    private val BLOCKS_WITHOUT_ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.BLOCK_REGISTRY)
    private val ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.ITEM_REGISTRY)

    val GYRO = BLOCKS_WITHOUT_ITEMS.register("gyro") { GyroscopicSensorBlock(BlockBehaviour.Properties.of(Material.STONE)) }

    fun register() {
        BLOCKS.applyAll()
        BLOCKS_WITHOUT_ITEMS.applyAll()

        BLOCKS.forEach {
            ITEMS.register(it.name) { BlockItem(it.get(), Item.Properties().tab(VCItems.TAB)) }
        }
        ITEMS.applyAll()
    }

}
