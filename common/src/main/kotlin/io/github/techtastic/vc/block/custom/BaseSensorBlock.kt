package io.github.techtastic.vc.block.custom

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import io.github.techtastic.vc.block.entity.BaseSensorBlockEntity

abstract class BaseSensorBlock(properties : Properties) : BaseEntityBlock(properties) {
    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T> =
            BlockEntityTicker { level: Level, pos : BlockPos, state : BlockState, blockEntity ->
                if (level.isClientSide) return@BlockEntityTicker

                if (blockEntity is BaseSensorBlockEntity) blockEntity.tick(level, pos, state, blockEntity)
            }
}