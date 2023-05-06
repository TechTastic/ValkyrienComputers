package net.techtastic.vc.forge;

import dev.architectury.platform.Platform;
import net.techtastic.vc.ValkyrienComputersMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.techtastic.vc.forge.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import net.techtastic.vc.forge.integrations.tis.valkyrienskies.ValkyrienComputersModuleProviders;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import net.techtastic.vc.ValkyrienComputersConfig;
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

        ValkyrienComputersConfig.Server.COMPUTERCRAFT ccConfig = ValkyrienComputersConfig.SERVER.getComputerCraft();
        if (Platform.isModLoaded("computercraft") && !ccConfig.getDisableComputerCraft()) {
            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();

            // EUREKA IS CURRENTLY UNSUPPORTED

            //if (Platform.isModLoaded("vs_eureka") && !ccConfig.getDisableEurekaIntegration())
                //EurekaPeripheralProviders.registerPeripheralProviders();
        }

        ValkyrienComputersConfig.Server.TIS tisConfig = ValkyrienComputersConfig.SERVER.getTIS3D();
        if (Platform.isModLoaded("tis3d") && !tisConfig.getDisableTIS3D()) {
            ValkyrienComputersModuleProviders.register();

            // EUREKA IS CURRENTLY UNSUPPORTED

            //if (Platform.isModLoaded("vs_eureka") && !tisConfig.getDisableEurekaIntegration())
                //EurekaSerialInterfaceProviders.register();
        }
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        ValkyrienComputersMod.initClient();
    }
}
