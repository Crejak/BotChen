package io.github.crejak.botchen.battle;

import io.github.crejak.botchen.PokemonInstance;
import io.github.crejak.botchen.Trainer;
import me.sargunvohra.lib.pokekotlin.model.Move;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class UserBattleInterface extends BattleInterface {
    public User user;
    public Trainer trainer;
    public MessageChannel battleChannel;

    public UserBattleInterface(User user, Trainer trainer, MessageChannel battleChannel) {
        super();

        this.user = user;
        this.trainer = trainer;
        this.battleChannel = battleChannel;
    }

    public void input(MessageChannel channel, String[] args) {
        if (args.length < 2) {
            printMainHelp(channel);
            return;
        }

        String cmd = args[1];

        if (cmd.equals("help")) {
            help(channel, args);
        } else if (cmd.equals("battle")) {
            printBattleSummary(channel);
        } else if (cmd.equals("fight") || cmd.equals("move")) {
            fight(channel, args);
        } else if (cmd.equals("pokemon") || cmd.equals("team")) {
            pokemon(channel, args);
        } else if (cmd.equals("bag")) {
            bag(channel, args);
        } else if (cmd.equals("run")) {
            run(channel);
        } else {
            channel.sendMessage("Désolé, je ne connais pas cette commande. Utilise `:p help` pour connaître les actions disponibles.").queue();
        }
    }

    @Override
    protected void onReadyToChooseAction() {
        battleChannel.sendMessage(trainer.name + ", que faire à ce tour ?\n" +
                "```\n" +
                ":p fight      Utiliser une capacité\n" +
                ":p bag        Utiliser un objet\n" +
                ":p pokemon    Changer de Pokémon combattant\n" +
                ":p run        Fuir le combat\n" +
                "```").queue();
    }

    @Override
    protected void onMustSwitch() {
        battleChannel.sendMessage(trainer.name + ", qui doit continuer le combat ?\n" +
                "```\n" +
                ":p pokemon    Changer de Pokémon combattant\n" +
                "```").queue();
    }

    private void printMainHelp(MessageChannel channel) {
        channel.sendMessage("Tu es actuellement en cours de combat Pokémon ! Cela signifie que tu n'as pas " +
                "accès aux mêmes commandes qu'en temps normal. Voilà les actions que tu peux effectuer :\n" +
                "```\n" +
                ":p          Afficher cette aide, équivalent à ':p help'\n" +
                ":p help     Afficher cette aide\n" +
                ":p battle   Afficher le résumé du combat actuel\n" +
                ":p fight    Utiliser une capacité\n" +
                ":p bag      Utiliser un objet du sac\n" +
                ":p pokemon  Échanger le Pokémon combattant avec un autre de ton équipe\n" +
                ":p run      Fuir le combat\n" +
                "```\n" +
                "Comme toujours, tu peux utiliser la commande suivante pour en savoir plus sur une action :\n" +
                "```\n" +
                ":p help <command>  Afficher l'aide pour une commande en particulier\n" +
                "```").queue();
    }

    private void help(MessageChannel channel, String[] args) {
        if (args.length == 2) {
            printMainHelp(channel);
            return;
        }

        String cmd = args[2];
        String text;
        switch (cmd) {
            case "help":
                text = "```\n" +
                        ":p help <command>\n\n" +
                        "Permet d'avoir des informations détaillées sur une commande en particulier.\n" +
                        "Exemple:\n\n" +
                        ":p help battle\n" +
                        "```";
                break;
            case "battle":
                text = "```\n" +
                        ":p battle\n\n" +
                        "Affiche un résumé du combat auquel tu participes actuellement. Cela inclut l'état actuel des " +
                        "Pokémon combattants, et si c'est à toi d'effectuer une action\n" +
                        "```";
                break;
            case "fight":
                text = "```\n" +
                        ":p fight (alias: move)\n\n" +
                        "Affiche la liste des capacités de ton Pokémon combattant. Tu peux choisir d'utiliser une de ces " +
                        "capacités avec la commande suivante :\n\n" +
                        ":p fight <moveIndex>  Utiliser une capacité du Pokémon combattant\n" +
                        ":p fight struggle     Effectuer l'attaque Lutte, si plus aucune autre option n'est possible\n\n" +
                        "Où <moveIndex> indique le numéro de la capacité à utiliser. Retiens que tu ne pourras pas " +
                        "utiliser une capacité pour laquelle ton Pokémon n'a plus de PP ! Si tu ne peux plus utiliser de " +
                        "capacité, ta seule option pour combattre est d'utiliser l'attaque Lutte.\n" +
                        "```";
                break;
            case "bag":
                text = "```\n" +
                        ":p bag\n\n" +
                        "Affiche la liste des objets dans ton sac. Tu peux choisir d'utiliser un de ces " +
                        "objets avec une des commandes suivantes :\n\n" +
                        ":p bag <itemIndex>              Utiliser un objet\n" +
                        ":p bag <itemIndex> <pkmnIndex>  Utiliser un objet sur un Pokémon de l'équipe\n\n" +
                        "Où <itemIndex> indique le numéro de l'objet à utiliser. Certains objets, comme les potions, " +
                        "s'utilisent sur une cible. Dans ce cas, il sera utilisé sur le Pokémon combattant, à moins que " +
                        "tu ne précises un Pokémon de l'équipe avec le paramètre <pkmnIndex>.\n" +
                        "```";
                break;
            case "pokemon":
                text = "```\n" +
                        ":p pokemon (alias: team)\n\n" +
                        "Affiche les Pokémon de ton équipe. Tu peux consulter leur résumé ou choisir d'en appeler un " +
                        "pour remplacer le Pokémon combattant avec les commandes suivantes :\n\n" +
                        ":p pokemon <index>         Afficher le résumé du Pokémon\n" +
                        ":p pokemon switch <index>  Échanger le Pokémon avec le Pokémon combattant\n\n" +
                        "Où <index> indique la position du Pokémon dans l'équipe.\n" +
                        "```";
                break;
            case "run":
                text = "```\n" +
                        ":p run\n\n" +
                        "Permet de fuir un combat. Cette option ne fonctionne que lors d'un combat contre un Pokémon " +
                        "sauvage.\n" +
                        "```";
                break;
            default:
                text = "Désolé, je ne connais pas la commande `" + cmd + "`. Utilise `:p help` pour connaître les actions disponibles.";
        }
        channel.sendMessage(text).queue();
    }

    private void printBattleSummary(MessageChannel channel) {
        String summary = battle.getBattleSummary() + "\n";
        switch (state) {
            case BLOCKED:
                summary += "Ce n'est pas à toi d'agir.";
                break;
            case CHOOSE_ACTION:
                summary += "Tu dois choisir une action pour ce tour :\n" +
                        "```\n" +
                        ":p fight      Utiliser une capacité\n" +
                        ":p bag        Utiliser un objet\n" +
                        ":p pokemon    Changer de Pokémon combattant\n" +
                        ":p run        Fuir le combat\n" +
                        "```";
                break;
            case MUST_SWITCH:
                summary += "Tu dois choisir un nouveau Pokémon combattant :\n" +
                        "```\n" +
                        ":p pokemon   Changer de Pokémon combattant\n" +
                        "```";
        }
        channel.sendMessage(summary).queue();
    }

    private void fight(MessageChannel channel, String[] args) {
        if (state != BattleInterfaceState.CHOOSE_ACTION) {
            channel.sendMessage("Ce n'est pas à toi d'agir !").queue();
            return;
        }

        PokemonInstance battlingPokemon = battle.getBattlingPokemon(side);
        List<Move> moves = battlingPokemon.moves;

        if (args.length == 2) {
            String movesString = battlingPokemon.getMovesAsString();
            channel.sendMessage("Voici les capacités que ton " + battlingPokemon.getNameFr() + " peut utiliser :\n" +
                    "```\n" + movesString + "\n```").queue();
            return;
        }

        String cmd = args[2];
        try {
            int index = Integer.parseInt(cmd);
            if (index < 1 || index > moves.size()) {
                channel.sendMessage("Tu dois indiquer le numéro de la capacité à utilier. Utilise `:p fight` pour connaître les capacités de ton Pokémon.").queue();
                return;
            }
            if (battlingPokemon.getPp(index) == 0) {
                channel.sendMessage("Ton Pokémon n'a plus de PP pour cette capacité !").queue();
                return;
            }
            String reason = "";
            if (!battle.canChooseMove(side, index, reason)) {
                channel.sendMessage(reason).queue();
                return;
            }
            battle.chooseAction(side, BattleAction.Fight(index));
            return;
        } catch (NumberFormatException ignored) {
        }

        if (cmd.equals("struggle")) {
            for (int i = 1; i <= moves.size(); i++) {
                if (battlingPokemon.getPp(i) > 0 && battle.canChooseMove(side, i, null)) {
                    channel.sendMessage("Tu peux encore utiliser des capacités, c'est donc impossible d'utiliser Lutte pour le moment.").queue();
                    return;
                }
            }
            battle.chooseAction(side, BattleAction.Struggle());
            return;
        } else {
            channel.sendMessage("Désolé, je ne connais pas cette commande. Utiliser `:p help fight` pour connaître les options disponibles.").queue();
            return;
        }
    }

    private void bag(MessageChannel channel, String[] args) {
        if (state != BattleInterfaceState.CHOOSE_ACTION) {
            channel.sendMessage("Ce n'est pas à toi d'agir !").queue();
            return;
        }

        channel.sendMessage("Tu n'as pas encore de sac !").queue();
    }

    private void pokemon(MessageChannel channel, String[] args) {
        if (state != BattleInterfaceState.CHOOSE_ACTION && state != BattleInterfaceState.MUST_SWITCH) {
            channel.sendMessage("Ce n'est pas à toi d'agir !").queue();
            return;
        }

        if (args.length == 2) {
            String teamSummary = trainer.getTeamSummary();
            channel.sendMessage(teamSummary).queue();
            return;
        }

        String cmd = args[2];

        try {
            int index = Integer.parseInt(cmd);
            if (index < 1 || index > trainer.team.size()) {
                channel.sendMessage("Tu dois indiquer la position d'un Pokémon dans ton équipe. " +
                        "Utilise `:p pokemon` pour consulter le résumé de ton équipe.").queue();
                return;
            }
            String summary = trainer.getPokemonInTeam(index).toDisplayableString();
            channel.sendMessage(summary).queue();
            return;
        } catch (NumberFormatException ignored) {
        }

        if (cmd.equals("switch")) {
            String reason = "";
            if (!battle.canSwitch(side, reason)) {
                channel.sendMessage(reason).queue();
                return;
            }
            if (args.length < 4) {
                channel.sendMessage("Utilisation de la commande : `:p pokemon switch <index>`.").queue();
                return;
            }
            try {
                int index = Integer.parseInt(args[3]);
                if (index < 1 || index > trainer.team.size()) {
                    channel.sendMessage("Tu dois indiquer la position d'un Pokémon dans ton équipe. " +
                            "Utilise `:p pokemon` pour consulter le résumé de ton équipe.").queue();
                    return;
                }
                if (trainer.getPokemonInTeam(index).isKo()) {
                    channel.sendMessage("Le Pokémon est KO. Impossible de l'envoyer au combat !").queue();
                    return;
                }
                if (state == BattleInterfaceState.CHOOSE_ACTION) {
                    battle.chooseAction(side, BattleAction.Pokemon(index));
                } else if (state == BattleInterfaceState.MUST_SWITCH) {
                    battle.chooseSwitch(side, index);
                }
                return;
            } catch (NumberFormatException ignored) {
                channel.sendMessage("Tu dois indiquer la position d'un Pokémon dans ton équipe. " +
                        "Utilise `:p pokemon` pour consulter le résumé de ton équipe.").queue();
                return;
            }
        } else {
            channel.sendMessage("Désolé, je ne connais pas cette commande. Utilise `:p help pokemon` pour connaître les actions disponibles.").queue();
            return;
        }
    }

    private void run(MessageChannel channel) {
        if (state != BattleInterfaceState.CHOOSE_ACTION) {
            channel.sendMessage("Ce n'est pas à toi d'agir !").queue();
            return;
        }

        if (battle.type == BattleType.AI_TRAINER || battle.type == BattleType.USER_TRAINER) {
            channel.sendMessage("On ne fuit pas d'un combat entre dresseurs !").queue();
            return;
        }

        String reason = "";
        if (!battle.canRun(side, reason)) {
            channel.sendMessage(reason).queue();
            return;
        }

        battle.chooseAction(side, BattleAction.Run());
    }
}
