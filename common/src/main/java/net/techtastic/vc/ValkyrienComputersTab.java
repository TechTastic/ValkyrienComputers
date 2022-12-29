package net.techtastic.vc;

import dev.architectury.registry.CreativeTabs;
import dev.architectury.registry.DeferredRegister;
import dev.architectury.registry.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ValkyrienComputersTab {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static RegistrySupplier<Item> LOGO = ITEMS.register(
            new ResourceLocation(ValkyrienComputersMod.MOD_ID, "vc_logo"),
            () -> new Item(new Item.Properties())
    );

    public static CreativeModeTab tab;

    public static void registerTab() {
        ITEMS.register();

        tab = CreativeTabs.create(
                new ResourceLocation(ValkyrienComputersMod.MOD_ID, "vc_tab"),
                () -> new ItemStack(LOGO.get())
        );
    }
}
