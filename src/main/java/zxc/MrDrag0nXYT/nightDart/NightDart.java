package zxc.MrDrag0nXYT.nightDart;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import zxc.MrDrag0nXYT.nightDart.command.Command;
import zxc.MrDrag0nXYT.nightDart.handler.EventHandlers;
import zxc.MrDrag0nXYT.nightDart.util.Config;
import zxc.MrDrag0nXYT.nightDart.util.Utilities;

import java.util.Objects;

public final class NightDart extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {
        config = new Config(this);

        Material dartMaterial = Material.matchMaterial(config.getConfig().getString("dart.material", "SPECTRAL_ARROW"));
        Utilities.setDartMaterial(dartMaterial == null ? Material.SPECTRAL_ARROW : dartMaterial);

        Objects.requireNonNull(getCommand("nightdart")).setExecutor(new Command(this, config));

        getServer().getPluginManager().registerEvents(new EventHandlers(this, config), this);

        if (config.getConfig().getBoolean("enable-metrics", true)) {
            Metrics metrics = new Metrics(this, 23806);
        }

        sendTitle(true);
    }

    @Override
    public void onDisable() {
        sendTitle(false);
    }

    public void reload() {
        config.reload();
    }


    private void sendTitle(boolean isEnable) {
        String isEnableMessage = isEnable ? "<#ace1af>Plugin successfully loaded!" : "<#d45079>Plugin successfully unloaded!";

        ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(Utilities.setColor(" "));
        sender.sendMessage(Utilities.setColor(" <#a880ff>█▄░█ █ █▀▀ █░█ ▀█▀ █▀▄ ▄▀█ █▀█ ▀█▀</#a880ff>    <#696969>|</#696969>    <#fcfcfc>Version: <#a880ff>" + this.getDescription().getVersion() + "</#a880ff>"));
        sender.sendMessage(Utilities.setColor(" <#a880ff>█░▀█ █ █▄█ █▀█ ░█░ █▄▀ █▀█ █▀▄ ░█░</#a880ff>    <#696969>|</#696969>    <#fcfcfc>Author: <#a880ff>MrDrag0nXYT (https://drakoshaslv.ru)</#a880ff>"));
        sender.sendMessage(Utilities.setColor(" "));
        sender.sendMessage(Utilities.setColor(" " + isEnableMessage));
        sender.sendMessage(Utilities.setColor(" "));
    }
}
