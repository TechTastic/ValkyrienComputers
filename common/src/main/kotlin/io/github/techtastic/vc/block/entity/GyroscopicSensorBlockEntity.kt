package io.github.techtastic.vc.block.entity

import io.github.techtastic.vc.block.VCBlockEntities
import io.github.techtastic.vc.util.ShipIntegrationMethods
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.random.Random
import kotlin.random.asJavaRandom

class GyroscopicSensorBlockEntity(pos: BlockPos, state: BlockState): BaseSensorBlockEntity(VCBlockEntities.GYRO.get(), pos, state) {
    private var previousQuaternion: Quaterniondc? = null
    var testAngle: Double = Math.toRadians(0.5)
    var animAngle: Double = 0.0
    var animDirection: Direction = Direction.NORTH

    override fun <T : BlockEntity?> tick(level: Level, pos: BlockPos, state: BlockState, be: T) {
        super.tick(level, pos, state, be)

        if (level.isClientSide)
            return

        val level = level as ServerLevel

        val ship = level.getShipManagingPos(pos) ?: return

        val quat = ship.transform.shipToWorldRotation
        if (previousQuaternion != null && !hasSignificantlyChanged(previousQuaternion!!, quat))
            return

        this.queueCCEvent("rotation", ShipIntegrationMethods.getRotationAsQuaternion(ship)!!)

        previousQuaternion = quat
    }
    
    private fun hasSignificantlyChanged(firstQuat: Quaterniondc, secondQuat: Quaterniondc): Boolean {
        val firstRPY = ShipIntegrationMethods.convertQuaternionToRPY(firstQuat)
        val secondRPY = ShipIntegrationMethods.convertQuaternionToRPY(secondQuat)
        return secondRPY.x > firstRPY.x + testAngle || secondRPY.x < firstRPY.x - testAngle || 
                secondRPY.y > firstRPY.y + testAngle || secondRPY.y < firstRPY.y - testAngle || 
                secondRPY.z > firstRPY.z + testAngle || secondRPY.z < firstRPY.z - testAngle;
    }

    override fun saveAdditional(tag: CompoundTag) {
        if (previousQuaternion != null) {
            tag.putDouble("vc\$quaternionX", previousQuaternion!!.x())
            tag.putDouble("vc\$quaternionY", previousQuaternion!!.y())
            tag.putDouble("vc\$quaternionZ", previousQuaternion!!.z())
            tag.putDouble("vc\$quaternionW", previousQuaternion!!.w())
        }
        tag.putDouble("vc\$testAngle", testAngle)

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        if (tag.contains("vc\$quaternionX"))
            previousQuaternion = Quaterniond(
                    tag.getDouble("vc\$quaternionX"),
                    tag.getDouble("vc\$quaternionY"),
                    tag.getDouble("vc\$quaternionZ"),
                    tag.getDouble("vc\$quaternionW")
            )

        testAngle = tag.getDouble("vc\$testAngle")
    }

    fun incrementAngle() {
        if (animAngle >= 359) {
            animAngle = 0.0
            changeDirection()
        } else
            animAngle += 1
    }

    private fun changeDirection() {
        val random = Random.asJavaRandom()
        when (animDirection) {
            Direction.UP, Direction.DOWN ->
                animDirection =
                        if (random.nextFloat() > 0.5)
                            Direction.NORTH
                        else
                            Direction.WEST
            Direction.SOUTH, Direction.NORTH ->
                animDirection =
                        if (random.nextFloat() > 0.5)
                            Direction.WEST
                        else
                            Direction.DOWN
            Direction.WEST, Direction.EAST ->
                animDirection =
                        if (random.nextFloat() > 0.5)
                            Direction.DOWN
                        else
                            Direction.NORTH
        }
    }
}