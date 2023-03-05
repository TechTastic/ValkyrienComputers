package net.techtastic.vc.cc.valkyrienskies;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersConfig.Server.COMPUTERCRAFT.RADARSETTINGS;
import net.techtastic.vc.cc.ValkyrienComputersBlocksCC;
import net.techtastic.vc.util.SpecialLuaTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.Ship;
import org.valkyrienskies.core.game.ships.ShipData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
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
	public final ArrayList<Object> scan (double radius) throws LuaException {
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

	public ArrayList<Object> scanForShips(Level level, BlockPos position, double radius) {
		RADARSETTINGS settings = ValkyrienComputersConfig.SERVER.getComputerCraft().getRadarSettings();

		if (level == null || position == null) {
			return SpecialLuaTables.getObjectAsArrayList("booting");
		}

		if (!level.isClientSide()) {
			// THROW EARLY RESULTS
			if (radius < 1.0) {
				return SpecialLuaTables.getObjectAsArrayList("radius too small");
			} else if (radius > settings.getMaxRadarRadius()) {
				return SpecialLuaTables.getObjectAsArrayList("radius too big");
			}
			if (!level.getBlockState(position).getBlock().is(ValkyrienComputersBlocksCC.RADAR.get())) {
				return SpecialLuaTables.getObjectAsArrayList("no radar");
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
				return SpecialLuaTables.getObjectAsArrayList("no ships");
			}

			// Give results the ID, X, Y, and z of each Ship
			for (Vector3d vec : ships) {
				Ship ship = VSGameUtilsKt.getShipManagingPos(level, vec);
				ShipData data = VSGameUtilsKt.getShipManagingPos(((ServerLevel) level), vec.x, vec.y, vec.z);
				Vector3dc pos = ship.getShipTransform().getShipPositionInWorldCoordinates();

				HashMap<String, Object> result = new HashMap<>();

				// Give Name
				if (settings.getRadarGetsName()) result.put("name", data.getName());

				// Give ID
				if (settings.getRadarGetsId()) result.put("id", data.getId());

				// Give Position
				if (settings.getRadarGetsPosition()) result.put("position", SpecialLuaTables.getVectorAsTable(pos));

				// Give Mass
				if (settings.getRadarGetsMass()) result.put("mass", data.getInertiaData().getShipMass());

				// Give Rotation
				if (settings.getRadarGetsRotation()) {
					result.put("rotation", SpecialLuaTables.getQuaternionAsTable(ship.getShipTransform().getShipCoordinatesToWorldCoordinatesRotation()));
				}

				// Give Velocity
				if (settings.getRadarGetsVelocity()) {
					result.put("velocity", SpecialLuaTables.getVectorAsTable(data.getVelocity()));
				}

				// Give Distance
				if (settings.getRadarGetsDistance()) result.put("distance", VSGameUtilsKt.squaredDistanceBetweenInclShips(
						level,
						vec.x,
						vec.y,
						vec.z,
						pos.x(),
						pos.y(),
						pos.z()
				));

				// Give Size
				if (settings.getRadarGetsSize()) {
					AABBdc aabb = ship.getShipAABB();
					HashMap<String, Double> map = new HashMap<>();
					map.put("x", aabb.maxX() - aabb.minX());
					map.put("Y", aabb.maxY() - aabb.minY());
					map.put("Z", aabb.maxZ() - aabb.minZ());
					result.put("size", map);
				}

				results[ships.indexOf(vec)] = result;
			}

			return SpecialLuaTables.getObjectAsArrayList(results);
		}
		return new ArrayList<>();
	}
}
