package io.github.techtastic.vc.forge.item;

import io.github.techtastic.vc.forge.item.renderer.GyroscopicSensorBEWLR;
import io.github.techtastic.vc.item.custom.GyroscopicSensorBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GyroscopicSensorBlockItemImpl extends GyroscopicSensorBlockItem {
    public GyroscopicSensorBlockItemImpl(@NotNull Block block, @NotNull Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new GyroscopicSensorBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            }
        });

        super.initializeClient(consumer);
    }
}
