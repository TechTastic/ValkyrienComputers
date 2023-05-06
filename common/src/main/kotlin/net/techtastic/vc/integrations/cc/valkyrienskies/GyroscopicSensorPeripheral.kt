package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.server.level.ServerLevel
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity
import net.techtastic.vc.integrations.ShipIntegrationMethods
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import java.util.*

class GyroscopicSensorPeripheral(private val sensor : GyroscopicSensorBlockEntity) : IPeripheral {
    val level = sensor.level
    var pos = sensor.blockPos

    @LuaFunction
    fun giveQuaternion(args: IArguments) {
        val bool = args.optBoolean(0)

        if (bool.isPresent)
            sensor.giveQuaternion = bool.get()
        else
            sensor.giveQuaternion = sensor.giveQuaternion.not()
    }

    @Throws(LuaException::class)
    @LuaFunction
    fun getRotation(args: IArguments): Map<String, Double> {
        if (level == null || level.isClientSide)
            throw LuaException("Level doesn't exist or is clientside")
        val level = level as ServerLevel
        val ship: ServerShip = level.getShipManagingPos(pos)
                ?: throw LuaException("Not on a Ship")

        return ShipIntegrationMethods.getRotationFromShip(ship, args.optBoolean(0, false))
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