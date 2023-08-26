package io.github.techtastic.vc.forge;

import com.jozufozu.flywheel.backend.RenderLayer;
import dev.architectury.platform.Platform;
import io.github.techtastic.vc.VCPartials;
import io.github.techtastic.vc.ValkyrienComputersMod;
import io.github.techtastic.vc.block.VCBlocks;
import io.github.techtastic.vc.forge.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(ValkyrienComputersMod.MOD_ID)
public class ValkyrienComputersModForge {
    boolean happendClientSetup = false;
    static IEventBus MOD_BUS;

    public ValkyrienComputersModForge() {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> VCPartials.INSTANCE::init);

        ValkyrienComputersMod.init();

        if (Platform.isModLoaded("computercraft"))
            ValkyrienComputersPeripheralProviders.registerPeripheralProviders();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        ValkyrienComputersMod.initClient();

        ItemBlockRenderTypes.setRenderLayer(VCBlocks.INSTANCE.getGYRO().get(), RenderType.cutout());
    }
}
