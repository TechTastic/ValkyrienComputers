package io.github.techtastic.vc.fabric.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.techtastic.vc.util.rendering.GyroscopicSensorItemRendering;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

public class GyroscopicSensorDIR implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
        GyroscopicSensorItemRendering.INSTANCE.render(stack, mode, ps, buffer, light, overlay);
    }
}
