package io.github.techtastic.vc.block.custom

import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.util.ShipIntegrationMethods
import io.github.techtastic.vc.util.VCProperties
import io.github.techtastic.vc.util.VCProperties.OUTPUT
import io.github.techtastic.vc.util.VoxelShapeUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.math.abs
import kotlin.math.floor

class GyroscopicSensorBlock(properties: Properties) : BaseSensorBlock(properties) {
    var SHAPE: VoxelShape? = null

    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(OUTPUT, VCProperties.OutputAxis.X))
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        if (SHAPE == null)
            SHAPE = VoxelShapeUtil.getGyroVoxel().optimize()

        return SHAPE!!
    }

    override fun getRenderShape(blockState: BlockState): RenderShape =
            RenderShape.ENTITYBLOCK_ANIMATED

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
            GyroscopicSensorBlockEntity(pos, state)

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val state = super.getStateForPlacement(context)
        val player = context.player ?: return state?.setValue(FACING, Direction.DOWN)

        if (player.isShiftKeyDown)
            return state?.setValue(FACING, context.clickedFace.opposite)
        return state?.setValue(FACING, context.nearestLookingDirection)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, OUTPUT)

        super.createBlockStateDefinition(builder)
    }

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        if (player.getItemInHand(hand).`is`(Items.REDSTONE_TORCH)) {
            level.setBlockAndUpdate(pos, state.cycle(OUTPUT))
            return InteractionResult.SUCCESS
        }

        return super.use(state, level, pos, player, hand, hit)
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos): Int {
        //return super.getAnalogOutputSignal(state, level, pos)

        val ship = level.getShipManagingPos(pos) ?: return 0
        val rpy = ShipIntegrationMethods.convertQuaternionToRPY(ship.transform.shipToWorldRotation)

        val redstone = abs(when (state.getValue(OUTPUT)) {
            VCProperties.OutputAxis.X ->
                floor(Math.toDegrees(rpy.x) / 24).toInt()
            VCProperties.OutputAxis.Y ->
                floor(Math.toDegrees(rpy.y) / 24).toInt()
            VCProperties.OutputAxis.Z ->
                floor(Math.toDegrees(rpy.z) / 24).toInt()
            else -> 0
        })

        System.err.println("Analog Signal Strength: $redstone")

        return redstone
    }
}