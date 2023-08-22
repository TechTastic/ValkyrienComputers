package io.github.techtastic.vc.util.rendering

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.techtastic.vc.VCPartials
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.util.MiscUtils
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.core.Direction
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import kotlin.random.Random
import kotlin.random.asJavaRandom

object GyroscopicSensorItemRendering {
    fun render(stack: ItemStack, mode: ItemTransforms.TransformType, ps: PoseStack, buffer: MultiBufferSource, light: Int, overlay: Int) {
        val state = (stack.item as BlockItem).block.defaultBlockState()
        val vb: VertexConsumer = buffer.getBuffer(RenderType.translucent())
        val tag = stack.orCreateTag
        var angle = Math.toRadians(
                if (tag.contains("vc\$animAngle"))
                    tag.getDouble("vc\$animAngle")
                else 0.0).toFloat()
        var direction =
                if (tag.contains("vc\$direction"))
                    Direction.valueOf(tag.getString("vc\$direction"))
                else
                    Direction.NORTH

        MiscUtils.standardModelRender(VCPartials.GYRO_CORE.get(), state).light(light).renderInto(ps, vb)

        MiscUtils.standardModelRender(VCPartials.GYRO_INNER.get(), state).light(light).rotateCentered(Direction.EAST, Math.toRadians(90.0).toFloat())
                .rotateCentered(direction, angle).renderInto(ps, vb)

        MiscUtils.standardModelRender(VCPartials.GYRO_MIDDLE.get(), state).light(light).rotateCentered(direction, angle).renderInto(ps, vb)

        MiscUtils.standardModelRender(VCPartials.GYRO_OUTER.get(), state).light(light).rotateCentered(Direction.SOUTH, Math.toRadians(90.0).toFloat())
                .rotateCentered(direction, angle).renderInto(ps, vb)

        if (angle >= 359) {
            angle = 0.0f
            direction = Direction.values()[Random.asJavaRandom().nextInt(0, Direction.values().size - 1)]
        } else
            angle += 1

        tag.putDouble("vc\$animAngle", angle.toDouble())
        tag.putString("vc\$direction", direction.name)
    }
}