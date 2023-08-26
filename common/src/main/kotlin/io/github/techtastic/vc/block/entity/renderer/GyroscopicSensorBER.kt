package io.github.techtastic.vc.block.entity.renderer

import com.jozufozu.flywheel.util.Color
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.techtastic.vc.VCPartials
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.util.MiscUtils
import io.github.techtastic.vc.util.ShipIntegrationMethods.convertQuaternionToRPY
import io.github.techtastic.vc.util.VCProperties
import io.github.techtastic.vc.util.VCProperties.OutputAxis
import io.github.techtastic.vc.util.rendering.SuperByteBuffer
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import kotlin.random.Random
import kotlin.random.asJavaRandom


class GyroscopicSensorBER(private val context: BlockEntityRendererProvider.Context): BaseSensorBER<GyroscopicSensorBlockEntity>(context) {
    override fun render(sensor: GyroscopicSensorBlockEntity, partial: Float, ps: PoseStack, buffer: MultiBufferSource, light: Int, overlay: Int) {
        val level: ClientLevel = sensor.level as ClientLevel
        val pos: BlockPos = sensor.blockPos
        val state: BlockState = sensor.blockState
        val axis: OutputAxis = state.getValue(VCProperties.OUTPUT)
        val vb: VertexConsumer = buffer.getBuffer(RenderType.translucent())

        // Put Rings and Core Partials here
        val core: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_CORE.get(), state).light(light)
        val inner: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_INNER.get(), state).light(light).rotateCentered(Direction.EAST, Math.toRadians(90.0).toFloat())
        val middle: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_MIDDLE.get(), state).light(light)
        val outer: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_OUTER.get(), state).light(light).rotateCentered(Direction.SOUTH, Math.toRadians(90.0).toFloat())

        when (axis) {
            OutputAxis.X -> outer.color(Color.RED)
            OutputAxis.Y -> middle.color(Color.GREEN)
            OutputAxis.Z -> inner.color(Color.BLACK)
        }

        val ship: ClientShip? = level.getShipObjectManagingPos(pos)
        if (ship == null)
            renderConstantRotation(sensor, ps, vb, core, inner, middle, outer)
        else
            renderShipRotation(ps, vb, core, inner, middle, outer, ship.transform.shipToWorldRotation)
    }

    private fun renderConstantRotation(gyro: GyroscopicSensorBlockEntity, ps: PoseStack, vb: VertexConsumer, core: SuperByteBuffer,
                                       inner: SuperByteBuffer, middle: SuperByteBuffer, outer: SuperByteBuffer) {
        core.renderInto(ps, vb)
        animateRing(inner, gyro.getInnerRingAnimation()).renderInto(ps, vb)
        animateRing(middle, gyro.getMiddleRingAnimation()).renderInto(ps, vb)
        animateRing(outer, gyro.getOuterRingAnimation()).renderInto(ps, vb)
    }

    private fun renderShipRotation(ps: PoseStack, vb: VertexConsumer, core: SuperByteBuffer, inner: SuperByteBuffer, middle: SuperByteBuffer,
                                   outer: SuperByteBuffer, quaternion: Quaterniondc) {
        val inverse: Quaterniond = quaternion.conjugate(Quaterniond())
        val rpy: Vector3d = convertQuaternionToRPY(inverse)

        core.rotateCentered(inverse).renderInto(ps, vb)
        outer.rotateCentered(MiscUtils.convertQuaternion(inverse)).rotateCentered(Direction.WEST, rpy.x.toFloat()).renderInto(ps, vb)
        middle.rotateCentered(MiscUtils.convertQuaternion(inverse)).rotateCentered(Direction.UP, rpy.y.toFloat()).renderInto(ps, vb)
        inner.rotateCentered(MiscUtils.convertQuaternion(inverse)).rotateCentered(Direction.NORTH, rpy.z.toFloat()).renderInto(ps, vb)
    }

    private fun animateRing(original: SuperByteBuffer, anim: Triple<Float, Direction, Direction>): SuperByteBuffer {
        return original.rotateCentered(anim.second, anim.first).rotateCentered(anim.third, anim.first)
    }
}