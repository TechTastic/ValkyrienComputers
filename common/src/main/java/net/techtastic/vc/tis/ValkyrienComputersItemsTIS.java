package net.techtastic.vc.tis;

import dev.architectury.registry.DeferredRegister;
import dev.architectury.registry.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersConfig.Server.TIS;
import net.techtastic.vc.ValkyrienComputersMod;
import net.techtastic.vc.ValkyrienComputersTab;

public class ValkyrienComputersItemsTIS {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static RegistrySupplier<Item> RADAR;
    public static RegistrySupplier<Item> READER;

    public static RegistrySupplier<Item> registerItem(String name, Item item) {
        return ITEMS.register(new ResourceLocation(ValkyrienComputersMod.MOD_ID, name), () -> item);
    }

    public static void registerItems() {
        CreativeModeTab tab = ValkyrienComputersTab.tab;

        TIS config = ValkyrienComputersConfig.SERVER.getTIS3D();
        if (!config.getDisableRadars()) {
            RADAR = registerItem("radar_tis", new Item(new Item.Properties().tab(tab)));
        }
        if (!config.getDisableShipReaders()) {
            READER = registerItem("reader_tis", new Item(new Item.Properties().tab(tab)));
        }

        ITEMS.register();
    }
}
