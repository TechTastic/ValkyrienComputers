package net.techtastic.vc.item

import li.cil.tis3d.common.item.ModuleItem
import li.cil.tis3d.util.EnumUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.techtastic.vc.ValkyrienComputersItems
import net.techtastic.vc.integrations.tis.valkyrienskies.AccelerometerSensorModule

class AccelerometerSensorModuleItem() : ModuleItem(Properties().tab(ValkyrienComputersItems.TAB)) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)
        var data: AccelerometerSensorModule.OUTMODE = loadFromStack(stack)
        if (player.isShiftKeyDown)
            data = data.next()

        saveToStack(stack, data)

        if (level.isClientSide())
            if (player.isShiftKeyDown)
                player.displayClientMessage(TextComponent(data.name), true)

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide())
    }

    override fun appendHoverText(stack: ItemStack, worldIn: Level?, tooltip: MutableList<Component>, flagIn: TooltipFlag) {
        val data: AccelerometerSensorModule.OUTMODE = loadFromStack(stack)
        tooltip.add(TextComponent("Mode: $data"))
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
    }

    companion object {
        fun loadFromTag(tag: CompoundTag): AccelerometerSensorModule.OUTMODE {
            return EnumUtils.load(AccelerometerSensorModule.OUTMODE::class.java, "ValkyrienComputers\$outputMode", tag)
        }

        fun loadFromStack(stack: ItemStack): AccelerometerSensorModule.OUTMODE {
            return loadFromTag(stack.orCreateTag)
        }

        fun saveToStack(stack: ItemStack, mode: AccelerometerSensorModule.OUTMODE) {
            val tag = stack.orCreateTag
            EnumUtils.save(mode, "ValkyrienComputers\$outputMode", tag)
        }
    }
}