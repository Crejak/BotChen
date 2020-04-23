import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Nature;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

import java.util.Random;

public class Pokedex {
    public PokeApi pokeApi;
    public final int KNOWN_POKEMON_COUNT = 151;

    public Pokedex() {
        this.pokeApi = new PokeApiClient();
    }

    public PokemonInstance getRandomPokemon() {
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(KNOWN_POKEMON_COUNT) + 1;
        int natureIndex = random.nextInt(25) + 1;
        int level = random.nextInt(100) + 1;
        Pokemon pokemon = pokeApi.getPokemon(index);
        PokemonSpecies species = pokeApi.getPokemonSpecies(pokemon.getSpecies().getId());
        Nature nature = pokeApi.getNature(natureIndex);
        return new PokemonInstance(pokemon, species, nature, level);
    }
}
