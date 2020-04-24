package io.github.crejak.botchen.battle;

import io.github.crejak.botchen.PokemonInstance;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

public class Battle {
    public List<PokemonInstance> teamLeft;
    public List<PokemonInstance> teamRight;
    public BattleAction actionLeft;
    public BattleAction actionRight;
    public BattleInterface interfaceLeft;
    public BattleInterface interfaceRight;

    public BattleType type;

    public MessageChannel battleChannel;

    public Battle(List<PokemonInstance> teamLeft, List<PokemonInstance> teamRight, BattleInterface interfaceLeft,
                  BattleInterface interfaceRight, BattleType type, MessageChannel channel) {
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

        this.battleChannel = channel;

        initBattle();
    }

    public void chooseAction(BattleSide side, BattleAction action) {
        switch (side) {
            case LEFT:
                this.actionLeft = action;
                this.interfaceLeft.block();
                break;
            case RIGHT:
                this.actionRight = action;
                this.interfaceRight.block();
                break;
        }

        if (actionLeft == null || actionRight == null) {
            return;
        }

        resolveActions();
    }

    public void chooseSwitch(BattleSide side, int pokemonIndex) {
        switch (side) {
            case LEFT:
                interfaceLeft.block();
                break;
            case RIGHT:
                interfaceRight.block();
                break;
        }

        //TODO gérer le switch

        if (interfaceLeft.state == BattleInterfaceState.BLOCKED && interfaceRight.state == BattleInterfaceState.BLOCKED) {
            interfaceLeft.readyToChooseAction();
            interfaceRight.readyToChooseAction();
        }
    }

    public boolean canChooseMove(BattleSide side, int moveIndex, String reason) {
        reason = null;
        return true;
        //TODO gérer les cas style entrave
    }

    public boolean canSwitch(BattleSide side, String reason) {
        reason = null;
        return true;
        //TODO gérer les capacités qui empêchent de switch
    }

    public boolean canRun(BattleSide side, String reason) {
        reason = null;
        return true;
        //TODO gérer regard noir et autre
    }

    private void initBattle() {
        // Y a surement des trucs à gérer dès le début, genre les capacités spéciales comme intimidation

        battleChannel.sendMessage(getBattleSummary()).queue();

        this.interfaceLeft.readyToChooseAction();
        this.interfaceRight.readyToChooseAction();
    }

    private void resolveActions() {
        // Ici résoudre le tour
        // Faut aussi prendre en compte les effets secondaires d'autres actions
        StringBuilder sb = new StringBuilder();

        // D'abord le switch
        if (actionLeft.type == BattleActionType.POKEMON) {
            sb.append("Left switches to ").append(actionLeft.teamIndex).append("\n");
        }
        if (actionRight.type == BattleActionType.POKEMON) {
            sb.append("Right switches to ").append(actionRight.teamIndex).append("\n");
        }

        // Ensuite les items
        if (actionLeft.type == BattleActionType.BAG) {
            sb.append("Left uses item ").append(actionLeft.itemIndex).append("\n");
        }
        if (actionRight.type == BattleActionType.BAG) {
            sb.append("Right uses item ").append(actionRight.itemIndex).append("\n");
        }

        // Ensuite la fuite
        if (actionLeft.type == BattleActionType.RUN) {
            sb.append("Left tries to run").append("\n");
        }
        if (actionRight.type == BattleActionType.RUN) {
            sb.append("Right tries to run").append("\n");
        }

        // Puis les moves
        //TODO déterminer la priorité
        if (actionLeft.type == BattleActionType.FIGHT || actionLeft.type == BattleActionType.STRUGGLE) {
            if (actionLeft.type == BattleActionType.FIGHT) {
                sb.append("Left uses move ").append(actionLeft.moveIndex).append("\n");
            } else {
                sb.append("Left struggles\n");
            }
        }
        if (actionRight.type == BattleActionType.FIGHT || actionRight.type == BattleActionType.STRUGGLE) {
            if (actionLeft.type == BattleActionType.FIGHT) {
                sb.append("Right uses move ").append(actionRight.moveIndex).append("\n");
            } else {
                sb.append("Right struggles\n");
            }
        }

        battleChannel.sendMessage(sb.toString()).queue();

        // On peut reset le tour
        actionLeft = null;
        actionRight = null;

        //TODO Checker la victoire

        // si un des deux est ko faut demander aux interfaces de switcher
        if (getBattlingPokemon(BattleSide.LEFT).isKo()) {
            interfaceLeft.mustSwitch();
        }
        if (getBattlingPokemon(BattleSide.RIGHT).isKo()) {
            interfaceRight.mustSwitch();
        }

        // Peut-on continuer le combat ?
        if (interfaceLeft.state != BattleInterfaceState.MUST_SWITCH && interfaceRight.state != BattleInterfaceState.MUST_SWITCH) {
            interfaceLeft.readyToChooseAction();
            interfaceRight.readyToChooseAction();
        }
    }

    public List<PokemonInstance> getTeam(BattleSide side) {
        switch (side) {
            case RIGHT:
                return teamRight;
            case LEFT:
                return teamLeft;
        }
        return null;
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
