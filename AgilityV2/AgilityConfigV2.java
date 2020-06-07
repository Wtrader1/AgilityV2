package net.runelite.client.plugins.AgilityV2;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
@ConfigGroup("agility")
public interface AgilityConfigV2 extends Config{
    @ConfigItem(
            keyName = "showClickboxes",
            name = "Show Clickboxes",
            description = "Show agility course obstacle clickboxes",
            position = 0
    )
    default boolean showClickboxes()
    {
        return true;
    }
    @ConfigItem(
            keyName = "configColor",
            name = "Overlay Color",
            description = "Color of Agility overlay",
            position = 2
    )
    default Color getOverlayColor()
    {
        return Color.GREEN;
    }
}
