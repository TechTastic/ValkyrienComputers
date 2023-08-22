package io.github.techtastic.vc.block.custom

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity

class GyroscopicSensorBlock(properties: Properties) : BaseSensorBlock(properties) {
    override fun getRenderShape(blockState: BlockState): RenderShape =
            RenderShape.ENTITYBLOCK_ANIMATED

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
            GyroscopicSensorBlockEntity(pos, state)
}