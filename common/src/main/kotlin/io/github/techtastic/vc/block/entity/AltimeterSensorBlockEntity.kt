package io.github.techtastic.vc.block.entity

import io.github.techtastic.vc.block.VCBlockEntities
import io.github.techtastic.vc.util.ShipIntegrationMethods
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.mod.common.getShipManagingPos

class AltimeterSensorBlockEntity(pos: BlockPos, state: BlockState): BaseSensorBlockEntity(VCBlockEntities.ALTIMETER.get(), pos, state) {
    private var previousAltitude: Double? = null

    override fun <T : BlockEntity?> tick(level: Level, pos: BlockPos, state: BlockState, be: T) {
        if (level.isClientSide)
            return

        val level = level as ServerLevel

        val ship = level.getShipManagingPos(pos) ?: return

        val altitude = ship.transform.positionInWorld.y()
        if (previousAltitude != null && (altitude < previousAltitude!! + 1 || altitude > previousAltitude!! - 1))
            return

        this.queueCCEvent("altitude", altitude)

        previousAltitude = altitude
    }
}