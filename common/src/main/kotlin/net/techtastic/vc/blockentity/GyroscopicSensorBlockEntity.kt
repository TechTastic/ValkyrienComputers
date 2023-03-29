package net.techtastic.vc.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import org.joml.Quaterniondc
import org.valkyrienskies.core.api.ships.ServerShip
import kotlin.math.asin
import kotlin.math.atan2

class GyroscopicSensorBlockEntity(pos: BlockPos, state: BlockState) : BaseSensorBlockEntity(ComputerCraftBlockEntities.GYRO.get(), pos, state) {
    var giveQuaternion = true
    private var previousQuaternion: Quaterniondc? = null

    override fun queueEvent(level: Level, pos: BlockPos, ship: ServerShip?) {
        if (ship == null || ValkyrienComputersConfig.SERVER.ComputerCraft.disableGyros) return

        val quat = ship.transform.shipToWorldRotation
        if (previousQuaternion?.let { quat.equals(it.x(), it.y(), it.z(), it.w()) } == true) return
        val x = quat.x()
        val y = quat.y()
        val z = quat.z()
        val w = quat.w()
        val event = mutableMapOf<String, Double>()
        if (giveQuaternion) {
            event["x"] = x
            event["y"] = y
            event["z"] = z
            event["w"] = w
        } else {
            event["roll"] = atan2(2 * y * w - 2 * x * z, 1 - 2 * y * y - 2 * z * z)
            event["pitch"] = atan2(2 * x * w - 2 * y * z, 1 - 2 * x * x - 2 * z * z)
            event["yaw"] = asin(2 * x * y + 2 * z * w)
        }

        this.events.put("rotation", event)

        previousQuaternion = quat
    }
}