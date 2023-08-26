package io.github.techtastic.vc

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import net.minecraft.resources.ResourceLocation
import io.github.techtastic.vc.block.VCBlockEntities
import io.github.techtastic.vc.block.VCBlocks
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity
import io.github.techtastic.vc.block.entity.renderer.GyroscopicSensorBER
import io.github.techtastic.vc.item.VCItems
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer

object ValkyrienComputersMod {
    const val MOD_ID = "vc"

    @JvmStatic
    fun init() {
        VCBlocks.register()
        VCBlockEntities.register()
        VCItems.register()
    }

    @JvmStatic
    fun initClient() {
        //RenderTypeRegistry.register(RenderType.translucent(), VCBlocks.GYRO.get())

        BlockEntityRendererRegistry.register(VCBlockEntities.GYRO.get()) { GyroscopicSensorBER(it) }
    }

    val String.resource: ResourceLocation get() = ResourceLocation(MOD_ID, this)
}