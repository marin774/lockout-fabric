package me.marin.lockout.lockout.goals.experience;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.ReachXPLevelGoal;
import net.minecraft.util.Identifier;

public class ReachXPLevel30Goal extends ReachXPLevelGoal {

    public ReachXPLevel30Goal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Reach XP level 30";
    }

    @Override
    public int getAmount() {
        return 30;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/experience/level_30.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
}
