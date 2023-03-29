package net.techtastic.vc.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.techtastic.vc.blockentity.BaseSensorBlockEntity
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity
import net.techtastic.vc.blockentity.MotorBlockEntity
import net.techtastic.vc.blockentity.UltrasonicSensorBlockEntity

class UltrasonicSensorBlock(properties: Properties) : BaseEntityBlock(properties) {
    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        if (ctx.player?.isCrouching == true) {
            return defaultBlockState()
                    .setValue(BlockStateProperties.FACING, ctx.nearestLookingDirection)
        }

        return defaultBlockState()
                .setValue(BlockStateProperties.FACING, ctx.nearestLookingDirection.opposite)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
            UltrasonicSensorBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T> =
    BlockEntityTicker { level: Level, pos : BlockPos, state : BlockState, blockEntity ->
        if (level.isClientSide) return@BlockEntityTicker

        if (blockEntity is UltrasonicSensorBlockEntity) {
            blockEntity.tick(level, pos, state, blockEntity)
        }
    }
}