package io.github.techtastic.vc.util

import dan200.computercraft.api.peripheral.IComputerAccess

class CCComputerHandler {
    val computers: MutableList<IComputerAccess> = mutableListOf()

    fun attachComputer(access: IComputerAccess) {
        computers.add(access)
    }

    fun detachComputer(access: IComputerAccess) {
        computers.remove(access)
    }

    fun queueEvent(name: String, objects: Any) {
        computers.forEach {
            it.queueEvent(name, objects)
        }
    }
}