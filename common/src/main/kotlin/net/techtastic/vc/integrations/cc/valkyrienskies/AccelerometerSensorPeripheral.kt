package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.server.level.ServerLevel
import net.techtastic.vc.blockentity.AccelerometerSensorBlockEntity
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import java.util.*
import kotlin.math.asin
import kotlin.math.atan2

class AccelerometerSensorPeripheral(private val sensor : AccelerometerSensorBlockEntity) : IPeripheral {
    val level = sensor.level
    var pos = sensor.blockPos

    @Throws(LuaException::class)
    @LuaFunction
    fun getVelocity(): Map<String, Double> {
        if (level == null || level.isClientSide) throw LuaException("Level doesn't exist or is clientside")
        val level = level as ServerLevel
        val ship: ServerShip = level.getShipManagingPos(pos) ?: throw LuaException("Not on a Ship")

        val vel = ship.velocity

        return mapOf(
                Pair("x", vel.x()),
                Pair("y", vel.y()),
                Pair("z", vel.z())
        )
    }

    override fun getType(): String = "accelerometer"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level?.getBlockEntity(pos) is AccelerometerSensorBlockEntity

    override fun attach(computer: IComputerAccess) {
        if (!sensor.computers.contains(computer))
            sensor.computers.add(computer)
    }

    override fun detach(computer: IComputerAccess) {
        if (sensor.computers.contains(computer))
            sensor.computers.remove(computer)
    }
}