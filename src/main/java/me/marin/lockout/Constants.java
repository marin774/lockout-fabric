package me.marin.lockout;

import net.minecraft.util.Identifier;

public class Constants {

    public static final String NAMESPACE = "lockout";

    public static final int MIN_BOARD_SIZE = 3;
    public static final int MAX_BOARD_SIZE = 7;

    public static final Identifier LOCKOUT_GOALS_TEAMS_PACKET = Identifier.of(NAMESPACE, "lockout_goals_teams");
    public static final Identifier START_LOCKOUT_PACKET = Identifier.of(NAMESPACE, "start_lockout");
    public static final Identifier UPDATE_TOOLTIP = Identifier.of(NAMESPACE, "update_tooltip");
    public static final Identifier COMPLETE_TASK_PACKET = Identifier.of(NAMESPACE, "complete_task");
    public static final Identifier END_LOCKOUT_PACKET = Identifier.of(NAMESPACE, "end_lockout");
    public static final Identifier UPDATE_TIMER_PACKET = Identifier.of(NAMESPACE, "update_timer");
    public static final Identifier LOCKOUT_VERSION_PACKET = Identifier.of(NAMESPACE, "lockout_version");

    public static final Identifier CUSTOM_BOARD_PACKET = Identifier.of(NAMESPACE, "set_custom_board");

    public static final Identifier BOARD_SCREEN_ID = Identifier.of(NAMESPACE, "board");

    public static final Identifier BOARD_FILE_ARGUMENT_TYPE = Identifier.of(NAMESPACE, "board_file");
    public static final Identifier BOARD_SIDE_ARGUMENT_TYPE = Identifier.of(NAMESPACE, "board_side");

    public static final Identifier GUI_IDENTIFIER = Identifier.of(NAMESPACE, "gui");

    public static final int GUI_PADDING = 2; // both x and y
    public static final int GUI_PADDING_BOTTOM = 13; // both x and y
    public static final int GUI_SLOT_SIZE = 18; // both x and y

    public static final Identifier GUI_CENTER_IDENTIFIER = Identifier.of(NAMESPACE, "gui_center");
    public static final int GUI_CENTER_PADDING = 7;
    public static final int GUI_CENTER_SLOT_SIZE = 18; // both x and y

    public static final int GUI_CENTER_HOVERED_COLOR = -2130706433;

    public static final String BOARD_SIDE_LEFT = "left";
    public static final String BOARD_SIDE_RIGHT = "right";

}
