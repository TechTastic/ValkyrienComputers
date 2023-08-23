package io.github.techtastic.vc.block.custom

import io.github.techtastic.vc.block.entity.AltimeterSensorBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class AltimeterSensorBlock(properties: Properties) : BaseSensorBlock(properties) {
    override fun getRenderShape(state: BlockState): RenderShape =
            RenderShape.ENTITYBLOCK_ANIMATED
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
            AltimeterSensorBlockEntity(pos, state)
}