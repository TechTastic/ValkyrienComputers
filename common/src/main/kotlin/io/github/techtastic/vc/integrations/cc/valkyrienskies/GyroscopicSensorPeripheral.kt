package io.github.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.server.level.ServerLevel
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.util.ShipIntegrationMethods
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import java.util.*

class GyroscopicSensorPeripheral(private val sensor: GyroscopicSensorBlockEntity) : BaseSensorPeripheral<GyroscopicSensorBlockEntity>(sensor) {
    val level = sensor.level
    var pos = sensor.blockPos

    override fun getType(): String = "gyroscope"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level?.getBlockEntity(pos) is GyroscopicSensorBlockEntity &&
            iPeripheral is GyroscopicSensorPeripheral

    @Throws(LuaException::class)
    @LuaFunction
    fun getRotation(args: IArguments): Map<String, Double>? {
        if (level == null || level.isClientSide)
            throw LuaException("Level doesn't exist or is clientside")
        val level = level as ServerLevel
        val ship: ServerShip = level.getShipManagingPos(pos)
                ?: throw LuaException("Not on a Ship")

        return if (args.optBoolean(0, false))
            ShipIntegrationMethods.getRotationAsQuaternion(ship)
        else
            ShipIntegrationMethods.getRotationAsRPY(ship)
    }

    @LuaFunction
    fun getTestAngle(): Double =
            this.sensor.testAngle

    @LuaFunction
    fun setTestAngle(angle: Double) {
        this.sensor.testAngle = 0.0.coerceAtLeast((2 * Math.PI).coerceAtMost(angle))
    }
}