package net.techtastic.vc

import dev.architectury.platform.Platform
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import net.techtastic.vc.registry.DeferredRegister
import net.techtastic.vc.registry.RegistrySupplier

@Suppress("unused")
object ValkyrienComputersBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    //val FARTER = ValkyrienComputersBlocks.FARTER withBE ::FartBlockEntity byName "farter"
    //val BEARING = ValkyrienComputersBlocks.BEARING withBE ::BearingBlockEntity byName "bearing"

    fun register() {
        if (Platform.isModLoaded("computercraft")) ComputerCraftBlockEntities.registerBlockEntities()
        BLOCKENTITIES.applyAll()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)
    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

            BlockEntityType.Builder.of(
                this.second,
                *this.first.map { it.get() }.toTypedArray()
            ).build(type)
        }
}
