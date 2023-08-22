package io.github.techtastic.vc.util

import com.jozufozu.flywheel.core.model.ModelUtil
import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import io.github.techtastic.vc.util.rendering.SuperByteBuffer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.level.block.state.BlockState
import org.joml.Quaterniondc

object MiscUtils {
    fun convertQuaternion(original: Quaterniondc): Quaternion =
            Quaternion(original.x().toFloat(), original.y().toFloat(), original.z().toFloat(), original.w().toFloat())

    fun standardModelRender(model: BakedModel, referenceState: BlockState): SuperByteBuffer {
        val data: ShadeSeparatedBufferedData = ModelUtil.getBufferedData(model, referenceState, PoseStack())
        val sbb = SuperByteBuffer(data)
        data.release()
        return sbb
    }
}