package net.techtastic.vc.forge.integrations.tis.valkyrienskies;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.tis3d.api.module.ModuleProvider;
import li.cil.tis3d.common.provider.module.SimpleModuleProvider;
import net.minecraftforge.registries.RegistryObject;
import net.techtastic.vc.ValkyrienComputersMod;
import net.techtastic.vc.integrations.tis.TISItems;
import net.techtastic.vc.integrations.tis.valkyrienskies.GyroscopicSensorModule;

public class ValkyrienComputersModuleProviders {
    private static final DeferredRegister<ModuleProvider> MODULES = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, ModuleProvider.REGISTRY);

    public static final RegistrySupplier<ModuleProvider> GYRO_MODULE = MODULES.register("gyro_tis",
            () -> new SimpleModuleProvider<>(RegistryObject.of(TISItems.INSTANCE.getGYRO_MODULE().get().getRegistryName(),
                    () -> TISItems.INSTANCE.getGYRO_MODULE().get().getRegistryType()), GyroscopicSensorModule::new));

    public void registerModuleProviders() {
        MODULES.register();
    }
}
