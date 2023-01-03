package net.techtastic.vc

import dev.architectury.platform.Platform
import net.techtastic.vc.cc.ValkyrienComputersBlocksCC
import net.techtastic.vc.tis.ValkyrienComputersItemsTIS
import org.valkyrienskies.core.impl.config.VSConfigClass

object ValkyrienComputersMod {
    const val MOD_ID = "vc"
    private val nextTick1 = mutableListOf<() -> Unit>()
    private val nextTick2 = mutableListOf<() -> Unit>()
    private var isTick1 = false

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("vc", ValkyrienComputersConfig::class.java)

        ValkyrienComputersTab.registerTab()

        val CC_config = ValkyrienComputersConfig.SERVER.ComputerCraft
        if (Platform.isModLoaded("computercraft") && !CC_config.disableComputerCraft) {
            ValkyrienComputersBlocksCC.registerCCBlocks()
        }

        val TIS_config = ValkyrienComputersConfig.SERVER.TIS3D
        if (Platform.isModLoaded("tis3d") && !TIS_config.disableTIS3D) {
            ValkyrienComputersItemsTIS.registerItems()
        }
    }

    private fun switchTickList(): MutableList<() -> Unit> {
        isTick1 = !isTick1

        return if (isTick1) nextTick1 else nextTick2
    }

    fun queueNextTick(task: () -> Unit) {
        if (isTick1) {
            nextTick2.add(task)
        } else {
            nextTick1.add(task)
        }
    }

    @JvmStatic
    fun initClient() {
    }
}
