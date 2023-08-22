package io.github.techtastic.vc.fabric.services;

import io.github.techtastic.vc.registry.DeferredRegister;
import io.github.techtastic.vc.services.DeferredRegisterBackend;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import io.github.techtastic.vc.fabric.DeferredRegisterImpl;

public class DeferredRegisterBackendFabric implements DeferredRegisterBackend {

    @Override
    public @NotNull <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl<>(id, registry);
    }
}
