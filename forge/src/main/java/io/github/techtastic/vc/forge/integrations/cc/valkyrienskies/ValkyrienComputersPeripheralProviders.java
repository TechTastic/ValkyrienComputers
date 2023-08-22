package io.github.techtastic.vc.forge.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import io.github.techtastic.vc.integrations.cc.valkyrienskies.GyroscopicSensorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import io.github.techtastic.vc.block.entity.GyroscopicSensorBlockEntity;
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
			if (be instanceof GyroscopicSensorBlockEntity gyro)
				return getLazy(new GyroscopicSensorPeripheral(gyro));

			return LazyOptional.empty();
		}

		private LazyOptional<IPeripheral> getLazy(IPeripheral peripheral) {
			return LazyOptional.of(() -> peripheral);
		}
	}
}
