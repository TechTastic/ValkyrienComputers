package net.techtastic.vc.forge.integrations.tis.valkyrienskies;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.tis3d.api.module.ModuleProvider;
import li.cil.tis3d.common.provider.module.SimpleModuleProvider;
import net.minecraftforge.registries.RegistryObject;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersMod;
import net.techtastic.vc.integrations.tis.TISItems;
import net.techtastic.vc.integrations.tis.valkyrienskies.GyroscopicSensorModule;

import java.util.HashMap;

public class ValkyrienComputersModuleProviders {
    private static final DeferredRegister<ModuleProvider> MODULES = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, ModuleProvider.REGISTRY);

    public static final RegistrySupplier<ModuleProvider> GYRO_MODULE =
            ValkyrienComputersConfig.SERVER.getTIS3D().getDisableGyros() && TISItems.INSTANCE.getGYRO_MODULE() != null ?
                    MODULES.register("gyro_tis",
                            () -> new SimpleModuleProvider<>(RegistryObject.of(TISItems.INSTANCE.getGYRO_MODULE().get().getRegistryName(),
                                    () -> TISItems.INSTANCE.getGYRO_MODULE().get().getRegistryType()), GyroscopicSensorModule::new)) : null;

    public static void register() {
        MODULES.register();
    }
}
