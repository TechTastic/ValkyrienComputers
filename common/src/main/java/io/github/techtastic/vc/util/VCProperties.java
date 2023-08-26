package io.github.techtastic.vc.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class VCProperties {
    public enum OutputAxis implements StringRepresentable {
        X("x"),
        Y("y"),
        Z("z");


        private final String name;
        private OutputAxis(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<OutputAxis> OUTPUT = EnumProperty.create("output_axis", OutputAxis.class);
}
