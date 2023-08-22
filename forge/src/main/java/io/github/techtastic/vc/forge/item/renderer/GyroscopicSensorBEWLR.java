package io.github.techtastic.vc.forge.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.techtastic.vc.util.rendering.GyroscopicSensorItemRendering;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GyroscopicSensorBEWLR extends BlockEntityWithoutLevelRenderer {
    public GyroscopicSensorBEWLR(BlockEntityRenderDispatcher arg, EntityModelSet arg2) {
        super(arg, arg2);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, ItemTransforms.@NotNull TransformType mode, @NotNull PoseStack ps, @NotNull MultiBufferSource buffer, int light, int overlay) {
        GyroscopicSensorItemRendering.INSTANCE.render(stack, mode, ps, buffer, light, overlay);
    }
}
