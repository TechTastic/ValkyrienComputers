package net.techtastic.vc.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.techtastic.vc.forge.integrations.cc.eureka.EurekaPeripheralProviders;
import net.techtastic.vc.forge.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import org.valkyrienskies.core.config.VSConfigClass;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersConfig.Server.COMPUTERCRAFT;
import net.techtastic.vc.ValkyrienComputersConfig.Server.OPENCOMPUTERS;
import net.techtastic.vc.ValkyrienComputersMod;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(
        ValkyrienComputersMod.MOD_ID
)
public class ValkyrienComputersModForge {
    boolean happendClientSetup = false;

    public ValkyrienComputersModForge() {
        // Submit our event bus to let architectury register our content on the right time

        EventBuses.registerModEventBus(ValkyrienComputersMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                () -> (Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(ValkyrienComputersConfig.class))
        );

        eventBus.addListener(this::clientSetup);

        ValkyrienComputersMod.init();

        LoadingModList mods = FMLLoader.getLoadingModList();
        COMPUTERCRAFT CC_Config = ValkyrienComputersConfig.SERVER.getComputerCraft();
        if (mods.getModFileById("computercraft") != null && !CC_Config.getDisableComputerCraft()) {
            // ComputerCraft is loaded and Integration is not disabled in the config

            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();

            if (mods.getModFileById("vs_eureka") != null) {
                // Eureka is loaded
                EurekaPeripheralProviders.registerPeripheralProviders();
            }
        }

        OPENCOMPUTERS OC_Config = ValkyrienComputersConfig.SERVER.getOpenComputers();
        if (mods.getModFileById("opencomputers") != null && !OC_Config.getDisableOpenComputers()) {
            // ComputerCraft is loaded and Integration is not disabled in the config
            if (mods.getModFileById("vs_eureka") != null && !CC_Config.getDisableEureka()) {
                // Eureka is loaded
            }
        }
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        ValkyrienComputersMod.initClient();

    }
}
