package io.github.crejak.botchen.battle;

import io.github.crejak.botchen.PokemonInstance;
import me.sargunvohra.lib.pokekotlin.model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasicAiBattleInterface extends BattleInterface {
    public Random random;

    public BasicAiBattleInterface() {
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    protected void onReadyToChooseAction() {
        PokemonInstance battlingPokemon = battle.getBattlingPokemon(side);
        List<Move> moves = battlingPokemon.moves;
        List<Integer> options = new ArrayList<>();

        for (int i = 1; i <= moves.size(); i++) {
            if (battlingPokemon.getPp(i) > 0 && battle.canChooseMove(side, i, null)) {
                options.add(i);
            }
        }

        if (options.size() == 0) {
            battle.chooseAction(side, BattleAction.Struggle());
        } else {
            int moveToUse = options.get(random.nextInt(options.size()));
            battle.chooseAction(side, BattleAction.Fight(moveToUse));
        }
    }

    @Override
    protected void onMustSwitch() {
        List<PokemonInstance> team = battle.getTeam(side);
        List<Integer> options = new ArrayList<>();

        for (int i = 2; i <= team.size(); i++) {
            if (!team.get(i-1).isKo()) {
                options.add(i);
            }
        }

        int pokemonToSwitch = options.get(random.nextInt(options.size()));
        battle.chooseSwitch(side, pokemonToSwitch);
    }
}
