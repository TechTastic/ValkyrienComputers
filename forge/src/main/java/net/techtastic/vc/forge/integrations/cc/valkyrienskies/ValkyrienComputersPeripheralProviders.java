package net.techtastic.vc.forge.integrations.cc.valkyrienskies;

import dan200.computercraft.api.ComputerCraftAPI;

public class ValkyrienComputersPeripheralProviders {
	public static void registerPeripheralProviders() {
		ComputerCraftAPI.registerPeripheralProvider(new RadarPeripheralProvider());
		ComputerCraftAPI.registerPeripheralProvider(new ShipReaderPeripheralProvider());
	}
}
