package io.github.techtastic.vc.cables

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag

class GlobalCableNetwork {
    val network: MutableList<Pair<BlockPos, BlockPos>> = mutableListOf()

    fun loadNetwork(tag: CompoundTag) {

    }

    fun saveNetwork(tag: CompoundTag) {
    }
}