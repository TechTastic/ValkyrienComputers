package net.techtastic.vc.forge.integrations.tis.eureka

import li.cil.tis3d.api.serial.SerialInterface
import li.cil.tis3d.api.serial.SerialInterfaceProvider
import li.cil.tis3d.api.serial.SerialProtocolDocumentationReference
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.level.Level
import net.minecraftforge.registries.ForgeRegistryEntry
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import java.util.*

class ShipHelmSerialInterfaceProvider :  ForgeRegistryEntry<SerialInterfaceProvider>(), SerialInterfaceProvider {
    override fun matches(level: Level, pos: BlockPos, direction: Direction): Boolean = level.getBlockEntity(pos) is ShipHelmBlockEntity

    override fun getInterface(level: Level, pos: BlockPos, direction: Direction): Optional<SerialInterface> =
            Optional.of(ShipHelmSerialInterface(level.getBlockEntity(pos) as ShipHelmBlockEntity))

    override fun getDocumentationReference(): Optional<SerialProtocolDocumentationReference> =
            Optional.of(SerialProtocolDocumentationReference(TranslatableComponent("block.vs_eureka.oak_ship_helm"), "ship_helm.md"))

    override fun stillValid(level: Level, pos: BlockPos, direction: Direction, serial: SerialInterface): Boolean =
            serial is ShipHelmSerialInterface

    class ShipHelmSerialInterface(helm: ShipHelmBlockEntity) : SerialInterface {
        override fun canWrite(): Boolean {
            TODO("Not yet implemented")
        }

        override fun write(p0: Short) {
            TODO("Not yet implemented")
        }

        override fun canRead(): Boolean {
            TODO("Not yet implemented")
        }

        override fun peek(): Short {
            TODO("Not yet implemented")
        }

        override fun skip() {
            TODO("Not yet implemented")
        }

        override fun reset() {
            TODO("Not yet implemented")
        }

        override fun save(p0: CompoundTag) {
            TODO("Not yet implemented")
        }

        override fun load(p0: CompoundTag) {
            TODO("Not yet implemented")
        }

    }
}