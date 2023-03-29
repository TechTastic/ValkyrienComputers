package net.techtastic.vc.blockentity

import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos

open class BaseSensorBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state)  {
    val computers : MutableList<IComputerAccess> = mutableListOf()
    var events : MutableMap<String, Any?> = mutableMapOf();

    open fun <T : BlockEntity?> tick(level: Level, pos: BlockPos, state: BlockState, be: T) {
        if (level.isClientSide) return

        val level = level as ServerLevel
        val ship : ServerShip? = level.getShipManagingPos(pos)
        ship?.let {
            if (events.isEmpty()) return;

            for(computer in computers) {
                computer.queueEvent(events.keys.last(), events.values.last())
            }

            events.clear()
            queueEvent(level, pos, ship)
        }
    }

    open fun queueEvent(level: Level, pos: BlockPos, ship: ServerShip?) {
    }
}