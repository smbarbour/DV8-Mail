package org.smbarbour.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;

public class Mail extends JavaPlugin {
	private EbeanServer database;
	private File pluginPath;
	private final Logger myLogger = Logger.getLogger("Minecraft");
	private PluginManager pm;
	private PluginDescriptionFile info;
	private FileConfiguration myConfig;

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Players.class);
		list.add(Messages.class);
		list.add(Mailboxes.class);
		list.add(Attachments.class);
		return list;
	}

	@Override
	public void onDisable(){
		logMessage(Level.INFO, info.getName() + " is now disabled.");
	}

	@Override
	public void onEnable(){
		pm = getServer().getPluginManager();
		pm.registerEvents(new MailListener(this), this);
		info = this.getDescription();
		logMessage(Level.INFO, info.getName() + " version " + info.getVersion() + " is enabled.");
        if (!new File(pluginPath, "config.yml").isFile()) {
            this.saveDefaultConfig();
        }		
		loadConfig();
		this.database = this.getDatabase();
		loadDatabase();
	}
	
	private void loadDatabase() {
		try {
			database.find(Players.class).findRowCount();
		} catch (PersistenceException pe) {
			logMessage(Level.INFO, "Installing database on first use.");
			installDDL();
		}		
	}

	private void loadConfig() {
		myConfig = this.getConfig();		
	}

	private void logMessage(Level logLevel, String message) {
		myLogger.log(logLevel, "[" + info.getName() + "]: " + message);
	}

}
