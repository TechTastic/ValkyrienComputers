package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.blockentity.UltrasonicSensorBlockEntity
import org.valkyrienskies.mod.common.getShipManagingPos

class UltrasonicSensorPeripheral(private val sensor : UltrasonicSensorBlockEntity) : IPeripheral {
    val level = sensor.level
    var pos: BlockPos = sensor.blockPos

    @LuaFunction
    fun getAlarmDistance(): Int = sensor.alarmDistance

    @LuaFunction
    fun setAlarmDistance(dist: Int) {
        if (ValkyrienComputersConfig.SERVER.ComputerCraft.disableSonic || !ValkyrienComputersConfig.SERVER.ComputerCraft.alarmDistanceChangable)
            throw LuaException("Disabled")

        if (dist > ValkyrienComputersConfig.SERVER.ComputerCraft.maxAlarmDistance) {
            sensor.alarmDistance = ValkyrienComputersConfig.SERVER.ComputerCraft.maxAlarmDistance
        } else {
            sensor.alarmDistance = dist
        }
        sensor.setChanged()
    }

    @LuaFunction
    fun clip(dist: Int): Pair<String, Pair<String, Double>> {
        if (level == null || level.isClientSide)
            throw LuaException("Clientside or Null")
        if (ValkyrienComputersConfig.SERVER.ComputerCraft.disableSonic)
            throw LuaException("Disabled")
        val level = level as ServerLevel
        if (dist > ValkyrienComputersConfig.SERVER.ComputerCraft.maxClipDistance)
            return sensor.getClip(level, pos, level.getShipManagingPos(pos), ValkyrienComputersConfig.SERVER.ComputerCraft.maxClipDistance)
                    ?: throw LuaException("Nothing to Hit")
        return sensor.getClip(level, pos, level.getShipManagingPos(pos), dist) ?: throw LuaException("Nothing to Hit")
    }

    override fun getType(): String = "ultrasonic"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level?.getBlockEntity(pos) is UltrasonicSensorBlockEntity

    override fun attach(computer: IComputerAccess) {
        if (!sensor.computers.contains(computer))
            sensor.computers.add(computer)
    }

    override fun detach(computer: IComputerAccess) {
        if (sensor.computers.contains(computer))
            sensor.computers.remove(computer)
    }
}