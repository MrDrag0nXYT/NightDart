package zxc.MrDrag0nXYT.nightDart.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zxc.MrDrag0nXYT.nightDart.NightDart;
import zxc.MrDrag0nXYT.nightDart.util.Config;
import zxc.MrDrag0nXYT.nightDart.util.Utilities;

import java.util.List;
import java.util.Objects;

public class Command implements CommandExecutor, TabCompleter {

    private final NightDart plugin;
    private final Config config;

    public Command(NightDart plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            org.bukkit.command.@NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings
    ) {

        YamlConfiguration yamlConfiguration = config.getConfig();

        if (strings.length == 0) {
            for (String string : yamlConfiguration.getStringList("messages.usage")) {
                commandSender.sendMessage(Utilities.setColor(string));
            }
            return false;
        }

        switch (strings[0].toLowerCase()) {

            case "give" -> {
                if (commandSender.hasPermission("nightdart.player.give")) {

                    if (strings.length >= 3) {
                        int dartCount = 1;
                        try {
                            dartCount = Integer.parseInt(strings[1]);
                        } catch (NumberFormatException e) {
                            for (String string : yamlConfiguration.getStringList("messages.invalid-number")) {
                                commandSender.sendMessage(
                                        Utilities.setColor(string)
                                );
                            }
                            return false;
                        }

                        String playerName = strings[2];

                        ItemStack dartItemStack = new ItemStack(Utilities.getDartMaterial() == null ? Material.SPECTRAL_ARROW : Utilities.getDartMaterial(), dartCount);
                        ItemMeta dartItemMeta = dartItemStack.getItemMeta();

                        dartItemMeta.setDisplayName(
                                Utilities.setColorForItem(yamlConfiguration.getString("dart.name", "<#a880ff>[☄]</#a880ff> <#fcfcfc>Усыпляющий дротик"))
                        );
                        dartItemMeta.setLore(
                                yamlConfiguration.getStringList("dart.lore").stream().map(Utilities::setColorForItem).toList()
                        );
                        dartItemMeta.getPersistentDataContainer().set(
                                Utilities.dartArrowDamage,
                                PersistentDataType.DOUBLE,
                                yamlConfiguration.getDouble("dart.damage", 1)
                        );

                        dartItemStack.setItemMeta(dartItemMeta);

                        if (Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getName().equals(playerName))) {
                            Objects.requireNonNull(Bukkit.getPlayer(playerName)).getInventory().addItem(dartItemStack);

                            for (String string : yamlConfiguration.getStringList("messages.given")) {
                                commandSender.sendMessage(
                                        Utilities.setColor(
                                                string
                                                        .replace("%player%", playerName)
                                                        .replace("%count%", String.valueOf(dartCount))
                                        )
                                );
                            }

                        } else {
                            for (String string : yamlConfiguration.getStringList("messages.not-found")) {
                                commandSender.sendMessage(
                                        Utilities.setColor(
                                                string
                                                        .replace("%player%", playerName)
                                        )
                                );
                            }
                        }
                    }

                } else {
                    for (String string : yamlConfiguration.getStringList("messages.no-permission")) {
                        commandSender.sendMessage(Utilities.setColor(string));
                    }
                    return false;
                }
            }

            case "reload" -> {
                if (commandSender.hasPermission("nightdart.admin.reload")) {
                    plugin.reload();
                    for (String string : yamlConfiguration.getStringList("messages.reloaded")) {
                        commandSender.sendMessage(Utilities.setColor(string));
                    }

                } else {
                    for (String string : yamlConfiguration.getStringList("messages.no-permission")) {
                        commandSender.sendMessage(Utilities.setColor(string));
                    }
                    return false;
                }
            }

            case "help" -> {
                for (String string : yamlConfiguration.getStringList("messages.usage")) {
                    commandSender.sendMessage(Utilities.setColor(string));
                }
            }

            default -> {
                for (String string : yamlConfiguration.getStringList("messages.usage")) {
                    commandSender.sendMessage(Utilities.setColor(string));
                }
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            org.bukkit.command.@NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings
    ) {

        switch (strings.length) {
            case 1 -> {
                return List.of("give", "reload", "help");
            }

            case 2 -> {
                if (strings[0].equalsIgnoreCase("give")) {
                    return List.of("1", "16", "32", "64");
                }
            }

            case 3 -> {
                if (strings[0].equalsIgnoreCase("give")) {
                    return null;
                }
            }

            default -> {
                return List.of();
            }
        }

        return List.of();
    }
}
