package net.runelite.client.plugins.AgilityV2;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import static net.runelite.api.ItemID.AGILITY_ARENA_TICKET;
import static net.runelite.api.Skill.AGILITY;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectChanged;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WallObjectChanged;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
@PluginDescriptor(
        name = "AgilityV2",
        description = "Show helpful information about agility courses and obstacles",
        tags = {"grace", "marks", "overlay", "shortcuts", "skilling", "traps"}
)
@PluginDependency(XpTrackerPlugin.class)
@Slf4j
public class AgilityPluginV2 extends Plugin{
    @Getter
    private final Map<TileObject, ObstacleV2> obstacles = new HashMap<>();

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AgilityOverlayV2 agilityOverlay;

    @Inject
    private Notifier notifier;

    @Inject
    private Client client;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private AgilityConfigV2 config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private XpTrackerService xpTrackerService;
    private Locations locations;
    private int Player_X;
    private int Player_Y;
    private int Player_Z;
    private int ReturnID;
    private int lastAgilityXp;
    private WorldPoint lastArenaTicketPosition;

    @Getter
    private int agilityLevel;


    @Provides
    AgilityConfigV2 getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AgilityConfigV2.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(agilityOverlay);
        agilityLevel = client.getBoostedSkillLevel(Skill.AGILITY);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(agilityOverlay);
        obstacles.clear();
        agilityLevel = 0;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case HOPPING:
            case LOGIN_SCREEN:
                lastArenaTicketPosition = null;
                break;
            case LOADING:
                obstacles.clear();
                break;

        }
    }


    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        if (statChanged.getSkill() != AGILITY)
        {
            return;
        }

        agilityLevel = statChanged.getBoostedLevel();

        // Determine how much EXP was actually gained
        int agilityXp = client.getSkillExperience(AGILITY);
        int skillGained = agilityXp - lastAgilityXp;
        lastAgilityXp = agilityXp;

        // Get course
        CoursesV2 course = CoursesV2.getCourse(client.getLocalPlayer().getWorldLocation().getRegionID());
        if (course == null
                || (course.getCourseEndWorldPoints().length == 0
                ? Math.abs(course.getLastObstacleXp() - skillGained) > 1
                : Arrays.stream(course.getCourseEndWorldPoints()).noneMatch(wp -> wp.equals(client.getLocalPlayer().getWorldLocation()))))
        {
            return;
        }
    }
    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getGameObject());
    }

    @Subscribe
    public void onGameObjectChanged(GameObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getGameObject(), null);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectChanged(GroundObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getGroundObject(), null);
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getWallObject());
    }

    @Subscribe
    public void onWallObjectChanged(WallObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getWallObject());
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getWallObject(), null);
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectChanged(DecorativeObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getDecorativeObject(), null);
    }

    private void onTileObject(Tile tile, TileObject oldObject, TileObject newObject)
    {
        obstacles.clear();
        obstacles.remove(oldObject);
        Player_X = client.getLocalPlayer().getWorldLocation().getX();
        Player_Y = client.getLocalPlayer().getWorldLocation().getY();
        Player_Z = client.getLocalPlayer().getWorldLocation().getPlane();

        locations = Locations.forPlayerLocation(Player_X,Player_Y,Player_Z);
        ReturnID = locations.getObjectID();
        if (newObject == null)
        {
            return;
        }
        if (ReturnID == newObject.getId())
        //if (ObstaclesV2.COURSE_OBSTACLE_IDS.contains(newObject.getId()))
        {
            obstacles.put(newObject, new ObstacleV2(tile, null));

        }
    }
}
