package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.server.level.ServerLevel
import net.techtastic.vc.blockentity.AccelerometerSensorBlockEntity
import net.techtastic.vc.integrations.ShipIntegrationMethods
import org.valkyrienskies.mod.common.getShipManagingPos
import java.util.*

class AccelerometerSensorPeripheral(private val sensor : AccelerometerSensorBlockEntity) : IPeripheral {
    val level = sensor.level
    var pos = sensor.blockPos

    @Throws(LuaException::class)
    @LuaFunction
    fun getVelocity(): Map<String, Double> {
        if (level == null || level.isClientSide) throw LuaException("Level doesn't exist or is clientside")
        return ShipIntegrationMethods.getVelocityFromShip((level as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("Not on a Ship"))
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