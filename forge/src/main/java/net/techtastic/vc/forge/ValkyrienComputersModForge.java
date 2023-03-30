package net.techtastic.vc.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.techtastic.vc.ValkyrienComputersMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.techtastic.vc.forge.integrations.cc.eureka.EurekaPeripheralProviders;
import net.techtastic.vc.forge.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import net.techtastic.vc.forge.integrations.tis.eureka.EurekaSerialInterfaceProviders;
import net.techtastic.vc.forge.integrations.tis.valkyrienskies.ValkyrienComputersModuleProviders;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import net.techtastic.vc.ValkyrienComputersConfig;
//import net.techtastic.vc.block.WoodType;
//import net.techtastic.vc.blockentity.renderer.ShipHelmBlockEntityRenderer;
//import net.techtastic.vc.blockentity.renderer.WheelModels;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(ValkyrienComputersMod.MOD_ID)
public class ValkyrienComputersModForge {
    boolean happendClientSetup = false;
    static IEventBus MOD_BUS;

    public ValkyrienComputersModForge() {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(ValkyrienComputersConfig.class)))
        );

        MOD_BUS.addListener(this::clientSetup);

        ValkyrienComputersMod.init();

        ModList mods = ModList.get();
        ValkyrienComputersConfig.Server.COMPUTERCRAFT ccConfig = ValkyrienComputersConfig.SERVER.getComputerCraft();
        if (mods.isLoaded("computercraft") && !ccConfig.getDisableComputerCraft()) {
            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();

            if (mods.isLoaded("vs_eureka") && !ccConfig.getDisableEurekaIntegration()) {
                EurekaPeripheralProviders.registerPeripheralProviders();
            }
        }

        ValkyrienComputersConfig.Server.TIS tisConfig = ValkyrienComputersConfig.SERVER.getTIS3D();
        if (ModList.get().isLoaded("tis3d") && !tisConfig.getDisableTIS3D()) {
            ValkyrienComputersModuleProviders.register();

            if (ModList.get().isLoaded("vs_eureka") && !tisConfig.getDisableEurekaIntegration()) {
                EurekaSerialInterfaceProviders.register();
            }
        }
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        ValkyrienComputersMod.initClient();
    }
}
