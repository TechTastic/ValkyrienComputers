package net.techtastic.vc.fabric.integrations.tis.valkyrienskies;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.module.Module;
import li.cil.tis3d.api.module.ModuleProvider;
import net.minecraft.world.item.ItemStack;
import net.techtastic.vc.tis.ValkyrienComputersItemsTIS;
import org.jetbrains.annotations.Nullable;

public class RadarModuleProvider implements ModuleProvider {
    @Override
    public boolean worksWith(ItemStack itemStack, Casing casing, Face face) {
        if (ValkyrienComputersItemsTIS.RADAR.isPresent()) {
            return itemStack.getItem().equals(ValkyrienComputersItemsTIS.RADAR.get());
        }
        return false;
    }

    @Nullable
    @Override
    public Module createModule(ItemStack itemStack, Casing casing, Face face) {
        return new RadarModule(casing, face);
    }
}
