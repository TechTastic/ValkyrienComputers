package net.techtastic.vc.fabric.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.blockentity.AccelerometerSensorBlockEntity;
import net.techtastic.vc.blockentity.GyroscopicSensorBlockEntity;
import net.techtastic.vc.blockentity.MotorBlockEntity;
import net.techtastic.vc.blockentity.UltrasonicSensorBlockEntity;
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks;
import net.techtastic.vc.integrations.cc.valkyrienskies.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ValkyrienComputersPeripheralProviders {
	public static void registerPeripheralProviders() {
		ComputerCraftAPI.registerPeripheralProvider(new ValkyrienComputersPeripheralProivder());
	}

	public static class ValkyrienComputersPeripheralProivder implements IPeripheralProvider {
		@Nullable
		@Override
		public IPeripheral getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
			BlockState state = level.getBlockState(blockPos);
			BlockEntity be = level.getBlockEntity(blockPos);
			if (ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableComputerCraft()) return null;

			if (state.is(ComputerCraftBlocks.READER.get()) &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableShipReaders()) {
				return new ShipReaderPeripheral(level, blockPos);
			} else if (state.is(ComputerCraftBlocks.RADAR.get()) &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableRadars()) {
				return new RadarPeripheral(level, blockPos);
			} else if (be instanceof MotorBlockEntity motor) {
				return new MotorPeripheral(motor);
			} else if (be instanceof GyroscopicSensorBlockEntity sensor &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableGyros()) {
				return new GyroscopicSensorPeripheral(sensor);
			} else if (be instanceof AccelerometerSensorBlockEntity sensor &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableAccels()) {
				return new AccelerometerSensorPeripheral(sensor);
			} else if (be instanceof UltrasonicSensorBlockEntity sensor &&
					!ValkyrienComputersConfig.SERVER.getComputerCraft().getDisableSonic()) {
				return new UltrasonicSensorPeripheral(sensor);
			}

			return null;
		}
	}
}
