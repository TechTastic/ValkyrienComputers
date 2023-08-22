package io.github.techtastic.vc.forge.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import io.github.techtastic.vc.registry.DeferredRegister;
import io.github.techtastic.vc.services.DeferredRegisterBackend;
import org.jetbrains.annotations.NotNull;
import io.github.techtastic.vc.forge.DeferredRegisterImpl;

public class DeferredRegisterBackendForge implements DeferredRegisterBackend {

    @Override
    public @NotNull <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl(id, registry);
    }
}
