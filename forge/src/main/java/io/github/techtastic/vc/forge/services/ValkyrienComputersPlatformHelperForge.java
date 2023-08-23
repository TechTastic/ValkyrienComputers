package io.github.techtastic.vc.forge.services;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.item.forge.ItemPropertiesRegistryImpl;
import io.github.techtastic.vc.forge.item.GyroscopicSensorBlockItemImpl;
import io.github.techtastic.vc.item.custom.GyroscopicSensorBlockItem;
import kotlin.jvm.functions.Function0;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import io.github.techtastic.vc.services.ValkyrienComputersPlatformHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.jetbrains.annotations.NotNull;

public class ValkyrienComputersPlatformHelperForge implements ValkyrienComputersPlatformHelper {
    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Function0<ItemStack> stack) {
        return new CreativeModeTab(id.toString()) {
            @Override
            public ItemStack makeIcon() {
                return stack.invoke();
            }

            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("itemGroup." + String.format("%s.%s", id.getNamespace(), id.getPath()));
            }
        };
    }
}
