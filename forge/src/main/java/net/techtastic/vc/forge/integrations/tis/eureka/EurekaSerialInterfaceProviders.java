package net.techtastic.vc.forge.integrations.tis.eureka;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.tis3d.api.serial.SerialInterfaceProvider;
import net.techtastic.vc.ValkyrienComputersMod;

public class EurekaSerialInterfaceProviders {
    private static final DeferredRegister<SerialInterfaceProvider> SERIALS = DeferredRegister.create(ValkyrienComputersMod.MOD_ID, SerialInterfaceProvider.REGISTRY);

    public static final RegistrySupplier<SerialInterfaceProvider> SHIP_HELM_SERIAL = SERIALS.register("ship_helm_tis", ShipHelmSerialInterfaceProvider::new);

    public static void register() {
        SERIALS.register();
    }
}
