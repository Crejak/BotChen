package io.github.crejak.botchen;

import io.github.crejak.botchen.util.Values;
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

    public int modAttack;
    public int modDefense;
    public int modSpecialAttack;
    public int modSpecialDefense;
    public int modSpeed;
    public int modEvasion;
    public int modAccuracy;

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

        this.modAttack = 0;
        this.modDefense = 0;
        this.modSpecialAttack = 0;
        this.modSpecialDefense = 0;
        this.modSpeed = 0;
        this.modEvasion = 0;
        this.modAccuracy = 0;

        this.currentHp = this.getHp();

        this.moves = moves;
        this.currentPps = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++) {
            this.currentPps.add(moves.get(i).getPp());
        }
    }

    public void resetModifiers() {
        this.modAttack = 0;
        this.modDefense = 0;
        this.modSpecialAttack = 0;
        this.modSpecialDefense = 0;
        this.modSpeed = 0;
        this.modEvasion = 0;
        this.modAccuracy = 0;
    }

    public void changeModDefense(int change) {
        this.modDefense += change;
        if (this.modDefense > 6) {
            this.modDefense = 6;
        }
        if (this.modDefense < -6) {
            this.modDefense = -6;
        }
    }

    public void changeModSpecialAttack(int change) {
        this.modSpecialAttack += change;
        if (this.modSpecialAttack > 6) {
            this.modSpecialAttack = 6;
        }
        if (this.modSpecialAttack < -6) {
            this.modSpecialAttack = -6;
        }
    }

    public void changeModSpecialDefense(int change) {
        this.modSpecialDefense += change;
        if (this.modSpecialDefense > 6) {
            this.modSpecialDefense = 6;
        }
        if (this.modSpecialDefense < -6) {
            this.modSpecialDefense = -6;
        }
    }

    public void changeModSpeed(int change) {
        this.modSpeed += change;
        if (this.modSpeed > 6) {
            this.modSpeed = 6;
        }
        if (this.modSpeed < -6) {
            this.modSpeed = -6;
        }
    }

    public void changeModAccuracy(int change) {
        this.modAccuracy += change;
        if (this.modAccuracy > 6) {
            this.modAccuracy = 6;
        }
        if (this.modAccuracy < -6) {
            this.modAccuracy = -6;
        }
    }

    public void changeModEvasion(int change) {
        this.modEvasion += change;
        if (this.modEvasion > 6) {
            this.modEvasion = 6;
        }
        if (this.modEvasion < -6) {
            this.modEvasion = -6;
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

    public int getModifiedAttack() {
        return (int)Math.floor(getAttack() * Values.getMultiplier(modAttack));
    }

    public int getModifiedDefense() {
        return (int)Math.floor(getDefense() * Values.getMultiplier(modDefense));
    }

    public int getModifiedSpecialAttack() {
        return (int)Math.floor(getSpecialAttack() * Values.getMultiplier(modSpecialAttack));
    }

    public int getModifiedSpecialDefense() {
        return (int)Math.floor(getSpecialDefense() * Values.getMultiplier(modSpecialDefense));
    }

    public int getModifiedSpeed() {
        return (int)Math.floor(getSpeed() * Values.getMultiplier(modSpeed));
    }

    public int getPp(int moveIndex) {
        return currentPps.get(moveIndex - 1);
    }

    public void reducePp(int moveIndex, int ppToSubtract) {
        int currentPp = getPp(moveIndex);
        currentPp -= ppToSubtract;
        if (currentPp < 0) {
            currentPp = 0;
        }
        currentPps.set(moveIndex-1, currentPp);
    }

    public Move getMove(int moveIndex) {
        return moves.get(moveIndex-1);
    }

    public boolean isKo() {
        return currentHp == 0;
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

    public String getMovesAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); ++i) {
            Move move = moves.get(i);
            sb.append(i + 1).append(") ").append(Translator.getNameFrOrDefault(move.getNames(), move.getName())).append("\n")
                    .append("   ").append(Translator.getTypeNameFr(move.getType().getId())).append(", PP ")
                    .append(getPp(i + 1)).append("/").append(move.getPp());
            if (i+1 < moves.size()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String toDisplayableString() {
        String gender = getShortGenderNameFr();
        if (!gender.equals("")) {
            gender = " (" + gender + ")";
        }

        String movesString = getMovesAsString();

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
