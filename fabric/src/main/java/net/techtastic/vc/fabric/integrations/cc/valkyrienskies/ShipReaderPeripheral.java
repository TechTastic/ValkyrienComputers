package net.techtastic.vc.fabric.integrations.cc.valkyrienskies;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.techtastic.vc.cc.ValkyrienComputersBlocksCC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.game.ships.ShipData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipReaderPeripheral implements IPeripheral {

    private Level level;
    private BlockPos pos;

    public ShipReaderPeripheral(Level level, BlockPos worldPosition) {
        this.level = level;
        this.pos = worldPosition;
    }

    @LuaFunction
    public final String getShipName() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                return ship.getName();
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return "";
    }

    @LuaFunction
    public final boolean setShipName(String string) throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                ship.setName(string);
                return true;
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final long getShipID() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                return ship.getId();
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return 0;
    }

    @LuaFunction
    public final double getMass() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                return ship.getInertiaData().getShipMass();
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return 0.0;
    }

    @LuaFunction
    public final Object[] getVelocity() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                Vector3dc vel = ship.getVelocity();
                return new Object[] { vel.x(), vel.y(), vel.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] getWorldspacePosition() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                Vector3dc vec = ship.getShipTransform().getShipPositionInWorldCoordinates();
                return new Object[] { vec.x(), vec.y(), vec.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] getShipyardPosition() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                Vector3dc vec = ship.getShipTransform().getShipPositionInShipCoordinates();
                return new Object[] { vec.x(), vec.y(), vec.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] getScale() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                Vector3dc scale = ship.getShipTransform().getShipCoordinatesToWorldCoordinatesScaling();
                return new Object[] { scale.x(), scale.y(), scale.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] getRotation() throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
            if (ship != null) {
                Quaterniondc rot = ship.getShipTransform().getShipCoordinatesToWorldCoordinatesRotation();
                return new Object[] { rot.x(), rot.y(), rot.z(), rot.w() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] transformPosition(double x, double y, double z) throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, new BlockPos(x, y, z));
            if (ship != null) {
                Vector3dc vec = ship.getShipToWorld().transformPosition(new Vector3d(x, y, z));
                return new Object[] { vec.x(), vec.y(), vec.z() };
            } else {
                ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
                if (ship != null) {
                    Vector3dc vec = ship.getWorldToShip().transformPosition(new Vector3d(x, y, z));
                    return new Object[] { vec.x(), vec.y(), vec.z() };
                } else {
                    throw new LuaException("Not on a Ship");
                }
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] transformDirection(double x, double y, double z) throws LuaException {
        if (!level.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, new BlockPos(x, y, z));
            if (ship != null) {
                Vector3dc vec = ship.getShipToWorld().transformDirection(new Vector3d(x, y, z));
                return new Object[] { vec.x(), vec.y(), vec.z() };
            } else {
                ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos);
                if (ship != null) {
                    Vector3dc vec = ship.getWorldToShip().transformDirection(new Vector3d(x, y, z));
                    return new Object[] { vec.x(), vec.y(), vec.z() };
                } else {
                    throw new LuaException("Not on a Ship");
                }
            }
        }
        return new Object[0];
    }

    @NotNull
    @Override
    public String getType() {
        return "ship_reader";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return level.getBlockState(pos).getBlock().is(ValkyrienComputersBlocksCC.READER.get());
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        IPeripheral.super.attach(computer);
    }
}
