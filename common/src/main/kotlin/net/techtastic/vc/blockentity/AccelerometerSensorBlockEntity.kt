package net.techtastic.vc.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.ShipIntegrationMethods
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip

class AccelerometerSensorBlockEntity(pos: BlockPos, state: BlockState) : BaseSensorBlockEntity(ComputerCraftBlockEntities.ACCEL.get(), pos, state) {
    var prevVelocity: Vector3dc? = null

    override fun queueEvent(level: Level, pos: BlockPos, ship: ServerShip?) {
        if (ship == null || ValkyrienComputersConfig.SERVER.ComputerCraft.disableAccels) return

        val vel = ship.velocity
        if (prevVelocity?.let { vel.equals(it.x(), it.y(), it.z()) } == true) return

        this.events["acceleration"] = ShipIntegrationMethods.getVelocityFromShip(ship)

        prevVelocity = vel
    }
}