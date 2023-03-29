package net.techtastic.vc.forge.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.blockentity.AccelerometerSensorBlockEntity;
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity;
import net.techtastic.vc.blockentity.MotorBlockEntity;
import net.techtastic.vc.blockentity.UltrasonicSensorBlockEntity;
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks;
import net.techtastic.vc.integrations.cc.valkyrienskies.*;
import org.jetbrains.annotations.NotNull;

public class ValkyrienComputersPeripheralProviders {
	public static void registerPeripheralProviders() {
		ComputerCraftAPI.registerPeripheralProvider(new ValkyrienComputersPeripheralProivder());
	}

	public static class ValkyrienComputersPeripheralProivder implements IPeripheralProvider {
		@Override
		public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
			BlockState state = level.getBlockState(blockPos);
			BlockEntity be = level.getBlockEntity(blockPos);

			if (ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableComputerCraft()) return LazyOptional.empty();

			if (state.is(ComputerCraftBlocks.READER.get()) &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableShipReaders()) {
				return LazyOptional.of(() -> new ShipReaderPeripheral(level, blockPos));
			} else if (state.is(ComputerCraftBlocks.RADAR.get()) &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableRadars()) {
				return LazyOptional.of(() -> new RadarPeripheral(level, blockPos));
			} else if (be instanceof MotorBlockEntity motor) {
				return LazyOptional.of(() -> new MotorPeripheral(motor));
			} else if (be instanceof GyroscopicSensorBlockEntity sensor &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableGyros()) {
				return LazyOptional.of(() -> new GyroscopicSensorPeripheral(sensor));
			} else if (be instanceof AccelerometerSensorBlockEntity sensor &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableAccels()) {
				return LazyOptional.of(() -> new AccelerometerSensorPeripheral(sensor));
			} else if (be instanceof UltrasonicSensorBlockEntity sensor &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableSonic()) {
				return LazyOptional.of(() -> new UltrasonicSensorPeripheral(sensor));
			}

			return LazyOptional.empty();
		}
	}
}
