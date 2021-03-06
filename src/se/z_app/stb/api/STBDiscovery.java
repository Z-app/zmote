package se.z_app.stb.api;

import se.z_app.stb.STB;
import se.z_app.stb.api.zenterio.Discovery;

/**
 * Class that searches for STBs on the current network
 * 
 * @author Viktor Dahl
 */
public class STBDiscovery {
	private String subNetAddress;
	private Discovery disc;
	
	/**
	 * Creates a new STBDiscovery with the specified sub-net address
	 * @param subNetAddress - The subnet address to use (form xxx.xxx.xxx.)
	 */
	public STBDiscovery(String subNetAddress) {
		this.subNetAddress = subNetAddress;
	}
	
	/**
	 * Search for all STBs on the current network
	 * @return An array with all STB's that were found.
	 */
	public STB[] find() {
		disc = new Discovery(subNetAddress);
		return disc.find();
	}
	

}
