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
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.techtastic.vc.ValkyrienComputersConfig
import net.techtastic.vc.item.UltrasonicSensorModuleItem
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.squaredDistanceBetweenInclShips
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips

open class UltrasonicSensorModule(casing: Casing, face: Face) : AbstractModuleWithRotation(casing, face) {
    enum class OUTMODE {
        BLOCK,
        ENTITY;

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

    var mode: OUTMODE = OUTMODE.BLOCK

    override fun step() {
        this.stepOutput()
    }

    private fun stepOutput() {
        if (casing.casingLevel.isClientSide) return
        val level = casing.casingLevel as ServerLevel

        var output: Short = 0xFFFF.toShort()

        val ship = level.getShipManagingPos(casing.position)
        if (ship != null) {
            val hit = getClip(level, casing.position, ship, ValkyrienComputersConfig.SERVER.TIS3D.maxClipDistance)
            if (hit != null) {
                output = when (this.mode) {
                    OUTMODE.BLOCK -> if (hit.first.equals("block"))
                        HalfFloat.toHalf(hit.second.second.toFloat())
                    else
                        0
                    OUTMODE.ENTITY -> if (hit.first.equals("entity"))
                        HalfFloat.toHalf(hit.second.second.toFloat())
                    else
                        0
                }
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
        val data: OUTMODE = UltrasonicSensorModuleItem.loadFromStack(stack)
        this.mode = data
    }

    override fun onUninstalled(stack: ItemStack) {
        UltrasonicSensorModuleItem.saveToStack(stack, this.mode)
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

    fun getClip(level: Level, pos: BlockPos, ship: ServerShip?, distance: Int): Pair<String, Pair<String, Double>>? {
        val state = level.getBlockState(pos)
        val direction = state.getValue(BlockStateProperties.FACING)

        val result: HitResult = level.clipIncludeShips(ClipContext(
                pos.toJOMLD().toMinecraft(),
                pos.relative(direction, distance).toJOMLD().toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                null),
                true,
                ship?.id
        )

        if (result.type.equals(HitResult.Type.MISS)) return null

        val dist = Pair("distance", level.squaredDistanceBetweenInclShips(
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                result.location.x, result.location.y, result.location.z)
        )

        return if (result.type.equals(HitResult.Type.BLOCK)) {
            Pair("block", dist)
        } else if (result.type.equals(HitResult.Type.ENTITY)) {
            Pair("entity", dist)
        } else {
            Pair("something", dist)
        }
    }
}