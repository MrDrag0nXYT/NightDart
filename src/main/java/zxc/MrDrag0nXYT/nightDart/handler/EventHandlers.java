package zxc.MrDrag0nXYT.nightDart.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import zxc.MrDrag0nXYT.nightDart.NightDart;
import zxc.MrDrag0nXYT.nightDart.util.Config;
import zxc.MrDrag0nXYT.nightDart.util.Utilities;

import java.util.*;

public class EventHandlers implements Listener {

    private final NightDart plugin;
    private final Config config;

    public EventHandlers(NightDart plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    private static final Map<UUID, Double> dartDamageMap = new HashMap<>();
    private static final List<UUID> crawledPlayers = new ArrayList<>();
    private static final Map<UUID, Long> dartCooldown = new HashMap<>();

    @EventHandler
    public void dartThrow(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack dart = player.getInventory().getItemInMainHand();

        if (
                event.getAction().isRightClick() &&
                        event.getHand() == EquipmentSlot.HAND &&
                        dart.getType() == Utilities.getDartMaterial()
        ) {
            ItemMeta dartMeta = dart.getItemMeta();

            if (dartMeta != null) {
                PersistentDataContainer container = dartMeta.getPersistentDataContainer();

                if (container.has(Utilities.dartArrowDamage, PersistentDataType.DOUBLE)) {

                    if (dartCooldown.containsKey(player.getUniqueId())) {
                        if (!player.hasPermission("nightdart.player.use")) {
                            for (String string : config.getConfig().getStringList("messages.no-permission")) {
                                player.sendMessage(Utilities.setColor(string));
                            }
                            return;
                        }

                        long timeLeft = dartCooldown.get(player.getUniqueId()) - System.currentTimeMillis();
                        for (String string : config.getConfig().getStringList("messages.cooldown")) {
                            player.sendMessage(Utilities.setColor(
                                    string.replace("%timeLeft%", String.valueOf(timeLeft / 1000))
                            ));
                        }
                        return;
                    }

                    if (player.getGameMode() != GameMode.CREATIVE) {
                        int amount = player.getInventory().getItemInMainHand().getAmount();
                        player.getInventory().getItemInMainHand().setAmount(amount - 1);
                    }
                    Arrow projectile = player.launchProjectile(Arrow.class);
                    dartDamageMap.put(projectile.getUniqueId(), config.getConfig().getDouble("dart.damage", 1));
                    projectile.setVelocity(player.getLocation().getDirection().multiply(
                            config.getConfig().getDouble("velocity-multiplier", 2)
                    ));

                    dartCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (config.getConfig().getLong("dart.cooldown", 2) * 1000));
                    Bukkit.getScheduler().runTaskLater(plugin, () -> dartCooldown.remove(player.getUniqueId()), (config.getConfig().getLong("dart.cooldown", 2) * 20));
                }
            }
        }
    }

    @EventHandler
    public void dartHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile instanceof Arrow) {
            Arrow dart = (Arrow) projectile;
            UUID dartUUID = dart.getUniqueId();
            ItemStack dartItemStack = dart.getItemStack();
            ItemMeta dartItemMeta = dartItemStack.getItemMeta();

            if (
                    dartItemMeta != null &&
                            dart.getShooter() instanceof Player shooter
            ) {
                if (dartDamageMap.containsKey(dartUUID)) {
                    YamlConfiguration yamlConfiguration = config.getConfig();

                    double damage = dartDamageMap.get(dartUUID);

                    Entity hitEntity = event.getHitEntity();

                    if (hitEntity instanceof Player hitPlayer) {
                        hitPlayer.damage(damage, shooter);

                        ConfigurationSection effectSection = yamlConfiguration.getConfigurationSection("dart.effects");
                        if (effectSection != null) {
                            for (String effect : effectSection.getKeys(false)) {
                                try {
                                    hitPlayer.addPotionEffect(
                                            new PotionEffect(
                                                    PotionEffectType.getByName(effect),
                                                    yamlConfiguration.getInt("dart.effects." + effect + ".duration", 1) * 20,
                                                    yamlConfiguration.getInt("dart.effects." + effect + ".amplifier", 1)
                                            )
                                    );

                                } catch (Exception ignored) {
                                }
                            }
                        }

                        Location crawlBlockLocation = hitPlayer.getLocation().add(0, 1, 0);
                        Block crawlBlock = crawlBlockLocation.getBlock();

                        Material oldMaterial = crawlBlock.getType();
                        if (crawlBlock.getType() == Material.AIR) {
                            crawlBlock.setType(Material.BARRIER);
                        }
                        crawledPlayers.add(hitPlayer.getUniqueId());

                        if (yamlConfiguration.getBoolean("title.enabled", false)) {
                            Component title = Utilities.setColor(yamlConfiguration.getString("title.title", ""));
                            Component subtitle = Utilities.setColor(yamlConfiguration.getString("title.subtitle", ""));
                            Component actionbar = Utilities.setColor(yamlConfiguration.getString("title.actionbar", ""));

                            long fadeIn = yamlConfiguration.getLong("title.time.fade-in", 10);
                            long stay = yamlConfiguration.getLong("title.time.stay", 70);
                            long fadeOut = yamlConfiguration.getLong("title.time.fade-out", 20);

                            hitPlayer.sendTitlePart(TitlePart.TITLE, title);
                            hitPlayer.sendTitlePart(TitlePart.SUBTITLE, subtitle);
                            hitPlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut)));
                            hitPlayer.sendActionBar(actionbar);
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                crawlBlock.setType(oldMaterial);
                                crawledPlayers.remove(hitPlayer.getUniqueId());
                            }
                        }.runTaskLater(plugin, yamlConfiguration.getLong("dart.crawl-time", 1) * 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void crawledPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (crawledPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
