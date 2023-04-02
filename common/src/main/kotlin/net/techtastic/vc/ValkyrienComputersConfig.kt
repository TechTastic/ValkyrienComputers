package net.techtastic.vc

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object ValkyrienComputersConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client

    class Server {

        val ComputerCraft = COMPUTERCRAFT()

        val TIS3D = TIS()

        class COMPUTERCRAFT {
            @JsonSchema(description = "Disable ComputerCraft Integration")
            val disableComputerCraft = false

            @JsonSchema(description = "Disable Eureka Integration")
            val disableEurekaIntegration = false

            @JsonSchema(description = "Disable Radars")
            val disableRadars = false

            @JsonSchema(description = "Disable Ship Readers")
            val disableShipReaders = false

            val RadarSettings = RADARSETTINGS()

            class RADARSETTINGS {
                @JsonSchema(description = "Maximum Range of Radars")
                val maxRadarRadius = 256

                val radarGetsName = false
                val radarGetsId = true
                val radarGetsPosition = true
                val radarGetsMass = true
                val radarGetsRotation = false
                val radarGetsVelocity = false
                val radarGetsDistance = false
                val radarGetsSize = false
            }

            @JsonSchema(description = "Disable Gyroscopic Sensors")
            val disableGyros = false

            @JsonSchema(description = "Disable Accelerometers")
            val disableAccels = false

            @JsonSchema(description = "Disable Ultrasonic Sensors")
            val disableSonic = false

            @JsonSchema(description = "Default Alarm Distance for CC Events")
            val defaultAlarmDistance = 3

            @JsonSchema(description = "Max Alarm Distance for CC Events")
            val maxAlarmDistance = 3

            @JsonSchema(description = "Is alarm distance changeable>")
            val alarmDistanceChangable = true

            @JsonSchema(description = "Max Clip Distance for Ultrasonic getClip() method")
            val maxClipDistance = 12
        }

        class TIS {
            @JsonSchema(description = "Disable TIS-3D Integration")
            val disableTIS3D = false

            @JsonSchema(description = "Disable Eureka Integration")
            val disableEurekaIntegration = false

            @JsonSchema(description = "Disable Gyroscopic Sensor Module")
            val disableGyros = false

            @JsonSchema(description = "Disable Accelerometer Sensor Module")
            val disableAccels = false
        }
    }
}
