package net.techtastic.vc.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.ShipIntegrationMethods
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import org.joml.Quaterniondc
import org.valkyrienskies.core.api.ships.ServerShip

class GyroscopicSensorBlockEntity(pos: BlockPos, state: BlockState) : BaseSensorBlockEntity(ComputerCraftBlockEntities.GYRO.get(), pos, state) {
    var giveQuaternion = true
    private var previousQuaternion: Quaterniondc? = null

    override fun queueEvent(level: Level, pos: BlockPos, ship: ServerShip?) {
        if (ship == null || ValkyrienComputersConfig.SERVER.ComputerCraft.disableGyros) return

        val quat = ship.transform.shipToWorldRotation
        if (previousQuaternion?.let { quat.equals(it.x(), it.y(), it.z(), it.w()) } == true)
            return

        this.events.put("rotation", ShipIntegrationMethods.getRotationFromShip(ship, giveQuaternion))

        previousQuaternion = quat
    }
}