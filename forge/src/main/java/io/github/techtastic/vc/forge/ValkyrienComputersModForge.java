package io.github.techtastic.vc.forge;

import dev.architectury.platform.Platform;
import io.github.techtastic.vc.VCPartials;
import io.github.techtastic.vc.ValkyrienComputersMod;
import io.github.techtastic.vc.block.VCBlocks;
import io.github.techtastic.vc.forge.integrations.cc.valkyrienskies.ValkyrienComputersPeripheralProviders;
import io.github.techtastic.vc.forge.item.renderer.GyroscopicSensorModel;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

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
    }

    void modelBakeModification(ModelBakeEvent event) {
        // Find the existing model for ChessBoard - it will have been added automatically by vanilla due to our registration of
        //   of the item in StartupCommon.
        // Replace the mapping with our custom ChessboardModel.

        //ModelResourceLocation itemModelResourceLocation = GyroscopicSensorModel.inventoryModel;
        //BakedModel existingModel = event.getModelRegistry().get(itemModelResourceLocation);

        //if (existingModel != null && !(existingModel instanceof GyroscopicSensorModel)){
            //GyroscopicSensorModel customModel = new GyroscopicSensorModel(existingModel);
            //event.getModelRegistry().put(itemModelResourceLocation, customModel);
        //}
    }
}
