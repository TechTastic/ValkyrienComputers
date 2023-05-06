package net.techtastic.vc.integrations.tis.valkyrienskies

import cc.emmert.tisadvanced.util.HalfFloat
import li.cil.tis3d.api.API
import li.cil.tis3d.api.machine.Casing
import li.cil.tis3d.api.machine.Face
import li.cil.tis3d.api.machine.Port
import li.cil.tis3d.api.prefab.module.AbstractModuleWithRotation
import li.cil.tis3d.api.util.RenderContext
import li.cil.tis3d.client.renderer.font.NormalFontRenderer
import li.cil.tis3d.util.Color
import li.cil.tis3d.util.EnumUtils
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.techtastic.vc.integrations.ShipIntegrationMethods
import net.techtastic.vc.item.AccelerometerSensorModuleItem
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.getShipManagingPos

open class AccelerometerSensorModule(casing: Casing, face: Face) : AbstractModuleWithRotation(casing, face) {
    enum class OUTMODE {
        X,
        Y,
        Z;

        operator fun next(): OUTMODE {
            if (ordinal == OUTMODE.values().size - 1)
                return OUTMODE.values()[0]
            return OUTMODE.values()[ordinal + 1]
        }

        fun prev(): OUTMODE {
            if (ordinal == 0)
                return OUTMODE.values()[OUTMODE.values().size - 1]
            return OUTMODE.values()[ordinal - 1]
        }
    }

    var mode: OUTMODE = OUTMODE.X

    override fun step() {
        this.stepOutput()
    }

    private fun stepOutput() {
        if (casing.casingLevel.isClientSide) return
        val level = casing.casingLevel as ServerLevel

        var output: Short = 0xFFFF.toShort()

        val ship = level.getShipManagingPos(casing.position)
        if (ship != null) {
            val vel = ShipIntegrationMethods.getVelocityFromShip(ship)
            output = when (this.mode) {
                OUTMODE.X -> HalfFloat.toHalf(vel.getValue("x").toFloat())
                OUTMODE.Y -> HalfFloat.toHalf(vel.getValue("y").toFloat())
                OUTMODE.Z -> HalfFloat.toHalf(vel.getValue("z").toFloat())
            }
        }

        for (port in Port.VALUES) {
            val sendingPipe = casing.getSendingPipe(face, port)
            if (!sendingPipe.isWriting) {
                sendingPipe.beginWrite(output)
            }
        }
    }

    override fun onInstalled(stack: ItemStack) {
        val data: OUTMODE = AccelerometerSensorModuleItem.loadFromStack(stack)
        this.mode = data
    }

    override fun onUninstalled(stack: ItemStack) {
        AccelerometerSensorModuleItem.saveToStack(stack, this.mode)
    }

    override fun save(tag: CompoundTag) {
        EnumUtils.save(this.mode, "ValkyrienComputers\$outputMode", tag)

        super.save(tag)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        this.mode = EnumUtils.load(OUTMODE::class.java, "ValkyrienComputers\$outputMode", tag)
    }

    @Environment(EnvType.CLIENT)
    override fun render(context: RenderContext) {
        if (!casing.isEnabled || !this.isVisible) {
            return
        }
        context.drawString(NormalFontRenderer.INSTANCE, mode.toString(), 0xFFFF)
        val matrixStack = context.matrixStack
        matrixStack.pushPose()
        rotateForRendering(matrixStack)
        context.drawAtlasQuadUnlit(ResourceLocation(ValkyrienSkiesMod.MOD_ID, "block/overlay/accel_module"))
        if (context.closeEnoughForDetails(casing.position)) {
            drawState(context)
        }
        matrixStack.popPose()
    }

    @Environment(EnvType.CLIENT)
    open fun drawState(context: RenderContext) {
        val matrixStack = context.matrixStack
        val font = API.normalFontRenderer
        matrixStack.translate((3 / 16f).toDouble(), (5 / 16f).toDouble(), 0.0)
        matrixStack.scale(1 / 64f, 1 / 64f, 1f)

        if (mode.name.length == 3)
            matrixStack.translate(7.25, 5.0, 0.0)

        context.drawString(font, mode.name, Color.WHITE)

        if (mode.name.length == 3)
            matrixStack.translate(-7.25, 0.0, 0.0)
    }
}