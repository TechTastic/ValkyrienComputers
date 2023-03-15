package net.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.techtastic.vc.blockentity.MotorBlockEntity
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks

class MotorPeripheral(private val motor: MotorBlockEntity) : IPeripheral {
    private val level: Level? = motor.level
    private val pos: BlockPos = motor.blockPos

    @LuaFunction
    @Throws(LuaException::class)
    fun activate() : Boolean {
        if (level!!.isClientSide()) return false
        doesMotorStillExist()
        if (motor.activated) {
            motor.activated = false
            return true
        }
        motor.activated = true
        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun reverse() : Boolean {
        if (level!!.isClientSide()) return false
        doesMotorStillExist()
        if (motor.reversed) {
            motor.reversed = false
            return true
        }
        motor.reversed = true
        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun removeHead() : Boolean {
        if (level!!.isClientSide()) return false
        if (motor.hingeId == null) return false
        doesMotorStillExist()
        motor.destroyConstraints()
        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun createHead() : Boolean {
        if (level!!.isClientSide()) return false
        doesMotorStillExist()
        motor.makeOrGetTop((level as ServerLevel?)!!, pos)
        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun hasHead() : Boolean {
        if (level!!.isClientSide()) return false
        doesMotorStillExist()
        return motor.attachmentConstraintId != null && motor.hingeId != null
    }

    @Throws(LuaException::class)
    @LuaFunction
    fun getAngle() : Double {
        if (level!!.isClientSide()) return 0.0
        doesMotorStillExist()
        return motor.currentAngle
    }

    override fun getType(): String = "motor"

    override fun equals(iPeripheral: IPeripheral?): Boolean = level!!.getBlockState(pos).`is`(ComputerCraftBlocks.MOTOR.get())

    private fun doesMotorStillExist() = motor.setChanged()
}