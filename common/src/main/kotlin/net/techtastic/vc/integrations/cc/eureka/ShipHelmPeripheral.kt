package net.techtastic.vc.integrations.cc.eureka

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.getShipManagingPos


class ShipHelmPeripheral(private val world : Level, private val pos : BlockPos) : IPeripheral {
    override fun equals(p0: IPeripheral?): Boolean = (world.getBlockState(pos).block is ShipHelmBlock)

    override fun getType(): String = "ship_helm"

    @LuaFunction
    @Throws(LuaException::class)
    fun move(arg: IArguments) {
        val direction = arg.optString(0)
        val bool = arg.optBoolean(1, false)
        if (direction.isEmpty) throw LuaException("missing direction")

        if (world.isClientSide) return
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        var fakePlayer = ship.getAttachment(SeatedControllingPlayer::class.java)
        if (fakePlayer == null) {
            fakePlayer = SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
            ship.saveAttachment(SeatedControllingPlayer::class.java, fakePlayer)
        }

        when (direction.get()) {
            "forward" -> fakePlayer.forwardImpulse = if (bool) 1.0f else 0.0f
            "back" -> fakePlayer.forwardImpulse = if (bool) -1.0f else 0.0f
            "left" -> fakePlayer.leftImpulse = if (bool) 1.0f else 0.0f
            "right" -> fakePlayer.leftImpulse = if (bool) -1.0f else 0.0f
            "up" -> fakePlayer.upImpulse = if (bool) 1.0f else 0.0f
            "down" -> fakePlayer.upImpulse = if (bool) -1.0f else 0.0f
            else -> throw LuaException("invalid direction")
        }
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun resetAllMovement() {
        if (world.isClientSide) return
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        var fakePlayer = ship.getAttachment(SeatedControllingPlayer::class.java)
        if (fakePlayer == null) {
            fakePlayer = SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
            ship.saveAttachment(SeatedControllingPlayer::class.java, fakePlayer)
        }

        fakePlayer.forwardImpulse = 0.0f
        fakePlayer.leftImpulse = 0.0f
        fakePlayer.upImpulse = 0.0f
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun isCruising(): Boolean {
        if (world.isClientSide) return false
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        var fakePlayer = ship.getAttachment(SeatedControllingPlayer::class.java)
        if (fakePlayer == null) {
            fakePlayer = SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
            ship.saveAttachment(SeatedControllingPlayer::class.java, fakePlayer)
        }

        return fakePlayer.cruise
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun startCruising(): Boolean {
        if (world.isClientSide) return false
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        var fakePlayer = ship.getAttachment(SeatedControllingPlayer::class.java)
        if (fakePlayer == null) {
            fakePlayer = SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
            ship.saveAttachment(SeatedControllingPlayer::class.java, fakePlayer)
        }

        fakePlayer.cruise = true

        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun stopCruising(): Boolean {
        if (world.isClientSide) return false
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        var fakePlayer = ship.getAttachment(SeatedControllingPlayer::class.java)
        if (fakePlayer == null) {
            fakePlayer = SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
            ship.saveAttachment(SeatedControllingPlayer::class.java, fakePlayer)
        }

        fakePlayer.cruise = false

        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun startAlignment(): Boolean {
        if (world.isClientSide) return false
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        control.aligning = true
        ship.saveAttachment(EurekaShipControl::class.java, control)
        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun stopAlignment(): Boolean {
        if (world.isClientSide) return false
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        control.aligning = false
        ship.saveAttachment(EurekaShipControl::class.java, control)
        return true
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun disassemble(): Boolean {
        if (world.isClientSide) return false
        val ship : ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        val be : BlockEntity? = world.getBlockEntity(pos)
        if (be is ShipHelmBlockEntity) {
            be.disassemble();

            return true;
        } else {
            throw LuaException("no ship helm");
        }
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun assemble(): Boolean {
        if (world.isClientSide) return false
        val ship : ServerShip? = (world as ServerLevel).getShipManagingPos(pos)
        if (ship != null) throw LuaException("already assembled")

        val be : BlockEntity? = world.getBlockEntity(pos)
        if (be is ShipHelmBlockEntity) {
            be.assemble();

            return true;
        } else {
            throw LuaException("no ship helm");
        }
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun getBalloonAmount(): Int {
        if (world.isClientSide) return 0
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        return control.balloons
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun getAnchorAmount(): Int {
        if (world.isClientSide) return 0
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        return control.anchors
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun getActiveAnchorAmount(): Int {
        if (world.isClientSide) return 0
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        return control.anchorsActive
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun areAnchorsActive(): Boolean {
        if (world.isClientSide) return false
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        return control.anchorsActive > 0
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun getShipHelmAmount(): Int {
        if (world.isClientSide) return 0
        val ship: ServerShip = (world as ServerLevel).getShipManagingPos(pos) ?: throw LuaException("no ship")
        val control = ship.getAttachment(EurekaShipControl::class.java) ?: throw LuaException("not Eureka ship")

        return control.helms
    }
}