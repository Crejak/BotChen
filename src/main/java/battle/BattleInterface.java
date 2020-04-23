package battle;

public abstract class BattleInterface {
    public BattleInterfaceState state;
    public abstract void readyToChooseAction();
    public abstract void mustSwitch();
}
