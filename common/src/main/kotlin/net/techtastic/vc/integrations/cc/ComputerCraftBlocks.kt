package net.techtastic.vc.integrations.cc

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.techtastic.vc.ValkyrienComputersItems.TAB
import net.techtastic.vc.ValkyrienComputersMod
import net.techtastic.vc.block.*
import net.techtastic.vc.registry.DeferredRegister
import net.techtastic.vc.registry.DeferredRegister.Companion.create

object ComputerCraftBlocks {
    private val BLOCKS = create(ValkyrienComputersMod.MOD_ID, Registry.BLOCK_REGISTRY)
    @JvmField
    var RADAR = BLOCKS.register("radar_cc") { Block(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }
    @JvmField
    var READER = BLOCKS.register("reader_cc") { Block(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }
    @JvmField
    var GRYO = BLOCKS.register("gyro_cc") { GyroscopicSensorBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }
    @JvmField
    var ACCEL = BLOCKS.register("accel_cc") { AccelerometerSensorBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }
    @JvmField
    var SONIC = BLOCKS.register("sonic_cc") { UltrasonicSensorBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }
    @JvmField
    var MOTOR = BLOCKS.register<Block>("motor_cc") { MotorBaseBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }

    var TOP = BLOCKS.register<Block>("top_cc") { TopBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)) }



    fun registerCCBlocks() {
        BLOCKS.applyAll()
    }

    fun registerItems(items: DeferredRegister<Item?>) {
        for (block in BLOCKS) {
            if (block == TOP) {
                continue
            }
            items.register(block.name) { BlockItem(block.get(), Item.Properties().tab(TAB)) }
        }
    }
}