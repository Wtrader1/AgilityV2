package net.runelite.client.plugins.AgilityV2;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.runelite.api.Tile;
import net.runelite.client.game.AgilityShortcut;

@Value
@AllArgsConstructor
class ObstacleV2 {
    private final Tile tile;
    @Nullable
    private final AgilityShortcut shortcut;
}
