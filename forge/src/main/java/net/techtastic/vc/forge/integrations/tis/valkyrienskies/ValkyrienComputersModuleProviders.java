package net.techtastic.vc.forge.integrations.tis.valkyrienskies;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.ModuleProvider;
import li.cil.tis3d.common.item.ModuleItem;
import li.cil.tis3d.common.provider.module.SimpleModuleProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IRegistryDelegate;
import net.minecraftforge.registries.RegistryObject;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersMod;
import net.techtastic.vc.integrations.tis.TISItems;
import net.techtastic.vc.integrations.tis.valkyrienskies.AccelerometerSensorModule;
import net.techtastic.vc.integrations.tis.valkyrienskies.GyroscopicSensorModule;
import net.techtastic.vc.registry.DeferredRegister;
import net.techtastic.vc.registry.RegistrySupplier;

import java.util.function.BiFunction;

public class ValkyrienComputersModuleProviders {
    private static final DeferredRegister<ModuleProvider> MODULES = DeferredRegister.Companion.create(ValkyrienComputersMod.MOD_ID, ModuleProvider.REGISTRY);

    public static final RegistrySupplier<ModuleProvider> GYRO_MODULE = registerModule(
            ValkyrienComputersConfig.SERVER.getTIS3D().getDisableGyros(),
            TISItems.INSTANCE.getGYRO_MODULE().get().delegate,
            "gyro_module",
            GyroscopicSensorModule::new
    );

    public static final RegistrySupplier<ModuleProvider> ACCEL_MODULE = registerModule(
            ValkyrienComputersConfig.SERVER.getTIS3D().getDisableAccels(),
            TISItems.INSTANCE.getACCEL_MODULE().get().delegate,
            "accel_module",
            AccelerometerSensorModule::new
    );

    private static RegistrySupplier<ModuleProvider> registerModule(
            boolean disabled,
            IRegistryDelegate<Item> delegate,
            String name,
            BiFunction<Casing, Face, ? extends Module> function
    ) {
        return !disabled ? MODULES.register(name,
                () -> new SimpleModuleProvider<>(RegistryObject.of(delegate.name(),
                        delegate::type), function)) : null;
    }

    public static void register() {
        MODULES.applyAll();
    }
}
