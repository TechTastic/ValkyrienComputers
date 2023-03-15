package net.techtastic.vc.util

import org.joml.Quaterniondc
import org.joml.Vector3dc

class SpecialLuaTables {

    companion object {
        @JvmStatic
        fun getVectorAsTable(vec: Vector3dc): MutableMap<Any, Any> = mutableMapOf(
                Pair("x", vec.x()),
                Pair("y", vec.y()),
                Pair("z", vec.z())
        )

        @JvmStatic
        fun getQuaternionAsTable(rot : Quaterniondc): MutableMap<Any, Any> = mutableMapOf(
                Pair("x", rot.x()),
                Pair("y", rot.y()),
                Pair("z", rot.z()),
                Pair("w", rot.w())
        )

        @JvmStatic
        fun getObjectAsArrayList(obj : Any) : MutableList<Any> = mutableListOf(obj)
    }
}