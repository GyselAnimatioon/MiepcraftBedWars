package ch.gyselanimatioon.miepcraftbedwars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

	public CommandListener() {

	}

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage().toLowerCase();
		if (msg == "hub" || msg == "lobby" || msg == "l" || msg == "exit" || msg.contains("leave")) {
		} else if (!event.getPlayer().hasPermission("miepcraftbedwars.commands.block.bypass")) {
			event.setCancelled(true);
		}
	}
}
