package io.github.crejak.botchen.battle;

public class BattleAction {
    public BattleActionType type;
    public int priority;
    public int moveIndex;
    public int itemIndex;
    public int teamIndex;

    public BattleAction(BattleActionType type, int priority) {
        this.type = type;
        this.priority = priority;
        this.moveIndex = -1;
        this.itemIndex = -1;
        this.teamIndex = -1;
    }

    public static BattleAction Fight(int moveIndex) {
        BattleAction action = new BattleAction(BattleActionType.FIGHT, 0);
        action.moveIndex = moveIndex;
        return action;
    }

    public static BattleAction Struggle() {
        return new BattleAction(BattleActionType.STRUGGLE, 0);
    }

    public static BattleAction Run() {
        return new BattleAction(BattleActionType.RUN, 3);
    }

    public static BattleAction Bag(int itemIndex, int teamIndex) {
        BattleAction action = new BattleAction(BattleActionType.BAG, 1);
        action.itemIndex = itemIndex;
        action.teamIndex = teamIndex;
        return action;
    }

    public static BattleAction Pokemon(int teamIndex) {
        BattleAction action = new BattleAction(BattleActionType.POKEMON, 2);
        action.teamIndex = teamIndex;
        return action;
    }
}
