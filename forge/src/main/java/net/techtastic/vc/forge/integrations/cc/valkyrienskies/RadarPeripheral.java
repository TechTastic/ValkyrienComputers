package net.techtastic.vc.forge.integrations.cc.valkyrienskies;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.cc.ValkyrienComputersBlocksCC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.Ship;
import org.valkyrienskies.core.game.ships.ShipData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.List;

public class RadarPeripheral implements IPeripheral {
	private Level level;
	private BlockPos pos;

	public RadarPeripheral(Level level, BlockPos worldPosition) {
		this.level = level;
		this.pos = worldPosition;
	}

	@LuaFunction
	public final Object[] scan (double radius) throws LuaException {
		return scanForShips(level, pos, radius);
	}

	@NotNull
	@Override
	public String getType() {
		return "radar";
	}

	@Override
	public boolean equals(@Nullable IPeripheral iPeripheral) {
		return level.getBlockState(pos).getBlock().is(ValkyrienComputersBlocksCC.RADAR.get());
	}

	public Object[] scanForShips(Level level, BlockPos position, double radius) {
		RADARSETTINGS settings = ValkyrienComputersConfig.SERVER.getComputerCraft().getRadarSettings();

		if (level == null || position == null) {
			return new Object[] {"booting"};
		}

		if (!level.isClientSide()) {
			// THROW EARLY RESULTS
			if (radius < 1.0) {
				return new Object[] {"radius too small"};
			} else if (radius > settings.getMaxRadarRadius()) {
				return new Object[] {"radius too big"};
			}
			if (!level.getBlockState(position).getBlock().is(ValkyrienComputersBlocksCC.RADAR.get())) {
				return new Object[] {"no radar"};
			}

			// IF RADAR IS ON A SHIP, USE THE WORLD SPACE COORDINATES
			Ship test = VSGameUtilsKt.getShipManagingPos(level, position);
			if (test != null) {
				Vector3d newPos = VSGameUtilsKt.toWorldCoordinates(test, position);
				position = new BlockPos(newPos.x, newPos.y, newPos.z);
			}

			// GET A LIST OF SHIP POSITIONS WITHIN RADIUS
			List<Vector3d> ships = VSGameUtilsKt.transformToNearbyShipsAndWorld(level, position.getX(), position.getY(), position.getZ(), radius);
			Object[] results = new Object[ships.size()];

			// TESTING FOR NO SHIPS
			if (results.length == 0) {
				results = new Object[1];
				results[0] = "no ships";
				return results;
			}

			// Give results the ID, X, Y, and z of each Ship
			for (Vector3d vec : ships) {
				Ship ship = VSGameUtilsKt.getShipManagingPos(level, vec);
				Vector3dc pos = ship.getShipTransform().getShipPositionInWorldCoordinates();

				HashMap<String, Object> result = new HashMap<>();
				result.put("name", VSGameUtilsKt.getShipObjectManagingPos(level, vec).getShipData().getName());

				Object[] resultPos = new Object[3];
				resultPos[0] = pos.x();
				resultPos[1] = pos.y();
				resultPos[2] = pos.z();
				result.put("pos", resultPos);

				if (ValkyrienComputersConfig.SERVER.getComputerCraft().getRadarGivesDistance()) {
					Quaterniondc rot = ship.getShipTransform().getShipCoordinatesToWorldCoordinatesRotation();

					Object[] resultRot = new Object[4];
					resultRot[0] = rot.x();
					resultRot[1] = rot.y();
					resultRot[2] = rot.z();
					resultRot[3] = rot.w();
					result.put("rot", resultRot);
				}

				ShipData data = VSGameUtilsKt.getShipManagingPos(((ServerLevel) level), vec.x, vec.y, vec.z);

				result.put("mass", data.getInertiaData().getShipMass());

				results[ships.indexOf(vec)] = result;
			}

			return results;
		}
		return new Object[0];
	}
}
