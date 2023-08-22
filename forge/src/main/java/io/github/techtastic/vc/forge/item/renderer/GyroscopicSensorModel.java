package io.github.techtastic.vc.forge.item.renderer;

import io.github.techtastic.vc.ValkyrienComputersMod;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class GyroscopicSensorModel implements BakedModel {
    private BakedModel baseModel;
    private ItemOverrides itemOverride;

    public GyroscopicSensorModel(BakedModel model)
    {
        baseModel = model;
        itemOverride = model.getOverrides();
    }

    // create a tag (ModelResourceLocation) for our model.
    //  "inventory" is used for item. If you don't specify it, you will end up with "" by default,
    //  which is used for blocks.
    public static final ModelResourceLocation inventoryModel
            = new ModelResourceLocation(ValkyrienComputersMod.MOD_ID + ":gyro", "inventory");

    // called for item rendering
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return baseModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseModel.useAmbientOcclusion();
    }

    @Override
    public ItemOverrides getOverrides() {
        return itemOverride;
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return baseModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseModel.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data) {
        return baseModel.getParticleIcon(data);
    }

    @Override
    public ItemTransforms getTransforms() {
        return baseModel.getTransforms();
    }

    // This is a forge extension that is expected for blocks only.
    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        throw new AssertionError("ChessboardModel::getQuads(IModelData) should never be called");
    }

    // This is a forge extension that is expected for blocks only.
    @NotNull
    @Override
    public IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData) {
        throw new AssertionError("ChessboardModel::getModelData should never be called");
    }
}
