package io.github.crejak.botchen.battle;

import io.github.crejak.botchen.PokemonInstance;

import java.util.List;

public class Battle {
    public List<PokemonInstance> teamLeft;
    public List<PokemonInstance> teamRight;
    public BattleAction actionLeft;
    public BattleAction actionRight;
    public BattleInterface interfaceLeft;
    public BattleInterface interfaceRight;

    public BattleType type;

    public Battle(List<PokemonInstance> teamLeft, List<PokemonInstance> teamRight, BattleInterface interfaceLeft,
                  BattleInterface interfaceRight, BattleType type) {
        this.teamLeft = teamLeft;
        this.teamRight = teamRight;
        this.interfaceLeft = interfaceLeft;
        this.interfaceRight = interfaceRight;

        this.interfaceLeft.battle = this;
        this.interfaceLeft.side = BattleSide.LEFT;
        this.interfaceLeft.init = true;

        this.interfaceRight.battle = this;
        this.interfaceRight.init = true;
        this.interfaceRight.side = BattleSide.RIGHT;

        this.type = type;

        initBattle();
    }

    public void chooseAction(BattleSide side, BattleAction action) {
        switch (side) {
            case LEFT:
                this.actionLeft = action;
                break;
            case RIGHT:
                this.actionRight = action;
                break;
        }

        if (actionLeft == null || actionRight == null) {
            return;
        }

        resolveActions();
    }

    public void chooseSwitch(BattleSide side, int pokemonIndex) {

    }

    private void initBattle() {
        // Y a surement des trucs à gérer dès le début, genre les capacités spéciales comme intimidation

        this.interfaceLeft.readyToChooseAction();
        this.interfaceRight.readyToChooseAction();
    }

    private void resolveActions() {
        // Ici résoudre le tour
        // Faut aussi prendre en compte les effets secondaires d'autres actions

        // si un des deux est ko faut demander aux interfaces de switcher

        // sinon on peut continuer avec readyToChooseAction()
    }

    public PokemonInstance getBattlingPokemon(BattleSide side) {
        if (side == BattleSide.LEFT) {
            return teamLeft.get(0);
        }
        return teamRight.get(0);
    }

    public String getBattleSummary() {
        PokemonInstance left = getBattlingPokemon(BattleSide.LEFT);
        PokemonInstance right = getBattlingPokemon(BattleSide.RIGHT);
        return "```\n" +
                left.getSummary() + "\n" +
                "\n" +
                right.getSummary() + "\n" +
                "```";
    }
}
