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

        class COMPUTERCRAFT {
            @JsonSchema(description = "Disable ComputerCraft Integration")
            val disableComputerCraft = false;

            @JsonSchema(description = "Disable Eureka Integration")
            val disableEureka = false;

            @JsonSchema(description = "Disable Radars")
            val disableRadars = false

            @JsonSchema(description = "Disable Ship Readers")
            val disableShipReaders = false

            @JsonSchema(description = "Maximum Range of Radars")
            val maxRadarRadius = 256

            @JsonSchema(description = "Whether or not ship radars give ship rotation")
            val radarGivesDistance = false;
        }

        val OpenComputers = OPENCOMPUTERS()

        class OPENCOMPUTERS {
            @JsonSchema(description = "Disabled OpenComputers Integration")
            val disableOpenComputers = false;

            @JsonSchema(description = "Disable Eureka Integration")
            val disableEureka = false;

            @JsonSchema(description = "Disable Radars")
            val disableRadars = false

            @JsonSchema(description = "Disable Ship Readers")
            val disableShipReaders = false

            @JsonSchema(description = "Maximum Range of Radars")
            val maxRadarRadius = 256
        }
    }
}
