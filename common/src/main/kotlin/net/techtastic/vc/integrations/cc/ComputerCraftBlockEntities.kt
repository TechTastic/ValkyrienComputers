package net.techtastic.vc.integrations.cc

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.ValkyrienComputersMod
import net.techtastic.vc.blockentity.*
import net.techtastic.vc.registry.DeferredRegister.Companion.create
import net.techtastic.vc.registry.RegistrySupplier

object ComputerCraftBlockEntities {
    private val BLOCKENTITIES = create(ValkyrienComputersMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    var MOTOR = ComputerCraftBlocks.MOTOR withBE ::MotorBlockEntity byName "motor_cc"
    var GYRO = ComputerCraftBlocks.GRYO withBE ::GyroscopicSensorBlockEntity byName "gyro_cc"
    var ACCEL = ComputerCraftBlocks.ACCEL withBE ::AccelerometerSensorBlockEntity byName "accel_cc"
    var SONIC = ComputerCraftBlocks.SONIC withBE ::UltrasonicSensorBlockEntity byName "sonic_cc"

    // Custom Methods

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
            Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
            Pair(this, blockEntity)

    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
            BLOCKENTITIES.register(name) {
                val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

                BlockEntityType.Builder.of(
                        this.second,
                        *this.first.map { it.get() }.toTypedArray()
                ).build(type)
            }

    fun registerBlockEntities() {
        BLOCKENTITIES.applyAll()
    }
}