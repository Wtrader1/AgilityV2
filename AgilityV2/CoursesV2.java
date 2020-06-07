package net.runelite.client.plugins.AgilityV2;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
enum CoursesV2 {
    GNOME(86.5, 46, 9781),
    DRAYNOR(120.0, 79, 12338),
    AL_KHARID(180.0, 0, 13105, new WorldPoint(3299, 3194, 0)),
    PYRAMID(722.0, 0, 13356, new WorldPoint(3364, 2830, 0)),
    VARROCK(238.0, 125, 12853),
    PENGUIN(540.0, 65, 10559),
    BARBARIAN(139.5, 60, 10039),
    CANIFIS(240.0, 175, 13878),
    APE_ATOLL(580.0, 300, 11050),
    FALADOR(440, 180, 12084),
    WILDERNESS(571.0, 499, 11837),
    WEREWOLF(730.0, 380, 14234),
    SEERS(570.0, 435, 10806),
    POLLNIVNEACH(890.0, 540, 13358),
    RELLEKA(780.0, 475, 10553),
    PRIFDDINAS(1337.0, 1037, 12895),
    ARDOUGNE(793.0, 529, 10547);
    private final static Map<Integer, CoursesV2> coursesByRegion;

    @Getter
    private final double totalXp;

    @Getter
    private final int lastObstacleXp;

    @Getter
    private final int regionId;

    @Getter
    private final WorldPoint[] courseEndWorldPoints;

    static
    {
        ImmutableMap.Builder<Integer, CoursesV2> builder = new ImmutableMap.Builder<>();

        for (CoursesV2 course : values())
        {
            builder.put(course.regionId, course);
        }

        coursesByRegion = builder.build();
    }

    CoursesV2(double totalXp, int lastObstacleXp, int regionId, WorldPoint... courseEndWorldPoints)
    {
        this.totalXp = totalXp;
        this.lastObstacleXp = lastObstacleXp;
        this.regionId = regionId;
        this.courseEndWorldPoints = courseEndWorldPoints;
    }

    static CoursesV2 getCourse(int regionId)
    {
        return coursesByRegion.get(regionId);
    }
}
