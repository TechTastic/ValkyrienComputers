package io.github.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import io.github.techtastic.vc.block.entity.AltimeterSensorBlockEntity
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.util.ShipIntegrationMethods
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos

class AltimeterSensorPeripheral(private val altimeter: AltimeterSensorBlockEntity): BaseSensorPeripheral<AltimeterSensorBlockEntity>(altimeter) {
    val level = altimeter.level
    var pos = altimeter.blockPos

    override fun equals(peripheral: IPeripheral?): Boolean = level?.getBlockEntity(pos) is AltimeterSensorBlockEntity &&
            peripheral is AltimeterSensorPeripheral

    override fun getType(): String = "altimeter"

    @Throws(LuaException::class)
    @LuaFunction
    fun getAltitude(): Double {
        if (level == null || level.isClientSide)
            throw LuaException("Level doesn't exist or is clientside")
        val level = level as ServerLevel
        val ship: ServerShip = level.getShipManagingPos(pos)
                ?: throw LuaException("Not on a Ship")

        return ship.transform.positionInWorld.y()
    }
}