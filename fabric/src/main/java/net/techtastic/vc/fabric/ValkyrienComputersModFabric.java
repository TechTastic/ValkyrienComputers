package net.techtastic.vc.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersConfig.Server.COMPUTERCRAFT;
import net.techtastic.vc.ValkyrienComputersMod;
import net.techtastic.vc.fabric.integrations.cc.eureka.EurekaPeripheralProviders;
import net.techtastic.vc.fabric.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;

public class ValkyrienComputersModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Class.forName("org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ValkyrienComputersMod.init();

        FabricLoader mods = FabricLoader.getInstance();
        COMPUTERCRAFT CC_Config = ValkyrienComputersConfig.SERVER.getComputerCraft();
        if (mods.isModLoaded("computercraft") && !CC_Config.getDisableComputerCraft()) {
            // ComputerCraft is loaded and Integration is not disabled in the config

            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();

            if (mods.isModLoaded("vs_eureka") && !CC_Config.getDisableEureka()) {
                // Eureka is loaded
                EurekaPeripheralProviders.registerPeripheralProviders();
            }
        }
    }
}