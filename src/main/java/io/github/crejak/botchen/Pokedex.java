package io.github.crejak.botchen;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.*;

import java.util.*;

public class Pokedex {
    public PokeApi pokeApi;
    public final int KNOWN_POKEMON_COUNT = 151;
    public final int VERSION_GROUP = 18; // US/UM version
    public Random random;

    public Pokedex() {
        this.random = new Random(System.currentTimeMillis());
        this.pokeApi = new PokeApiClient();
    }

    public PokemonInstance getRandomPokemon() {
        int index = random.nextInt(KNOWN_POKEMON_COUNT) + 1;
        int level = random.nextInt(100) + 1;

        return generatePokemon(index, level);
    }

    public PokemonInstance generatePokemon(int speciesIndex, int level) {
        int natureIndex = random.nextInt(25) + 1;
        Pokemon pokemon = pokeApi.getPokemon(speciesIndex);
        PokemonSpecies species = pokeApi.getPokemonSpecies(pokemon.getSpecies().getId());
        Nature nature = pokeApi.getNature(natureIndex);

        List<PokemonMove> moveList = getPokemonMoves(pokemon, level, true);
        List<Move> moves = new ArrayList<>();
        ListIterator<PokemonMove> it = moveList.listIterator(moveList.size());
        while (it.hasPrevious() && moves.size() < 4) {
            PokemonMove move = it.previous();
            moves.add(pokeApi.getMove(move.getMove().getId()));
        }

        return new PokemonInstance(pokemon, species, nature, level, moves);
    }

    private List<PokemonMove> getPokemonMoves(Pokemon pokemon, int maxLevel, boolean filterLearnMethod) {
        List<PokemonMove> moveList = pokemon.getMoves();

        List<PokemonMove> filteredList = new ArrayList<>();
        for (PokemonMove move :
                moveList) {
            PokemonMoveVersion pmv = getVersion(move);
            if (pmv == null) {
                continue;
            }
            if (pmv.getLevelLearnedAt() <= maxLevel &&
                    (!filterLearnMethod || pmv.getMoveLearnMethod().getId() == 1)) {
                filteredList.add(move);
            }
        }

        Collections.sort(filteredList, new Comparator<PokemonMove>() {
            @Override
            public int compare(PokemonMove o1, PokemonMove o2) {
                PokemonMoveVersion pmv1 = getVersion(o1);
                PokemonMoveVersion pmv2 = getVersion(o2);
                return pmv1.getLevelLearnedAt() - pmv2.getLevelLearnedAt();
            }
        });

        return filteredList;
    }
    
    private PokemonMoveVersion getVersion(PokemonMove move) {
        List<PokemonMoveVersion> versions = move.getVersionGroupDetails();
        PokemonMoveVersion foundVersion = null;
        for (PokemonMoveVersion version :
                versions) {
            if (version.getVersionGroup().getId() == VERSION_GROUP && (
                    foundVersion == null ||
                            foundVersion.getMoveLearnMethod().getId() > 1 && version.getMoveLearnMethod().getId() <= 1 ||
                            foundVersion.getMoveLearnMethod().getId() <= 1 && version.getMoveLearnMethod().getId() <= 1 &&
                                    foundVersion.getLevelLearnedAt() < version.getLevelLearnedAt()
                    )) {
                foundVersion = version;
            }
        }
        return foundVersion;
    }
}
