package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks
import net.techtastic.vc.util.SpecialLuaTables.Companion.getQuaternionAsTable
import net.techtastic.vc.util.SpecialLuaTables.Companion.getVectorAsTable
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.math.asin
import kotlin.math.atan2

class ShipReaderPeripheral(private val level: Level, private val pos: BlockPos) : IPeripheral {
    /*@LuaFunction
    public final String getShipName() throws LuaException {
        ServerShip ship = isEnabledAndOnShip(pos);
        if (ship != null) {
            return ship.getName();
        } else {
            throw new LuaException("Not on a Ship");
        }
    }

    @LuaFunction
    public final boolean setShipName(String string) throws LuaException {
        ServerShip ship = isEnabledAndOnShip(pos);
        if (ship != null) {
            ship.setName(string);
            return true;
        } else {
            throw new LuaException("Not on a Ship");
        }
    }*/
    @Throws(LuaException::class)
    @LuaFunction
    fun getShipID() : Long = isEnabledAndOnShip(pos)?.id ?: throw LuaException("Not on a Ship")

    @Throws(LuaException::class)
    @LuaFunction
    fun getMass() : Double = isEnabledAndOnShip(pos)?.inertiaData?.mass ?: throw LuaException("Not on a Ship")

    @Throws(LuaException::class)
    @LuaFunction
    fun getVelocity() : MutableMap<Any, Any> {
        val vel = isEnabledAndOnShip(pos)?.velocity ?: throw LuaException("Not on a Ship")
        return getVectorAsTable(vel)
    }

    @Throws(LuaException::class)
    @LuaFunction
    fun getWorldspacePosition() : MutableMap<Any, Any> {
        val position = isEnabledAndOnShip(pos)?.transform?.positionInWorld ?: throw LuaException("Not on a Ship")
        return getVectorAsTable(position)
    }

    @Throws(LuaException::class)
    @LuaFunction
    fun getShipyardPosition() : MutableMap<Any, Any> {
        val position = isEnabledAndOnShip(pos)?.transform?.positionInShip ?: throw LuaException("Not on a Ship")
        return getVectorAsTable(position)
    }

    @Throws(LuaException::class)
    @LuaFunction
    fun getScale() : MutableMap<Any, Any> {
        val scaling = isEnabledAndOnShip(pos)?.transform?.shipToWorldScaling ?: throw LuaException("Not on a Ship")
        return getVectorAsTable(scaling)
    }

    @Throws(LuaException::class)
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
    @Throws(LuaException::class)
    fun getRotation(arg: IArguments): MutableMap<Any, Any> {
        val isQuaternion = arg.optBoolean(0, false)
        val ship = isEnabledAndOnShip(pos)

        val quat = isEnabledAndOnShip(pos)?.transform?.shipToWorldRotation ?: throw LuaException("Not on a Ship")
        if (isQuaternion) return getQuaternionAsTable(quat)

        val x = quat.x()
        val y = quat.y()
        val z = quat.z()
        val w = quat.w()
        return mutableMapOf(
                Pair("roll", atan2(2 * y * w - 2 * x * z, 1 - 2 * y * y - 2 * z * z)),
                Pair("pitch", atan2(2 * x * w - 2 * y * z, 1 - 2 * x * x - 2 * z * z)),
                Pair("yaw", asin(2 * x * y + 2 * z * w))
        )
    }

    @LuaFunction
    @Throws(LuaException::class)
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
    @Throws(LuaException::class)
    fun transformDirection(x: Double, y: Double, z: Double): MutableMap<Any, Any> {
        val ship = isEnabledAndOnShip(BlockPos(x, y, z))
        return if (ship != null) {
            getVectorAsTable(ship.shipToWorld.transformDirection(Vector3d(x, y, z)))
        } else {
            val matrix = isEnabledAndOnShip(pos)?.worldToShip ?: throw LuaException("Not on a Ship")
            getVectorAsTable(matrix.transformDirection(Vector3d(x, y, z)))
        }
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