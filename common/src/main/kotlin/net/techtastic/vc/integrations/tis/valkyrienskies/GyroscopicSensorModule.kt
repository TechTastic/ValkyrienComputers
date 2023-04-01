package net.techtastic.vc.integrations.tis.valkyrienskies

import li.cil.tis3d.api.machine.Casing
import li.cil.tis3d.api.machine.Face
import li.cil.tis3d.api.machine.Port
import li.cil.tis3d.api.prefab.module.AbstractModuleWithRotation
import li.cil.tis3d.util.EnumUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.mod.common.getShipManagingPos

class GyroscopicSensorModule(casing: Casing, face: Face) : AbstractModuleWithRotation(casing, face) {
    enum class OUTPUT_MODE {
        X,
        Y,
        Z,
        W,
        ROLL,
        PITCH,
        YAW;

        operator fun next(): OUTPUT_MODE {
            if (ordinal == OUTPUT_MODE.values().size - 1)
                return OUTPUT_MODE.values()[0]
            return OUTPUT_MODE.values()[ordinal + 1]
        }

        fun prev(): OUTPUT_MODE {
            if (ordinal == 0)
                return OUTPUT_MODE.values()[OUTPUT_MODE.values().size - 1]
            return OUTPUT_MODE.values()[ordinal - 1]
        }
    }

    var mode: OUTPUT_MODE = OUTPUT_MODE.X

    override fun step() {
        this.stepOutput()
    }

    private fun stepOutput() {
        if (casing.casingLevel.isClientSide) return
        val level = casing.casingLevel as ServerLevel
        val ship = level.getShipManagingPos(casing.position) ?: return
        val rot = ship.transform.shipToWorldRotation
        for (port in Port.VALUES) {
            val sendingPipe = casing.getSendingPipe(face, port)
            if (!sendingPipe.isWriting) {
                when (this.mode) {
                    OUTPUT_MODE.X -> sendingPipe.beginWrite()
                }
                sendingPipe.beginWrite()
            }
        }
    }

    override fun save(tag: CompoundTag) {
        EnumUtils.save(this.mode, "ValkyrienComputers\$outputMode", tag)

        super.save(tag)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        this.mode = EnumUtils.load(OUTPUT_MODE::class.java, "ValkyrienComputers\$outputMode", tag)
    }
}