package battle;

import java.util.List;

public class Battle {
    public List<PokemonInstance> teamLeft;
    public List<PokemonInstance> teamRight;
    public BattleAction actionLeft;

    public Battle(List<PokemonInstance> teamLeft, List<PokemonInstance> teamRight) {
        this.teamLeft = teamLeft;
        this.teamRight = teamRight;
    }

    public void chooseAction(BattleSide side, String action, int... params) {
        
    }
}
