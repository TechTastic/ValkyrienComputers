package net.techtastic.vc.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
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
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.techtastic.vc.blockentity.MotorBlockEntity
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import net.techtastic.vc.util.DirectionalShape
import net.techtastic.vc.util.RotShapes

class MotorBaseBlock(properties: Properties) : BaseEntityBlock(properties) {
    val MOTOR_BASE = RotShapes.or(
            RotShapes.box(0.0, 4.0, 6.0, 6.0, 8.0, 10.0),
            RotShapes.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            RotShapes.box(0.0, 4.0, 10.0, 16.0, 8.0, 16.0),
            RotShapes.box(0.0, 4.0, 0.0, 16.0, 8.0, 6.0),
            RotShapes.box(10.0, 4.0, 6.0, 16.0, 8.0, 10.0)
    )

    val MOTOR_BASE_SHAPE = DirectionalShape.up(MOTOR_BASE)

    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
                .setValue(BlockStateProperties.FACING, ctx.clickedFace)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        /*if (level is ServerLevel) {
            var be = level.getBlockEntity(pos) as MotorBlockEntity
            be.makeOrGetTop(level, pos)
        }*/
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return MOTOR_BASE_SHAPE[state.getValue(BlockStateProperties.FACING)]
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return MotorBlockEntity(pos, state)
    }
}