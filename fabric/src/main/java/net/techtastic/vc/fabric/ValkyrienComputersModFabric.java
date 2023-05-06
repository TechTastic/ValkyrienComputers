package net.techtastic.vc.fabric;

import dev.architectury.platform.Platform;
import net.techtastic.vc.ValkyrienComputersMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.techtastic.vc.fabric.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import net.techtastic.vc.ValkyrienComputersConfig;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class ValkyrienComputersModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before vc
        new ValkyrienSkiesModFabric().onInitialize();

        ValkyrienComputersConfig.Server.COMPUTERCRAFT ccConfig = ValkyrienComputersConfig.SERVER.getComputerCraft();
        if (Platform.isModLoaded("computercraft") && !ccConfig.getDisableComputerCraft()) {
            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();

            // EUREKA IS CURRENTLY UNSUPPORTED

            //if (Platform.isModLoaded("vs_eureka") && !ccConfig.getDisableEurekaIntegration())
                //EurekaPeripheralProviders.registerPeripheralProviders();
        }

        ValkyrienComputersMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            ValkyrienComputersMod.initClient();
        }
    }

    public static class ModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return (parent) -> VSClothConfig.createConfigScreenFor(
                    parent,
                    VSConfigClass.Companion.getRegisteredConfig(ValkyrienComputersConfig.class)
            );
        }
    }
}
