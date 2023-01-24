package net.techtastic.vc.forge.integrations.cc.eureka;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.techtastic.vc.cc.eureka.ShipHelmPeripheral;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.block.ShipHelmBlock;

public class ShipHelmPeripheralProvider implements IPeripheralProvider {
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        if (
                level.getBlockState(blockPos).getBlock() instanceof ShipHelmBlock
        ) {
            return LazyOptional.of(() -> new ShipHelmPeripheral(level, blockPos));
        }
        return LazyOptional.empty();
    }
}
