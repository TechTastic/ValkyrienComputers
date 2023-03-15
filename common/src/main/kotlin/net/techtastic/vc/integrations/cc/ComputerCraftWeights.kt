package net.techtastic.vc.integrations.cc

import dev.architectury.platform.Platform
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.ValkyrienComputersMod
import org.valkyrienskies.core.apigame.world.chunks.BlockType
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.BlockStateInfoProvider
import org.valkyrienskies.mod.common.ValkyrienSkiesMod.vsCore

object ComputerCraftWeights : BlockStateInfoProvider {
    override val priority: Int
        get() = 30

    override fun getBlockStateMass(blockState: BlockState): Double? {
        return null
    }

    override fun getBlockStateType(blockState: BlockState): BlockType? {
        if (Platform.isModLoaded("computercraft") && blockState.block == ComputerCraftBlocks.TOP.get())
            return vsCore.blockTypes.air

        return null
    }

    fun register() {
        Registry.register(BlockStateInfo.REGISTRY, ResourceLocation(ValkyrienComputersMod.MOD_ID, "motor_top"), ComputerCraftWeights)
    }
}