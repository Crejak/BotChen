package io.github.crejak.botchen.battle;

public class BattleAction {
    public BattleActionType type;
    public int moveIndex;
    public int itemIndex;
    public int teamIndex;

    public BattleAction(BattleActionType type) {
        this.type = type;
        this.moveIndex = -1;
        this.itemIndex = -1;
        this.teamIndex = -1;
    }

    public static BattleAction Fight(int moveIndex) {
        BattleAction action = new BattleAction(BattleActionType.FIGHT);
        action.moveIndex = moveIndex;
        return action;
    }

    public static BattleAction Run() {
        return new BattleAction(BattleActionType.RUN);
    }

    public static BattleAction Bag(int itemIndex, int teamIndex) {
        BattleAction action = new BattleAction(BattleActionType.BAG);
        action.itemIndex = itemIndex;
        action.teamIndex = teamIndex;
        return action;
    }

    public static BattleAction Pokemon(int teamIndex) {
        BattleAction action = new BattleAction(BattleActionType.POKEMON);
        action.teamIndex = teamIndex;
        return action;
    }
}
