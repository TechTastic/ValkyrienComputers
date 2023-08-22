package io.github.techtastic.vc.fabric.services;

import io.github.techtastic.vc.item.custom.GyroscopicSensorBlockItem;
import io.github.techtastic.vc.services.ValkyrienComputersPlatformHelper;
import kotlin.jvm.functions.Function0;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import io.github.techtastic.vc.services.ValkyrienComputersPlatformHelper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ValkyrienComputersPlatformHelperFabric implements ValkyrienComputersPlatformHelper {
    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Function0<ItemStack> stack) {
        return FabricItemGroupBuilder.build(id, stack::invoke);
    }

    @NotNull
    @Override
    public GyroscopicSensorBlockItem newGyroscopicSensorBlockItem(@NotNull Block block, @NotNull Item.Properties properties) {
        return new GyroscopicSensorBlockItem(block, properties);
    }
}
