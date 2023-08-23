package io.github.techtastic.vc.services

import io.github.techtastic.vc.item.custom.GyroscopicSensorBlockItem
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

interface ValkyrienComputersPlatformHelper {
    fun createCreativeTab(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab
}