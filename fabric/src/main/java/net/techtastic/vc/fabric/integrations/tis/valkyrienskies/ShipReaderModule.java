package net.techtastic.vc.fabric.integrations.tis.valkyrienskies;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.prefab.module.AbstractModuleWithRotation;

public class ShipReaderModule extends AbstractModuleWithRotation {
    public ShipReaderModule(Casing casing, Face face) {
        super(casing, face);
    }
}
