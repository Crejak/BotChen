import java.util.*;

public class Trainer {
    public String id;
    public int trainerId;
    public String name;
    public Gender gender;
    public int money;
    public Set<Integer> pokedex;

    public List<PokemonInstance> team;
    public List<PokemonInstance> pc;

    public Trainer(String id, String name, Gender gender) {
        this.id = id;
        this.trainerId = (new Random(System.currentTimeMillis())).nextInt(100_000);
        this.name = name;
        this.gender = gender;
        this.money = 0;
        this.pokedex = new HashSet<>();

        this.team = new ArrayList<>(6);
        this.pc = new ArrayList<>(100);
    }

    public String getTeamSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("```\n");
        sb.append("Il y a ").append(team.size()).append(" Pokémon dans ton équipe.\n");
        for (int i = 0; i < team.size(); i++) {
            PokemonInstance pokemon = team.get(i);
            sb.append(i+1).append(") ").append(pokemon.getSummary()).append("\n");
        }
        sb.append("```");
        return sb.toString();
    }

    public int addPokemon(PokemonInstance pokemon) {
        pokedex.add(pokemon.species.getId());
        if (team.size() < 6) {
            team.add(pokemon);
            return 1;
        } else {
            pc.add(pokemon);
            return 2;
        }
    }

    public PokemonInstance getPokemonInTeam(int index) {
        return team.get(index - 1);
    }

    public void teamSwitch(int indexA, int indexB) {
        PokemonInstance pokemonA = getPokemonInTeam(indexA);
        PokemonInstance pokemonB = getPokemonInTeam(indexB);
        team.set(indexA - 1, pokemonB);
        team.set(indexB - 1, pokemonA);
    }

    public PokemonInstance getPokemonInPc(int index) {
        return pc.get(index - 1);
    }

    public PokemonInstance freePokemon(int indexPc) {
        return pc.remove(indexPc - 1);
    }

    public void dropPokemon(int indexTeam) {
        PokemonInstance pokemon = team.remove(indexTeam - 1);
        pc.add(pokemon);
    }

    public void takePokemon(int indexPc) {
        if (team.size() == 6) {
            return;
        }
        PokemonInstance pokemon = pc.remove(indexPc - 1);
        team.add(pokemon);
    }

    public String getPcSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("```\n");
        sb.append("Il y a ").append(pc.size()).append(" Pokémon dans ton PC.\n");
        for (int i = 0; i < pc.size(); i++) {
            PokemonInstance pokemon = pc.get(i);
            sb.append(i+1).append(") ").append(pokemon.getShortSummary()).append("\n");
        }
        sb.append("```");
        return sb.toString();
    }
}
