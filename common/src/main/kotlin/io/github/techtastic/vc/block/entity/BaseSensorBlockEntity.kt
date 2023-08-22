package io.github.techtastic.vc.block.entity

import dev.architectury.platform.Platform
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import io.github.techtastic.vc.util.CCComputerHandler

open class BaseSensorBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state)  {
    val ccHandler : CCComputerHandler = CCComputerHandler()

    open fun <T : BlockEntity?> tick(level: Level, pos: BlockPos, state: BlockState, be: T) {
    }

    open fun queueCCEvent(name: String, objects: Any) {
        if (Platform.isModLoaded("computercraft"))
            this.ccHandler.queueEvent(name, objects)
    }
}