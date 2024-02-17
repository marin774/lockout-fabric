package me.marin.lockout.json;

import java.util.List;

public class JSONBoard {

    public List<JSONGoal> goals;

    public static class JSONGoal {
        public String id;
        public String data;
    }

}
