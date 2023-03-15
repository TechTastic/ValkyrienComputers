package net.techtastic.vc.forge.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.blockentity.MotorBlockEntity;
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks;
import net.techtastic.vc.integrations.cc.valkyrienskies.MotorPeripheral;
import net.techtastic.vc.integrations.cc.valkyrienskies.RadarPeripheral;
import net.techtastic.vc.integrations.cc.valkyrienskies.ShipReaderPeripheral;
import org.jetbrains.annotations.NotNull;

public class ValkyrienComputersPeripheralProviders {
	public static void registerPeripheralProviders() {
		ComputerCraftAPI.registerPeripheralProvider(new ValkyrienComputersPeripheralProivder());
	}

	public static class ValkyrienComputersPeripheralProivder implements IPeripheralProvider {
		@Override
		public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
			if (level.getBlockState(blockPos).is(ComputerCraftBlocks.READER.get()) &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableShipReaders()) {
				return LazyOptional.of(() -> new ShipReaderPeripheral(level, blockPos));
			} else if (level.getBlockState(blockPos).is(ComputerCraftBlocks.RADAR.get()) &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableRadars()) {
				return LazyOptional.of(() -> new RadarPeripheral(level, blockPos));
			} else if (level.getBlockEntity(blockPos) instanceof MotorBlockEntity motor) {
				return LazyOptional.of(() -> new MotorPeripheral(motor));
			}

			return LazyOptional.empty();
		}
	}
}
