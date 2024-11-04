package zxc.MrDrag0nXYT.nightDart.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.UUID;

public class Utilities {

    public static final NamespacedKey dartArrowDamage = new NamespacedKey("nightdart", "dart_arrow_damage");
    private static Material dartMaterial;
    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    public static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component setColor(String text) {
        return miniMessage.deserialize(text);
    }

    public static String setColorForItem(String text) {
        return LegacyComponentSerializer.legacySection().serialize(
                miniMessage.deserialize(text)
        );
    }

    public static Material getDartMaterial() {
        return dartMaterial;
    }

    public static void setDartMaterial(Material dartMaterial) {
        Utilities.dartMaterial = dartMaterial;
    }
}
