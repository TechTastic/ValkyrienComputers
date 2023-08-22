package io.github.techtastic.vc.block.entity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import io.github.techtastic.vc.block.entity.BaseSensorBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

open class BaseSensorBER<T: BaseSensorBlockEntity>(private val context: BlockEntityRendererProvider.Context): BlockEntityRenderer<T> {
    override fun render(sensor: T, partial: Float, ps: PoseStack, buffer: MultiBufferSource, light: Int, overlay: Int) {
    }
}