package io.github.techtastic.vc.block.entity.renderer

import com.jozufozu.flywheel.core.model.ModelUtil
import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Quaternion
import io.github.techtastic.vc.VCPartials
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.util.MiscUtils
import io.github.techtastic.vc.util.ShipIntegrationMethods.convertQuaternionToRPY
import io.github.techtastic.vc.util.rendering.SuperByteBuffer
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.BakedModel
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
        if (sensor.level !is ClientLevel)
            return

        val level: ClientLevel = sensor.level as ClientLevel
        val pos: BlockPos = sensor.blockPos
        val state: BlockState = sensor.blockState
        val vb: VertexConsumer = buffer.getBuffer(RenderType.translucent())

        context.blockRenderDispatcher.renderSingleBlock(state, ps, buffer, light, overlay)

        // Put Rings and Core Partials here
        val core: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_CORE.get(), state).light(light)
        val inner: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_INNER.get(), state).light(light).rotateCentered(Direction.EAST, Math.toRadians(90.0).toFloat())
        val middle: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_MIDDLE.get(), state).light(light)
        val outer: SuperByteBuffer = MiscUtils.standardModelRender(VCPartials.GYRO_OUTER.get(), state).light(light).rotateCentered(Direction.SOUTH, Math.toRadians(90.0).toFloat())

        val ship: ClientShip? = level.getShipObjectManagingPos(pos)
        if (ship == null) {
            renderConstantRotation(sensor, ps, vb, core, inner, middle, outer)
        } else {
            renderShipRotation(ps, vb, core, inner, middle, outer, ship.transform.shipToWorldRotation)
        }
    }

    private fun renderConstantRotation(gyro: GyroscopicSensorBlockEntity, ps: PoseStack, vb: VertexConsumer, core: SuperByteBuffer,
                                       inner: SuperByteBuffer, middle: SuperByteBuffer, outer: SuperByteBuffer) {
        val angle = Math.toRadians(gyro.animAngle).toFloat()
        val direction = gyro.animDirection

        core.renderInto(ps, vb)
        inner.rotateCentered(direction, angle).renderInto(ps, vb)
        middle.rotateCentered(direction, angle).renderInto(ps, vb)
        outer.rotateCentered(direction, angle).renderInto(ps, vb)

        gyro.incrementAngle()
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
}