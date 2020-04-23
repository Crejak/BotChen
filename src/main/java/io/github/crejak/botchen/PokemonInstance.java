package io.github.crejak.botchen;

import io.github.crejak.botchen.util.Translator;
import me.sargunvohra.lib.pokekotlin.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokemonInstance {
    public Pokemon pokemon;
    public PokemonSpecies species;
    public Nature nature;
    public int level;

    public int gender;

    public int ivHp;
    public int ivAttack;
    public int ivDefense;
    public int ivSpecialAttack;
    public int ivSpecialDefense;
    public int ivSpeed;

    public int evHp;
    public int evAttack;
    public int evDefense;
    public int evSpecialAttack;
    public int evSpecialDefense;
    public int evSpeed;

    public int currentHp;

    public List<Move> moves;
    public List<Integer> currentPps;

    public PokemonInstance(Pokemon pokemon, PokemonSpecies species, Nature nature, int level, List<Move> moves) {
        this.pokemon = pokemon;
        this.species = species;
        this.nature = nature;
        this.level = level;

        Random random = new Random(System.currentTimeMillis());
        if (this.species.getGenderRate() == -1) {
            this.gender = 3;
        } else {
            this.gender = random.nextInt(2) + 1;
        }

        this.ivHp = random.nextInt(32);
        this.ivAttack = random.nextInt(32);
        this.ivDefense = random.nextInt(32);
        this.ivSpecialAttack = random.nextInt(32);
        this.ivSpecialDefense = random.nextInt(32);
        this.ivSpeed = random.nextInt(32);

        this.evHp = 0;
        this.evAttack = 0;
        this.evDefense = 0;
        this.evSpecialAttack = 0;
        this.evSpecialDefense = 0;
        this.evSpeed = 0;

        this.currentHp = this.getHp();

        this.moves = moves;
        this.currentPps = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++) {
            this.currentPps.add(moves.get(i).getPp());
        }
    }

    public int getBaseStat(int statId) {
        List<PokemonStat> stats = pokemon.getStats();
        for (PokemonStat stat :
                stats) {
            if (stat.getStat().getId() == statId) {
                return stat.getBaseStat();
            }
        }
        return 0;
    }
    
    public int getBaseHp() {
        return getBaseStat(1);
    }

    public int getBaseAttack() {
        return getBaseStat(2);
    }

    public int getBaseDefense() {
        return getBaseStat(3);
    }

    public int getBaseSpecialAttack() {
        return getBaseStat(4);
    }

    public int getBaseSpecialDefense() {
        return getBaseStat(5);
    }

    public int getBaseSpeed() {
        return getBaseStat(6);
    }

    public double getNatureModifierForStat(int statId) {
        try {
            if (nature.getIncreasedStat().getId() == statId) {
                return 1.1;
            }
            if (nature.getDecreasedStat().getId() == statId) {
                return 0.9;
            }
        } catch (NullPointerException e) {
            return 1;
        }
        return 1;
    }

    public int getHp() {
        return (int)Math.floor((2d * getBaseHp() + ivHp + Math.floor(evHp / 4d)) * level / 100d) + level + 10;
    }

    public int getIv(int statId) {
        switch (statId) {
            case 1:
                return ivHp;
            case 2:
                return ivAttack;
            case 3:
                return ivDefense;
            case 4:
                return ivSpecialAttack;
            case 5:
                return ivSpecialDefense;
            case 6:
                return ivSpeed;
            default:
                return -1;
        }
    }

    public int getEv(int statId) {
        switch (statId) {
            case 1:
                return evHp;
            case 2:
                return evAttack;
            case 3:
                return evDefense;
            case 4:
                return evSpecialAttack;
            case 5:
                return evSpecialDefense;
            case 6:
                return evSpeed;
            default:
                return -1;
        }
    }

    public int getStat(int statId) {
        double natureMod = getNatureModifierForStat(statId);
        return (int)((Math.floor((2 * getBaseStat(statId) + getIv(statId) + Math.floor(getEv(statId) / 4d)) * level / 100d) + 5d) * natureMod);
    }

    public int getAttack() {
        return getStat(2);
    }

    public int getDefense() {
        return getStat(3);
    }

    public int getSpecialAttack() {
        return getStat(4);
    }

    public int getSpecialDefense() {
        return getStat(5);
    }

    public int getSpeed() {
        return getStat(6);
    }

    public int getPp(int moveIndex) {
        return currentPps.get(moveIndex - 1);
    }

    public String getNameFr() {
        List<Name> names = species.getNames();
        for (Name name :
                names) {
            if (name.getLanguage().getId() == 5) {
                return name.getName();
            }
        }
        return species.getName();
    }

    public String getNatureNameFr() {
        List<Name> names = nature.getNames();
        for (Name name :
                names) {
            if (name.getLanguage().getId() == 5) {
                return name.getName();
            }
        }
        return species.getName();
    }

    public String getGenderNameFr() {
        switch (this.gender) {
            case 1:
                return "Femelle";
            case 2:
                return "Mâle";
            case 3:
                return "Asexué";
            default:
                return null;
        }
    }

    public String getShortGenderNameFr() {
        switch (this.gender) {
            case 1:
                return "F";
            case 2:
                return "M";
            case 3:
                return "";
            default:
                return null;
        }
    }

    public String getSummary() {
        String gender = getShortGenderNameFr();
        if (!gender.equals("")) {
            gender = " (" + gender + ")";
        }
        return getNameFr() + gender + " nv. " + level + "\n" +
                "   PV " + currentHp + "/" + this.getHp();
    }

    public String getShortSummary() {
        String gender = getShortGenderNameFr();
        if (!gender.equals("")) {
            gender = " (" + gender + ")";
        }
        return getNameFr() + gender + " nv. " + level;
    }

    public String toDisplayableString() {
        String gender = getShortGenderNameFr();
        if (!gender.equals("")) {
            gender = " (" + gender + ")";
        }
        String movesString = "";
        for (int i = 0; i < moves.size(); ++i) {
            Move move = moves.get(i);
            movesString += (i+1) + ") " + Translator.getNameFrOrDefault(move.getNames(), move.getName()) + "\n" +
                    "   " + Translator.getTypeNameFr(move.getType().getId()) + ", PP " + getPp(i+1) + "/" + move.getPp() + "\n";
        }

        return "```\n" +
                getNameFr() + gender + " nv. " + level + "\n" +
                getNatureNameFr() + " de nature\n" +
                "IVs: " + ivHp + ", " +
                ivAttack + ", " +
                ivDefense + ", " +
                ivSpecialAttack + ", " +
                ivSpecialDefense + ", " +
                ivSpeed + "\n" +
                "PV            " + getHp() + "\n" +
                "Attaque       " + getAttack() + "\n" +
                "Défense       " + getDefense() + "\n" +
                "Attaque Spé.  " + getSpecialAttack() + "\n" +
                "Défense Spé.  " + getSpecialDefense() + "\n" +
                "Vitesse       " + getSpeed() + "\n" +
                movesString +
                "```";
    }
}
