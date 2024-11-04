package zxc.MrDrag0nXYT.nightDart.util;

import org.bukkit.configuration.file.YamlConfiguration;
import zxc.MrDrag0nXYT.nightDart.NightDart;

import java.io.File;
import java.util.List;

public class Config {

    private final NightDart plugin;

    private final String fileName;
    private File file;
    private YamlConfiguration config;

    public Config(NightDart plugin) {
        this.plugin = plugin;
        this.fileName = "config.yml";

        init();
        updateConfig();
    }

    private void init() {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        try {
            config.load(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }



    /*
     * Checking config values
     */

    private void checkConfigValue(String key, Object defaultValue) {
        if (!config.contains(key)) {
            config.set(key, defaultValue);
        }
    }

    private void updateConfig() {
        checkConfigValue("enable-metrics", true);

        checkConfigValue("dart.material", "SPECTRAL_ARROW");
        checkConfigValue("dart.name", "<#a880ff>[☄]</#a880ff> <#fcfcfc>Усыпляющий дротик");
        checkConfigValue("dart.lore", List.of(" ", " <#fffafa>Усыпляет жертву на 5 секунд ", " "));
        checkConfigValue("dart.damage", 1);
        checkConfigValue("dart.velocity-multiplier", 2);
        checkConfigValue("dart.crawl-time", 3);
        checkConfigValue("dart.cooldown", 2);

        checkConfigValue("dart.effects.BLINDNESS.duration", 3);
        checkConfigValue("dart.effects.BLINDNESS.amplifier", 1);

        checkConfigValue("title.enabled", true);
        checkConfigValue("title.title", "<gradient:#aaaaaa:#fcfcfc:#aaaaaa>Оглушён!</gradient>");
        checkConfigValue("title.subtitle", "<#fcfcfc>В вас попали <#a880ff>усыпляющим дротиком</#a880ff>");
        checkConfigValue("title.actionbar", "");
        checkConfigValue("title.time.fade-in", 10);
        checkConfigValue("title.time.stay", 70);
        checkConfigValue("title.time.fade-out", 20);

        checkConfigValue("messages.no-permission", List.of("<#745c97>NightDart <#c0c0c0>› <#dc143c>У вас недостаточно прав для выполнения этой команды!"));
        checkConfigValue("messages.usage", List.of(
                "<#745c97>NightDart <#c0c0c0>› <#fcfcfc>Информация",
                " <#c0c0c0>‣ <click:suggest_command:'/nightdart reload'><#745c97>/nightdart reload</click> <#c0c0c0>- <#fcfcfc>перезагрузить плагин",
                " <#c0c0c0>‣ <click:suggest_command:'/nightdart give'><#745c97>/nightdart give [ник] [количество]</click> <#c0c0c0>- <#fcfcfc>получить дротик"
        ));
        checkConfigValue("messages.given", List.of("<#745c97>NightDart <#c0c0c0>› <#fcfcfc>Выдано %count% дротиков игроку %player%"));
        checkConfigValue("messages.invalid-number", List.of("<#745c97>NightDart <#c0c0c0>› <#fcfcfc>Вы ввели неправильное число!"));
        checkConfigValue("messages.not-found", List.of("<#745c97>NightDart <#c0c0c0>› <#fcfcfc>Игрок %player% не найден"));
        checkConfigValue("messages.reloaded", List.of("<#745c97>NightDart <#c0c0c0>› <#00ff7f>Плагин успешно перезагружен!"));
        checkConfigValue("messages.cooldown", List.of("<#745c97>NightDart <#c0c0c0>› <#dc143c>Подождите ещё %timeLeft% секунд перед повторным выстрелом!"));

        save();
    }

}