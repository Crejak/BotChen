package io.github.crejak.botchen.battle;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.tools.javac.util.DefinedBy;
import io.github.crejak.botchen.PokemonInstance;
import io.github.crejak.botchen.util.ApiConstants;
import io.github.crejak.botchen.util.Values;
import me.sargunvohra.lib.pokekotlin.model.Move;
import me.sargunvohra.lib.pokekotlin.model.MoveMetaData;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;
import java.util.Random;

public class Battle {
    public List<PokemonInstance> teamLeft;
    public List<PokemonInstance> teamRight;
    public BattleAction actionLeft;
    public BattleAction actionRight;
    public BattleInterface interfaceLeft;
    public BattleInterface interfaceRight;

    public BattleType type;

    public MessageChannel battleChannel;

    public Random random;

    public Weather weather;

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

        this.random = new Random(System.currentTimeMillis());

        this.weather = Weather.CLEAR;

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

        PokemonInstance battlingPokemon = getBattlingPokemon(side);
        List<PokemonInstance> team = getTeam(side);
        team.set(0, team.get(pokemonIndex-1));
        team.set(pokemonIndex-1, battlingPokemon);
        battleChannel.sendMessage((side == BattleSide.LEFT ? "LEFT" : "RIGHT") + " a appelé " + team.get(0).getNameFr() + " au combat !").queue();

        if (interfaceLeft.state == BattleInterfaceState.BLOCKED && interfaceRight.state == BattleInterfaceState.BLOCKED) {
            interfaceLeft.readyToChooseAction();
            interfaceRight.readyToChooseAction();
            battleChannel.sendMessage(getBattleSummary()).queue();
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

    private BattleSide comparePriority() {
        if (actionLeft.priority > actionRight.priority) {
            return BattleSide.LEFT;
        }
        if (actionRight.priority > actionLeft.priority) {
            return BattleSide.RIGHT;
        }
        if (actionLeft.priority > 0) {
            return BattleSide.LEFT;
        }
        Move moveLeft = getBattlingPokemon(BattleSide.LEFT).getMove(actionLeft.moveIndex);
        Move moveRight = getBattlingPokemon(BattleSide.RIGHT).getMove(actionRight.moveIndex);
        if (moveLeft.getPriority() > moveRight.getPriority()) {
            return BattleSide.LEFT;
        }
        if (moveRight.getPriority() > moveLeft.getPriority()) {
            return BattleSide.RIGHT;
        }
        int speedLeft = getBattlingPokemon(BattleSide.LEFT).getModifiedSpeed();
        int speedRight = getBattlingPokemon(BattleSide.RIGHT).getModifiedSpeed();
        if (speedLeft > speedRight) {
            return BattleSide.LEFT;
        }
        if (speedRight > speedLeft) {
            return BattleSide.RIGHT;
        }
        return random.nextInt(2) == 1 ? BattleSide.LEFT : BattleSide.RIGHT;
    }

    private void resolveActions() {
        BattleSide first = comparePriority();
        BattleSide second = first == BattleSide.LEFT ? BattleSide.RIGHT : BattleSide.LEFT;

        resolveAction(first);
        resolveAction(second);

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
            battleChannel.sendMessage(getBattleSummary()).queue();
            interfaceLeft.readyToChooseAction();
            interfaceRight.readyToChooseAction();
        }
    }

    public BattleAction getAction(BattleSide side) {
        return side == BattleSide.LEFT ? actionLeft : actionRight;
    }

    private void resolveAction(BattleSide side) {
        BattleAction action = getAction(side);
        List<PokemonInstance> team = getTeam(side);
        PokemonInstance battlingPokemon = getBattlingPokemon(side);
        PokemonInstance opponentPokemon = getOpponentPokemon(side);

        switch (action.type) {
            case RUN:
                //TODO implementer run
                break;
            case POKEMON:
                team.set(0, team.get(action.teamIndex-1));
                team.set(action.teamIndex-1, battlingPokemon);
                battlingPokemon.resetModifiers();
                break;
            case BAG:
                //TODO implementer les items mdr
                break;
            case FIGHT:
                resolveMove(side, action.moveIndex);
                break;
            case STRUGGLE:
                //TODO implementer struggle
                break;
        }
    }

    private void resolveMove(BattleSide side, int moveIndex) {
        PokemonInstance battlingPokemon = getBattlingPokemon(side);
        PokemonInstance opponentPokemon = getOpponentPokemon(side);
        Move move = battlingPokemon.getMove(moveIndex);
        MoveMetaData meta = move.getMeta();
        int moveCategory = meta.getCategory().getId();

        switch (moveCategory) {
            case ApiConstants
                    .MOVE_CATEGORY_DAMAGE:
                int damage = computeDamage(battlingPokemon, opponentPokemon, move);
            //TODO continuer ici mdr
        }

        battlingPokemon.reducePp(moveIndex, 1);
    }

    private int computeDamage(PokemonInstance battlingPokemon, PokemonInstance opponentPokemon, Move move) {
        int pokemonTypeA = battlingPokemon.pokemon.getTypes().get(0).getType().getId();
        int pokemonTypeB = battlingPokemon.pokemon.getTypes().size() > 1 ?
                battlingPokemon.pokemon.getTypes().get(1).getType().getId() :
                -1;
        int opponentTypeA = opponentPokemon.pokemon.getTypes().get(0).getType().getId();
        int opponentTypeB = opponentPokemon.pokemon.getTypes().size() > 1 ?
                opponentPokemon.pokemon.getTypes().get(1).getType().getId() :
                -1;

        MoveMetaData meta = move.getMeta();
        int moveType = move.getType().getId();

        double critChance = Values.getCritChance(meta.getCritRate());
        boolean crit = random.nextDouble() <= critChance;
        double a, d;
        if (move.getDamageClass().getId() == ApiConstants.DAMAGE_CLASS_PHYSICAL) {
            a = battlingPokemon.getModifiedAttack();
            d = opponentPokemon.getModifiedDefense();
            if (crit) {
                a = Math.max(a, battlingPokemon.getAttack());
                d = Math.max(a, opponentPokemon.getDefense());
            }
        } else {
            a = battlingPokemon.getModifiedAttack();
            d = opponentPokemon.getModifiedDefense();
            if (crit) {
                a = Math.max(a, battlingPokemon.getAttack());
                d = Math.max(a, opponentPokemon.getDefense());
            }
        }
        double ratioAD = a / d;
        double weatherMod = 1;
        if (weather == Weather.RAIN && moveType == ApiConstants.TYPE_FIRE || weather == Weather.HARSH_SUNLIGHT && moveType == ApiConstants.TYPE_WATER) {
            weatherMod = 0.5;
        }
        if (weather == Weather.RAIN && moveType == ApiConstants.TYPE_WATER || weather == Weather.HARSH_SUNLIGHT && moveType == ApiConstants.TYPE_FIRE) {
            weatherMod = 1.5;
        }
        double critMod = crit ? 1.5 : 1;
        double randomMod = random.nextDouble() * 0.15 + 0.85;
        double stabMod = pokemonTypeA == moveType || pokemonTypeB == moveType ? 1.5 : 1;
        double typeEffectiveness = Values.getTypeEffectiveness(moveType, opponentTypeA);
        if (opponentTypeB != -1) {
            typeEffectiveness *= Values.getTypeEffectiveness(moveType, opponentTypeB);
        }
        //burn mod
        //other mods
        int damage = (int)Math.ceil((((2d * battlingPokemon.level / 5d + 2d) * move.getPower() * ratioAD) / 50d + 2d)
                * weatherMod * critMod * randomMod * stabMod * typeEffectiveness);
        return damage;
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

    public PokemonInstance getOpponentPokemon(BattleSide side) {
        if (side == BattleSide.RIGHT) {
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
