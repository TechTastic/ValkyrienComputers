package io.github.techtastic.vc.registry

import io.github.techtastic.vc.item.custom.GyroscopicSensorBlockItem
import io.github.techtastic.vc.services.ValkyrienComputersPlatformHelper
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.*

class BlockItemHelper {
    companion object {
        fun getNew(block: Block, properties: Item.Properties): GyroscopicSensorBlockItem {
            return ServiceLoader.load(ValkyrienComputersPlatformHelper::class.java)
                    .findFirst()
                    .get()
                    .newGyroscopicSensorBlockItem(block, properties)
        }
    }
}