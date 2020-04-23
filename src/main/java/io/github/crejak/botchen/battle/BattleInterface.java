package io.github.crejak.botchen.battle;

public abstract class BattleInterface {
    public BattleInterfaceState state;
    public Battle battle;
    public BattleSide side;
    public boolean init;

    public BattleInterface() {
        this.state = BattleInterfaceState.BLOCKED;
        this.side = null;
        this.battle = null;
        this.init = false;
    }

    public final void readyToChooseAction() {
        this.state = BattleInterfaceState.CHOOSE_ACTION;
        this.onReadyToChooseAction();
    }

    protected void onReadyToChooseAction() {

    }

    public final void mustSwitch() {
        this.state = BattleInterfaceState.MUST_SWITCH;
        this.onMustSwitch();
    }

    protected void onMustSwitch() {

    }

    public final void block() {
        this.state = BattleInterfaceState.BLOCKED;
        this.onBlock();
    }

    protected void onBlock() {

    }
}
