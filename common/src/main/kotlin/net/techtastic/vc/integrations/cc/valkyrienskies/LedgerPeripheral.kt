package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.techtastic.vc.integrations.ShipIntegrationMethods
import org.valkyrienskies.mod.common.getShipManagingPos

class LedgerPeripheral(private val level: Level, private val pos: BlockPos): IPeripheral {
    override fun equals(p0: IPeripheral?): Boolean = p0 is LedgerPeripheral

    override fun getType(): String = "ledger"

    @LuaFunction
    fun getMass(): Double =
            (level as ServerLevel).getShipManagingPos(pos)?.inertiaData?.mass
                    ?: throw LuaException("not on a ship")

    @LuaFunction
    fun getScale(): Map<String, Double> = ShipIntegrationMethods.getScaleFromShip((level as ServerLevel).getShipManagingPos(pos)
            ?: throw LuaException("not on a ship"))

    @LuaFunction
    fun getSize(): Map<String, Int> = ShipIntegrationMethods.getSizeFromShip((level as ServerLevel).getShipManagingPos(pos)
            ?: throw LuaException("not on a ship"))

    @LuaFunction
    fun getShipId(): Long = ShipIntegrationMethods.getIdFromShip((level as ServerLevel).getShipManagingPos(pos)
            ?: throw LuaException("not on a ship"))

    @LuaFunction
    fun getName(): String = ShipIntegrationMethods.getNameFromShip((level as ServerLevel).getShipManagingPos(pos)
            ?: throw LuaException("not on a ship"))

    fun setName(name: String) = ShipIntegrationMethods.setNameForShip((level as ServerLevel).getShipManagingPos(pos)
            ?: throw LuaException("not on a ship"), name)

    fun getBlockAt(x: Double, y: Double, z: Double): Map<String, Any> {
        val currentShip = (level as ServerLevel).getShipManagingPos(pos)
                ?: throw LuaException("not on a ship")
        val test = BlockPos(x, y, z)
        val testShip = level.getShipManagingPos(test)
                ?: throw LuaException("location not on a ship")
        if (currentShip.id != testShip.id)
            throw LuaException("not on same ship")

        val block = level.getBlockState(test)
        return mapOf(
                Pair("id", block.block.descriptionId),
                Pair("state", getBlockState(block))
        )
    }

    private fun getBlockState(state: BlockState): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        for (prop in state.values) {
            map[prop.key.name] = state.getValue(prop.key)
        }
        return map.toMap()
    }
}