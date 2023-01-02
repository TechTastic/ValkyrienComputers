package net.techtastic.vc.forge.integrations.cc.valkyrienskies;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.cc.ValkyrienComputersBlocksCC;
import org.jetbrains.annotations.NotNull;

public class ShipReaderPeripheralProvider implements IPeripheralProvider {
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        if (
                level.getBlockState(blockPos).getBlock().equals(ValkyrienComputersBlocksCC.READER.get()) &&
                        !ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableShipReaders()
        ) {
            return LazyOptional.of(() -> new ShipReaderPeripheral(level, blockPos));
        }
        return LazyOptional.empty();
    }
}
