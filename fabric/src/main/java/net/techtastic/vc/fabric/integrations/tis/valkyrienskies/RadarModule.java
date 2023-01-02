package net.techtastic.vc.fabric.integrations.tis.valkyrienskies;

import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.machine.Pipe;
import li.cil.tis3d.api.machine.Port;
import li.cil.tis3d.api.prefab.module.AbstractModuleWithRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.techtastic.vc.ValkyrienComputersConfig;
import net.techtastic.vc.ValkyrienComputersConfig.Server.TIS.RADARSETTINGS;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.game.ships.ShipData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RadarModule extends AbstractModuleWithRotation {
    private Object[] radar_result;
    private Object[] ship_result;
    private Object[] sub_result;
    private int[] args;
    private State state = State.INIT;
    private Commands command = Commands.NONE;

    public enum State {
        INIT,
        ARG,
        EXEC,
        IDLE

    }

    public enum Commands {
        NONE,
        SCAN,
        DATA
    }

    public RadarModule(Casing casing, Face face) {
        super(casing, face);
    }

    @Override
    public void step() {
        stepOutput();
        stepInput();
    }

    @Override
    public void onDisabled() {
        state = State.INIT;
        command = Commands.NONE;
        args = new int[] {};
        radar_result = new Object[] {};
    }

    private void stepInput() {
        for (Port port : Port.VALUES) {
            final Pipe receiving = getCasing().getReceivingPipe(getFace(), port);
            if (!receiving.isReading()) receiving.beginRead();

            if (!receiving.canTransfer()) return;

            short input = receiving.read();
            switch (command) {
                case SCAN:
                    if (state.equals(State.INIT)) {
                        if (input > ValkyrienComputersConfig.SERVER.getTIS3D().getRadarSettings().getMaxRadarRadius()) return;

                        args = new int[]{input};
                        state = State.EXEC;
                    }
                    break;
                case DATA:
                    if (state.equals(State.INIT)) {

                    }

                    if (state.equals(State.ARG)) {

                    }
                    break;
                default:
                    initCommand(input);
            }
        }
    }

    private void stepOutput() {
        if (state.equals(State.EXEC)) {
            switch (command) {
                case SCAN:
                    scan(args[0]);

                    state = State.IDLE;
                    command = Commands.NONE;
                    break;
                case DATA:
                    // Grab Data at indexes
                    break;
                default:
                    return;
            }
        }
    }

    private void initCommand(short input) {
        switch (input) {
            case 1:
                state = State.INIT;
                command = Commands.SCAN;
                break;
            case 2:
                state = State.INIT;
                command = Commands.DATA;
                break;
            default:
                return;
        }
    }

    private void scan(double radius) {
        Level level = getCasing().getCasingWorld();
        BlockPos pos = getCasing().getPosition();

        List<Vector3d> ships = VSGameUtilsKt.transformToNearbyShipsAndWorld(level, pos.getX(), pos.getY(), pos.getZ(), radius);
        radar_result = new Object[ships.size()];
        for (Vector3d vec : ships) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, vec.x, vec.y, vec.z);
            Quaterniondc rot = ship.getShipTransform().getShipCoordinatesToWorldCoordinatesRotation();
            Vector3dc vel = ship.getVelocity();
            AABBic aabb = ship.getShipVoxelAABB();

            byte[] name = ship.getName().getBytes(Charset.forName("Cp437"));
            double mass = ship.getInertiaData().getShipMass();
            Object[] position = new Object[] {
                    (short) vec.x,
                    (short) vec.y,
                    (short) vec.z
            };
            Object[] rotation = new Object[] {
                    (short) rot.x(),
                    (short) rot.y(),
                    (short) rot.z(),
                    (short) rot.w()
            };
            Object[] velocity = new Object[] {
                    (short) vel.x(),
                    (short) vel.y(),
                    (short) vel.z()
            };
            double distance = VSGameUtilsKt.squaredDistanceBetweenInclShips(level, pos.getX(), pos.getY(), pos.getZ(), vec.x, vec.y, vec.z);
            Object[] size = new Object[] {
                    (short) Math.abs(aabb.maxX() - aabb.minX()),
                    (short) Math.abs(aabb.maxY() - aabb.minY()),
                    (short) Math.abs(aabb.maxZ() - aabb.minZ())
            };

            ArrayList<Object> data = new ArrayList<Object>();

            RADARSETTINGS settings = ValkyrienComputersConfig.SERVER.getTIS3D().getRadarSettings();
            if (settings.getRadarGetsName()) data.add(name);
            if (settings.getRadarGetsMass()) data.add((short) mass);
            if (settings.getRadarGetsPosition()) data.add(position);
            if (settings.getRadarGetsRotation()) data.add(rotation);
            if (settings.getRadarGetsVelocity()) data.add(velocity);
            if (settings.getRadarGetsDistance()) data.add((short) distance);
            if (settings.getRadarGetsSize()) data.add(size);

            radar_result[ships.indexOf(vec)] = data;
        }
    }
}
