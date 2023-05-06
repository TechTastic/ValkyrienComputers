package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.ShipIntegrationMethods
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks
import net.techtastic.vc.util.SpecialLuaTables.Companion.getVectorAsTable
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos

class ShipReaderPeripheral(private val level: Level, private val pos: BlockPos) : IPeripheral {
    @LuaFunction
    fun getShipName(): String =
            ShipIntegrationMethods.getNameFromShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"))

    @LuaFunction
    fun setShipName(name: String) =
            ShipIntegrationMethods.setNameForShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"), name)

    @LuaFunction
    fun getShipID() : Long =
            ShipIntegrationMethods.getIdFromShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"))

    @LuaFunction
    fun getMass() : Double =
            ShipIntegrationMethods.getMassFromShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"))

    @LuaFunction
    fun getVelocity() : Map<String, Double> =
            ShipIntegrationMethods.getVelocityFromShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"))

    @LuaFunction
    fun getPosition(arg: IArguments) : Map<String, Double> =
            ShipIntegrationMethods.getPositionFromShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"), arg.optBoolean(0, false))

    @LuaFunction
    fun getScale() : Map<String, Double> =
            ShipIntegrationMethods.getScaleFromShip(isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"))

    @LuaFunction
    fun getSize() : MutableMap<String, Int> {
        val aabb = isEnabledAndOnShip(pos)?.shipAABB ?: throw LuaException("Not on a Ship")
        return mutableMapOf(
                Pair("x", aabb.maxX() - aabb.minX()),
                Pair("y", aabb.maxY() - aabb.minY()),
                Pair("z", aabb.maxZ() - aabb.minZ())
        )
    }

    @LuaFunction
    fun getRotation(arg: IArguments): Map<String, Double> =
            ShipIntegrationMethods.getRotationFromShip(
                    isEnabledAndOnShip(pos) ?: throw LuaException("Not on a Ship"),
                    arg.optBoolean(0, false))

    @LuaFunction
    fun transformPosition(x: Double, y: Double, z: Double): MutableMap<Any, Any> {
        val ship = isEnabledAndOnShip(BlockPos(x, y, z))
        return if (ship != null) {
            getVectorAsTable(ship.shipToWorld.transformPosition(Vector3d(x, y, z)))
        } else {
            val matrix = isEnabledAndOnShip(pos)?.worldToShip ?: throw LuaException("Not on a Ship")
            getVectorAsTable(matrix.transformPosition(Vector3d(x, y, z)))
        }
    }

    @LuaFunction
    fun transformDirection(x: Double, y: Double, z: Double): MutableMap<Any, Any> {
        val matrix = isEnabledAndOnShip(pos)?.worldToShip ?: throw LuaException("Not on a Ship")
        return getVectorAsTable(matrix.transformDirection(Vector3d(x, y, z)))
    }

    @Throws(LuaException::class)
    fun isEnabledAndOnShip(position: BlockPos): ServerShip? {
        if (level.isClientSide()) return null
        if (ValkyrienComputersConfig.SERVER.ComputerCraft.disableShipReaders) throw LuaException("Disabled")
        return (level as ServerLevel).getShipManagingPos(position)
    }

    override fun getType(): String ="ship_reader"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level.getBlockState(pos).`is`(ComputerCraftBlocks.READER.get())
}