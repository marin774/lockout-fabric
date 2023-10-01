package me.marin.lockout.lockout.goals.kill;

public class KillSnowGolemInNetherGoal extends KillSnowGolemGoal {

    public KillSnowGolemInNetherGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill a Snow Golem in The Nether";
    }

}
