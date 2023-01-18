package net.techtastic.vc.fabric.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;

public class ValkyrienComputersPeripheralProviders {
	public static void registerPeripheralProviders() {
		ComputerCraftAPI.registerPeripheralProvider(new RadarPeripheralProvider());
		ComputerCraftAPI.registerPeripheralProvider(new ShipReaderPeripheralProvider());
		//ComputerCraftAPI.registerPeripheralProvider(new MotorPeripheralProvider());
	}
}
