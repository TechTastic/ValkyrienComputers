package net.techtastic.vc.util

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos

class ShipUtils {
    companion object {
        fun getShipManagingPos(level: ServerLevel, pos: BlockPos): ServerShip? = level.getShipManagingPos(pos)
    }
}