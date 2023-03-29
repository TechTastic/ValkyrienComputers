package net.techtastic.vc.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.squaredDistanceBetweenInclShips
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips

class UltrasonicSensorBlockEntity(pos: BlockPos, state: BlockState) : BaseSensorBlockEntity(ComputerCraftBlockEntities.SONIC.get(), pos, state) {
    var alarmDistance = ValkyrienComputersConfig.SERVER.ComputerCraft.defaultAlarmDistance

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("ValkyrienComputers\$alarmDistance", alarmDistance)

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        alarmDistance = tag.getInt("ValkyrienComputers\$alarmDistance")
    }

    override fun <T : BlockEntity?> tick(level: Level, pos: BlockPos, state: BlockState, be: T) {
        if (level.isClientSide || ValkyrienComputersConfig.SERVER.ComputerCraft.disableGyros) return
        val level = level as ServerLevel

        if (events.isEmpty()) return;

        for(computer in computers) {
            computer.queueEvent(events.keys.last(), events.values.last())
        }

        events.clear()
        queueEvent(level, pos, level.getShipManagingPos(pos))
    }

    override fun queueEvent(level: Level, pos: BlockPos, ship: ServerShip?) {
        events["hit"] = getClip(level, pos, ship, alarmDistance)
    }

    fun getClip(level: Level, pos: BlockPos, ship: ServerShip?, distance: Int): Pair<String, Pair<String, Double>>? {
        val state = level.getBlockState(pos)
        val direction = state.getValue(BlockStateProperties.FACING)

        val result: HitResult = level.clipIncludeShips(ClipContext(
                pos.toJOMLD().toMinecraft(),
                pos.relative(direction, distance).toJOMLD().toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                null),
                true,
                ship?.id
        )

        if (result.type.equals(HitResult.Type.MISS)) return null

        val dist = Pair("distance", level.squaredDistanceBetweenInclShips(
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                result.location.x, result.location.y, result.location.z)
        )

        return if (result.type.equals(HitResult.Type.BLOCK)) {
            Pair("block", dist)
        } else if (result.type.equals(HitResult.Type.ENTITY)) {
            Pair("entity", dist)
        } else {
            Pair("something", dist)
        }
    }
}