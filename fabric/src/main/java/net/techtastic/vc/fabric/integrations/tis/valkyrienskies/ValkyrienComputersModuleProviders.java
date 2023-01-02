package net.techtastic.vc.fabric.integrations.tis.valkyrienskies;

import li.cil.tis3d.common.API;

public class ValkyrienComputersModuleProviders {
    public static void registerModuleProivders() {
        API.module.addProvider(new RadarModuleProvider());
        API.module.addProvider(new ShipReaderModuleProvider());
    }
}
