package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.ShipIntegrationMethods
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.squaredDistanceBetweenInclShips
import org.valkyrienskies.mod.common.toWorldCoordinates
import org.valkyrienskies.mod.common.transformToNearbyShipsAndWorld

class RadarPeripheral(private val level: Level, private val pos: BlockPos) : IPeripheral {
    @LuaFunction
    @Throws(LuaException::class)
    fun scan(radius: Double): MutableList<Any> {
        if (ValkyrienComputersConfig.SERVER.ComputerCraft.disableRadars) throw LuaException("Disabled")

        return scanForShips(level, pos, radius)
    }

    override fun getType(): String = "radar"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level.getBlockState(pos).`is`(ComputerCraftBlocks.RADAR.get())

    private fun scanForShips(level: Level?, position: BlockPos?, radius: Double): MutableList<Any> {
        var position = position
        val settings = ValkyrienComputersConfig.SERVER.ComputerCraft.RadarSettings

        if (level == null || position == null) return mutableListOf("booting")

        if (!level.isClientSide()) {
            // THROW EARLY RESULTS
            if (radius < 1.0) return mutableListOf("radius too small")
            if (radius > settings.maxRadarRadius) return mutableListOf("radius too big")

            // IF RADAR IS ON A SHIP, USE THE WORLD SPACE COORDINATES
            val test = level.getShipManagingPos(position)
            if (test != null) {
                val newPos = test.toWorldCoordinates(position)
                position = BlockPos(newPos.x, newPos.y, newPos.z)
            }

            // GET A LIST OF SHIP POSITIONS WITHIN RADIUS
            val ships = level.transformToNearbyShipsAndWorld(position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), radius)
            val results = mutableListOf<Any>()

            // TESTING FOR NO SHIPS
            if (ships.isEmpty())
                return mutableListOf("no ships")

            // Give results the ID, X, Y, and z of each Ship
            for (vec in ships) {
                val data = (level as ServerLevel).getShipManagingPos(vec.x, vec.y, vec.z)
                val pos = data!!.transform.positionInWorld
                val result = mutableMapOf<String, Any>()

                // Give Name
                if (settings.radarGetsName)
                    result["name"] = ShipIntegrationMethods.getNameFromShip(data)

                // Give ID
                if (settings.radarGetsId)
                    result["id"] = ShipIntegrationMethods.getIdFromShip(data)

                // Give Position
                if (settings.radarGetsPosition)
                    result["position"] = ShipIntegrationMethods.getPositionFromShip(data, false)

                // Give Mass
                if (settings.radarGetsMass)
                    result["mass"] = ShipIntegrationMethods.getMassFromShip(data)

                // Give Rotation
                if (settings.radarGetsRotation)
                    result["rotation"] = ShipIntegrationMethods.getRotationFromShip(data, true)

                // Give Velocity
                if (settings.radarGetsVelocity)
                    result["velocity"] = ShipIntegrationMethods.getVelocityFromShip(data)

                // Give Distance
                if (settings.radarGetsDistance)
                    result["distance"] = level.squaredDistanceBetweenInclShips(vec.x, vec.y, vec.z, pos.x(), pos.y(), pos.z())

                // Give Size
                if (settings.radarGetsSize) {
                    val aabb = data.shipAABB
                    result["size"] = mutableMapOf(
                            Pair("x", aabb!!.maxX() - aabb.minX()),
                            Pair("y", aabb.maxY() - aabb.minY()),
                            Pair("z", aabb.maxZ() - aabb.minZ())
                    )
                }

                results.add(result)
            }
            return results
        }
        return mutableListOf()
    }
}