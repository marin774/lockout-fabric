package me.marin.lockout;

import net.minecraft.util.Identifier;

public class Constants {

    public static final String NAMESPACE = "lockout";
    public static final Identifier BEGIN_LOCKOUT_PACKET = new Identifier(NAMESPACE, "begin_lockout");
    public static final Identifier COMPLETE_TASK_PACKET = new Identifier(NAMESPACE, "complete_task");
    public static final Identifier GUI_IDENTIFIER = new Identifier(NAMESPACE, "textures/guis/gui.png");

    public static final int GUI_WIDTH = 94;
    public static final int GUI_HEIGHT = 105;
    public static final int GUI_FIRST_ITEM_OFFSET = 3; // both x and y
    public static final int GUI_ITEM_SLOT_SIZE = 18; // both x and y

    public static final Identifier GUI_CENTER_IDENTIFIER = new Identifier(NAMESPACE, "textures/guis/gui_center.png");
    public static final int GUI_CENTER_WIDTH = 104;
    public static final int GUI_CENTER_HEIGHT = 114;
    public static final int GUI_CENTER_FIRST_ITEM_OFFSET_X = 8;
    public static final int GUI_CENTER_FIRST_ITEM_OFFSET_Y = 18;
    public static final int GUI_CENTER_ITEM_SLOT_SIZE = 18; // both x and y


}
