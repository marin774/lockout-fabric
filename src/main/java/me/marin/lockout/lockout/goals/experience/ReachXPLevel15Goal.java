package me.marin.lockout.lockout.goals.experience;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.ReachXPLevelGoal;
import net.minecraft.util.Identifier;

public class ReachXPLevel15Goal extends ReachXPLevelGoal {

    public ReachXPLevel15Goal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Reach XP level 15";
    }

    @Override
    public int getAmount() {
        return 15;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/experience/level_15.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
}
