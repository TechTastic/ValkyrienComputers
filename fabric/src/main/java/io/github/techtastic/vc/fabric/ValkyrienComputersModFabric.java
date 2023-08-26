package io.github.techtastic.vc.fabric;

import dev.architectury.platform.Platform;
import io.github.techtastic.vc.VCPartials;
import io.github.techtastic.vc.block.VCBlocks;
import io.github.techtastic.vc.fabric.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import io.github.techtastic.vc.ValkyrienComputersMod;
import io.github.techtastic.vc.fabric.rendering.GyroscopicSensorDIR;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class ValkyrienComputersModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before vc
        new ValkyrienSkiesModFabric().onInitialize();

        if (Platform.isModLoaded("computercraft"))
            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();

        ValkyrienComputersMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            VCPartials.INSTANCE.init();

            ValkyrienComputersMod.initClient();

            BlockRenderLayerMap.INSTANCE.putBlock(VCBlocks.INSTANCE.getGYRO().get(), RenderType.cutout());

            BuiltinItemRendererRegistry.INSTANCE.register(VCBlocks.INSTANCE.getGYRO().get().asItem(), new GyroscopicSensorDIR());
        }
    }
}
