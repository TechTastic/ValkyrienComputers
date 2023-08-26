package io.github.techtastic.vc.util;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

import static net.minecraft.world.level.block.Block.box;

public class VoxelShapeUtil {
    public static VoxelShape getGyroVoxel() {
        return Stream.of(
                box(0, 0, 0, 1, 2, 2),
                box(1, 0, 0, 2, 1, 2),
                box(1, 1, 0, 2, 2, 1),
                box(2, 0, 0, 3, 1, 1),
                box(0, 2, 0, 1, 3, 1),
                box(0, 0, 2, 1, 1, 3),
                box(15, 0, 14, 16, 2, 16),
                box(14, 0, 14, 15, 1, 16),
                box(14, 1, 15, 15, 2, 16),
                box(13, 0, 15, 14, 1, 16),
                box(15, 2, 15, 16, 3, 16),
                box(15, 0, 13, 16, 1, 14),
                box(14, 0, 0, 16, 2, 1),
                box(14, 0, 1, 16, 1, 2),
                box(15, 1, 1, 16, 2, 2),
                box(15, 0, 2, 16, 1, 3),
                box(15, 2, 0, 16, 3, 1),
                box(13, 0, 0, 14, 1, 1),
                box(0, 0, 15, 2, 2, 16),
                box(0, 0, 14, 2, 1, 15),
                box(0, 1, 14, 1, 2, 15),
                box(0, 0, 13, 1, 1, 14),
                box(0, 2, 15, 1, 3, 16),
                box(2, 0, 15, 3, 1, 16),
                box(0, 14, 14, 1, 16, 16),
                box(1, 15, 14, 2, 16, 16),
                box(1, 14, 15, 2, 15, 16),
                box(2, 15, 15, 3, 16, 16),
                box(0, 13, 15, 1, 14, 16),
                box(0, 15, 13, 1, 16, 14),
                box(15, 14, 0, 16, 16, 2),
                box(14, 15, 0, 15, 16, 2),
                box(14, 14, 0, 15, 15, 1),
                box(13, 15, 0, 14, 16, 1),
                box(15, 13, 0, 16, 14, 1),
                box(15, 15, 2, 16, 16, 3),
                box(14, 14, 15, 16, 16, 16),
                box(14, 15, 14, 16, 16, 15),
                box(15, 14, 14, 16, 15, 15),
                box(15, 15, 13, 16, 16, 14),
                box(15, 13, 15, 16, 14, 16),
                box(13, 15, 15, 14, 16, 16),
                box(0, 14, 0, 2, 16, 1),
                box(0, 15, 1, 2, 16, 2),
                box(0, 14, 1, 1, 15, 2),
                box(0, 15, 2, 1, 16, 3),
                box(0, 13, 0, 1, 14, 1),
                box(2, 15, 0, 3, 16, 1)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }
}
