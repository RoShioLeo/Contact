package cloud.lemonslice.contact.common.config;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;

import java.util.List;

public class ContactConfig extends MidnightConfig
{
    @Comment(centered = true)
    public static MidnightConfig.Comment contraband;
    @Entry
    public static List<String> blacklistID = Lists.newArrayList("contact:parcel", "minecraft:shulker_box", "minecraft:bundle",
            "minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box",
            "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box",
            "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box",
            "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box");

    @Server
    @Comment(centered = true)
    public static MidnightConfig.Comment mail;
    @Server
    @Entry(min = 0, max = 1200, isSlider = true)
    public static int postalSpeed = 4;

    @Server
    @Entry(min = 0, max = 12000, isSlider = true)
    public static int ticksToAnotherWorld = 1200;

    @Client
    @Comment(centered = true)
    public static MidnightConfig.Comment gui;
    @Client
    @Entry
    public static boolean showNewMailToast = true;
}
