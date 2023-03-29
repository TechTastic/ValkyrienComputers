package net.techtastic.vc.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.blockentity.AccelerometerSensorBlockEntity
import net.techtastic.vc.blockentity.BaseSensorBlockEntity
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity
import net.techtastic.vc.blockentity.MotorBlockEntity

class AccelerometerSensorBlock(properties: Properties) : BaseEntityBlock(properties) {
    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
            AccelerometerSensorBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T> =
    BlockEntityTicker { level: Level, pos : BlockPos, state : BlockState, blockEntity ->
        if (level.isClientSide) return@BlockEntityTicker

        if (blockEntity is AccelerometerSensorBlockEntity) {
            blockEntity.tick(level, pos, state, blockEntity)
        }
    }
}