package net.techtastic.vc.forge.integrations.tis.eureka

import li.cil.tis3d.api.serial.SerialInterface
import li.cil.tis3d.api.serial.SerialInterfaceProvider
import li.cil.tis3d.api.serial.SerialProtocolDocumentationReference
import li.cil.tis3d.util.EnumUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.registries.ForgeRegistryEntry
import net.techtastic.vc.util.ByteUtils
import net.techtastic.vc.util.ShipUtils
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*


class ShipHelmSerialInterfaceProvider :  ForgeRegistryEntry<SerialInterfaceProvider>(), SerialInterfaceProvider {
    override fun matches(level: Level, pos: BlockPos, direction: Direction): Boolean = level.getBlockEntity(pos) is ShipHelmBlockEntity

    override fun getInterface(level: Level, pos: BlockPos, direction: Direction): Optional<SerialInterface> =
            Optional.of(ShipHelmSerialInterface(level.getBlockEntity(pos) as ShipHelmBlockEntity))

    override fun getDocumentationReference(): Optional<SerialProtocolDocumentationReference> =
            Optional.of(SerialProtocolDocumentationReference(TranslatableComponent("block.vs_eureka.oak_ship_helm"), "ship_helm.md"))

    override fun stillValid(level: Level, pos: BlockPos, direction: Direction, serial: SerialInterface): Boolean =
            serial is ShipHelmSerialInterface

    class ShipHelmSerialInterface(private val helm: ShipHelmBlockEntity) : SerialInterface {
        enum class MODE {
            MOVE,
            RESETMOVE,
            CRUISE,
            ALIGN,
            ASSEMBLY,
            BALLOON,
            ANCHOR,
            HELM;

            operator fun next(): MODE {
                if (ordinal == MODE.values().size - 1)
                    return MODE.values()[0]
                return MODE.values()[ordinal + 1]
            }

            fun prev(): MODE {
                if (ordinal == 0)
                    return MODE.values()[MODE.values().size - 1]
                return MODE.values()[ordinal - 1]
            }
        }

        private var mode: MODE = MODE.MOVE

        private var input: Byte = Byte.MAX_VALUE

        private var charset: Charset = Charset.forName("Cp437")

        override fun canWrite(): Boolean = true

        override fun write(p0: Short) {
            val bytes = ByteUtils.shortToByteArray(p0)
            val iMode = bytes[0]
            this.input = bytes[1]

            this.mode = when (iMode.toShort()) {
                1.toShort() -> MODE.MOVE
                2.toShort() -> MODE.RESETMOVE
                3.toShort() -> MODE.CRUISE
                4.toShort() -> MODE.ALIGN
                5.toShort() -> MODE.ASSEMBLY
                6.toShort() -> MODE.BALLOON
                7.toShort() -> MODE.ANCHOR
                8.toShort() -> MODE.HELM
                else -> this.mode
            }
        }

        override fun canRead(): Boolean = true

        override fun peek(): Short {
            if (helm.level == null) return 0xFFFF.toShort()
            if (helm.level!!.isClientSide) return 0xFFFF.toShort()
            val level: ServerLevel = helm.level as ServerLevel
            val ship = ShipUtils.getShipManagingPos(level, helm.blockPos)

            when(this.mode) {
                MODE.MOVE -> move(ship, this.input)
                MODE.RESETMOVE -> resetAllMovement(ship)
                MODE.CRUISE -> return cruise(ship, this.input)
                MODE.ALIGN -> return align(ship, this.input)
                MODE.ASSEMBLY -> return assembly(ship, this.input)
                MODE.BALLOON -> return getBalloons(ship)
                MODE.ANCHOR -> return getAnchors(ship, this.input)
                MODE.HELM -> return getHelms(ship)
            }

            this.input = Byte.MAX_VALUE

            return 0xFFFF.toShort()
        }

        override fun skip() {
        }

        override fun reset() {
            this.mode = MODE.MOVE
        }

        override fun save(p0: CompoundTag) {
            EnumUtils.save(this.mode, "ValkyrienComputers\$mode", p0)
            p0.putByte("ValkyrienComputers\$input", this.input)
        }

        override fun load(p0: CompoundTag) {
            this.mode = EnumUtils.load(MODE::class.java, "ValkyrienComputers\$mode", p0)
            this.input = p0.getByte("ValkyrienComputers\$input")
        }

        private fun move(ship: ServerShip?, input: Byte) {
            if (ship == null) return
            ship.getAttachment(EurekaShipControl::class.java) ?: return
            if (input == Byte.MAX_VALUE) return

            var control = ship.getAttachment(SeatedControllingPlayer::class.java)
            if (control == null) {
                control = SeatedControllingPlayer(helm.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
                ship.saveAttachment(SeatedControllingPlayer::class.java, control)
            }

            val array = ByteArray(1)
            array[0] = input

            when (this.charset.decode(ByteBuffer.wrap(array)).get().lowercase()) {
                "f" -> control.forwardImpulse = 1.0f
                "b" -> control.forwardImpulse = -1.0f
                "l" -> control.leftImpulse = 1.0f
                "r" -> control.leftImpulse = -1.0f
                "u" -> control.upImpulse = 1.0f
                "d" -> control.upImpulse = -1.0f
            }
        }

        private fun resetAllMovement(ship: ServerShip?) {
            if (ship == null) return
            ship.getAttachment(EurekaShipControl::class.java) ?: return

            var control = ship.getAttachment(SeatedControllingPlayer::class.java)
            if (control == null) {
                control = SeatedControllingPlayer(helm.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
                ship.saveAttachment(SeatedControllingPlayer::class.java, control)
            }

            control.forwardImpulse = 0.0f
            control.leftImpulse = 0.0f
            control.upImpulse = 0.0f
        }

        private fun cruise(ship: ServerShip?, input: Byte): Short {
            if (ship == null) return 0xFFFF.toShort()
            ship.getAttachment(EurekaShipControl::class.java) ?: return 0xFFFF.toShort()
            if (input == Byte.MAX_VALUE) return 0xFFFF.toShort()

            var control = ship.getAttachment(SeatedControllingPlayer::class.java)
            if (control == null) {
                control = SeatedControllingPlayer(helm.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite)
                ship.saveAttachment(SeatedControllingPlayer::class.java, control)
            }

            if (input.toInt() != 0) control.cruise = control.cruise.not()
            return if (control.cruise) 1 else 0
        }

        private fun align(ship: ServerShip?, input: Byte): Short {
            if (ship == null) return 0xFFFF.toShort()
            val control = ship.getAttachment(EurekaShipControl::class.java) ?: return 0xFFFF.toShort()
            if (input == Byte.MAX_VALUE) return 0xFFFF.toShort()

            if (input.toInt() != 0) control.aligning = control.aligning.not()
            return if (control.aligning) 1 else 0
        }

        private fun assembly(ship: ServerShip?, input: Byte): Short {
            if (input != Byte.MAX_VALUE && input.toInt() == 1)
                return if (helm.assembled) 1 else 0

            ship?.let { helm.disassemble() } ?: helm.assemble()
            return 3
        }

        private fun getBalloons(ship: ServerShip?): Short {
            if (ship == null) return 0xFFFF.toShort()
            val control = ship.getAttachment(EurekaShipControl::class.java) ?: return 0xFFFF.toShort()

            return control.balloons.toShort()
        }

        private fun getAnchors(ship: ServerShip?, input: Byte): Short {
            if (ship == null) return 0xFFFF.toShort()
            val control = ship.getAttachment(EurekaShipControl::class.java) ?: return 0xFFFF.toShort()
            if (input == Byte.MAX_VALUE) return 0xFFFF.toShort()

            when(input.toInt()) {
                0 -> return control.anchors.toShort()
                1 -> return control.anchorsActive.toShort()
            }

            return 0xFFFF.toShort()
        }

        private fun getHelms(ship: ServerShip?): Short {
            if (ship == null) return 0xFFFF.toShort()
            val control = ship.getAttachment(EurekaShipControl::class.java) ?: return 0xFFFF.toShort()

            return control.helms.toShort()
        }
    }
}