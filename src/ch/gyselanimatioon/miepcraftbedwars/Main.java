package ch.gyselanimatioon.miepcraftbedwars;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private Connection connection;
	private String host, database, username, password;
	private int port;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {

		host = getConfig().getString("host");
		port = getConfig().getInt("port");
		database = getConfig().getString("database");
		username = getConfig().getString("username");
		password = getConfig().getString("password");
		
		try {
			openConnection();
			Statement statement = connection.createStatement();

			ResultSet result = statement.executeQuery("SELECT * FROM bw_stats ORDER BY Wins DESC LIMIT 10;");
			int x = -497;
			int top = 1;
			double kd = -1;
			while (result.next()) {
				String uuid = result.getString("Player");
				String name = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid)).getName();
				int wins = Integer.parseInt(result.getString("Wins"));
				double kills = Integer.parseInt(result.getString("Kills"));
				double deaths = Integer.parseInt(result.getString("Deaths"));
				
				if (deaths != 0) {
					kd = (Math.round((kills / deaths) * 100.0) / 100.0);
				} else {
					kd = (Math.round((kills / 1) * 100.0) / 100.0);
				}
				
				// Setting Leaderboard Signs
				Block signBlock = Bukkit.getWorld("world").getBlockAt(x, 36, -225);
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte) 0x2);
				BlockState signState = signBlock.getState();
				if (signState instanceof Sign) {
					Sign sign = (Sign) signState;
					sign.setLine(0, "--- #" + top + " ---");
					sign.setLine(1, name);
					sign.setLine(2, "Wins: " + wins);
					sign.setLine(3, "K/D: " + kd);
					sign.update();
					System.out.println(top + ": " + name + "(" + uuid + ") " + wins + " " + kd);
					
				}
				// Setting Leaderboard Skulls
				Block skullBlock = Bukkit.getWorld("world").getBlockAt(x, 37, -225);
				skullBlock.setType(Material.SKULL);
				skullBlock.setData((byte) 0x2);
				BlockState skullState = skullBlock.getState();
				if (skullState instanceof Skull) {
					Skull skull = (Skull) skullState;
					skull.setRotation(BlockFace.NORTH);
					skull.setSkullType(SkullType.PLAYER);
					skull.setOwner(name);
					skull.update();
				}
				x--;
				top++;
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new QuitListener(), this);
		this.getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onDisable() {
	}

	public void openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed()) {
			return;
		}

		synchronized (this) {
			if (connection != null && !connection.isClosed()) {
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);

		}
	}

}
