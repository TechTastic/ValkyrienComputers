package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.server.level.ServerLevel
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import java.util.*
import kotlin.math.asin
import kotlin.math.atan2

class GyroscopicSensorPeripheral(private val sensor : GyroscopicSensorBlockEntity) : IPeripheral {
    val level = sensor.level
    var pos = sensor.blockPos

    @LuaFunction
    fun giveQuaternion(args: IArguments) {
        val bool = args.optBoolean(0)

        if (bool.isPresent) {
            sensor.giveQuaternion = bool.get()
        } else {
            sensor.giveQuaternion = sensor.giveQuaternion.not()
        }
    }

    @Throws(LuaException::class)
    @LuaFunction
    fun getRotation(args: IArguments): MutableMap<String, Double> {
        if (level == null || level.isClientSide) throw LuaException("Level doesn't exist or is clientside")
        val level = level as ServerLevel
        val ship: ServerShip = level.getShipManagingPos(pos) ?: throw LuaException("Not on a Ship")

        val quat = ship.transform.shipToWorldRotation
        val x = quat.x()
        val y = quat.y()
        val z = quat.z()
        val w = quat.w()
        val rotation = mutableMapOf<String, Double>()
        if (args.optBoolean(0, false)) {
            rotation["x"] = x
            rotation["y"] = y
            rotation["z"] = z
            rotation["w"] = w
        } else {
            rotation["roll"] = atan2(2 * y * w - 2 * x * z, 1 - 2 * y * y - 2 * z * z)
            rotation["pitch"] = atan2(2 * x * w - 2 * y * z, 1 - 2 * x * x - 2 * z * z)
            rotation["yaw"] = asin(2 * x * y + 2 * z * w)
        }
        
        return rotation
    }

    override fun getType(): String = "gyroscope"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level?.getBlockEntity(pos) is GyroscopicSensorBlockEntity

    override fun attach(computer: IComputerAccess) {
        if (!sensor.computers.contains(computer))
            sensor.computers.add(computer)
    }

    override fun detach(computer: IComputerAccess) {
        if (sensor.computers.contains(computer))
            sensor.computers.remove(computer)
    }
}