package net.runelite.client.plugins.AgilityV2;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.plugins.chatbox.minimal_json.Location;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
public class AgilityOverlayV2 extends Overlay {
    private static final int MAX_DISTANCE = 2350;
    private final Client client;
    private final AgilityPluginV2 plugin;
    private final AgilityConfigV2 config;
    private Locations locations;
    private int Player_X;
    private int Player_Y;
    private int Player_Z;
    private int ReturnID;
    @Inject
    private AgilityOverlayV2(Client client, AgilityPluginV2 plugin, AgilityConfigV2 config){
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
        Point mousePosition = client.getMouseCanvasPosition();
        Player_X = client.getLocalPlayer().getWorldLocation().getX();
        Player_Y = client.getLocalPlayer().getWorldLocation().getY();
        Player_Z = client.getLocalPlayer().getWorldLocation().getPlane();

        locations = Locations.forPlayerLocation(Player_X,Player_Y,Player_Z);
        ReturnID = locations.getObjectID();

        plugin.getObstacles().forEach((object, obstacle) ->
        {
            if(Locations.getAgilityLocation().contains(ReturnID)){

                System.out.println(ReturnID);
                return;
            }

            //if (ObstaclesV2.COURSE_OBSTACLE_IDS.contains(ReturnID) && !config.showClickboxes())
            //{
             //   return;
            //}

            Tile tile = obstacle.getTile();
            if (tile.getPlane() == client.getPlane()
                    && object.getLocalLocation().distanceTo(playerLocation) < MAX_DISTANCE)
            {
                // This assumes that the obstacle is not clickable.

                Shape objectClickbox = object.getClickbox();
                if (objectClickbox != null)
                {
                    AgilityShortcut agilityShortcut = obstacle.getShortcut();
                    Color configColor = agilityShortcut == null || agilityShortcut.getLevel() <= plugin.getAgilityLevel() ? config.getOverlayColor() : Color.ORANGE;

                    if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY()))
                    {
                        graphics.setColor(configColor.darker());
                    }
                    else
                    {
                        graphics.setColor(configColor);
                    }

                    graphics.draw(objectClickbox);
                    graphics.setColor(new Color(configColor.getRed(), configColor.getGreen(), configColor.getBlue(), 50));
                    graphics.fill(objectClickbox);
                }
            }

        });
        return null;
    }
}
