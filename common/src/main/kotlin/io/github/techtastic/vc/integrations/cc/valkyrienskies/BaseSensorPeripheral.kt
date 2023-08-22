package io.github.techtastic.vc.integrations.cc.valkyrienskies

import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import io.github.techtastic.vc.block.entity.BaseSensorBlockEntity

abstract class BaseSensorPeripheral<in T: BaseSensorBlockEntity>(private val t: T): IPeripheral {
    override fun attach(computer: IComputerAccess) {
        super.attach(computer)

        t.ccHandler.attachComputer(computer)
    }

    override fun detach(computer: IComputerAccess) {
        super.detach(computer)

        t.ccHandler.detachComputer(computer)
    }
}