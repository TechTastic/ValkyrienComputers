package io.github.techtastic.vc.fabric.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity;
import io.github.techtastic.vc.integrations.cc.valkyrienskies.GyroscopicSensorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
			if (be instanceof GyroscopicSensorBlockEntity gyro)
				return new GyroscopicSensorPeripheral(gyro);

			return null;
		}
	}
}
