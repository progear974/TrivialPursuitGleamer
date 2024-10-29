package ai.gleamer.ugly;

import java.util.ArrayList;
import java.util.LinkedList;

/*
*   Une classe qui ne respectent pas plusieurs bonnes pratiques bonnes pratiques SOLID, KISS, DRY.
    - Avec plusieurs responsabilités : gestion du jeu, gestion des joueurs, gestion des questions, gestion de l'affichage
 *  - Classe qui n'utilise pas d'interface (couplage fort)
 *  - Aucun check pour vérifier les données entrantes (roll)
 *  - Mauvaises utilisation des modificateurs d'accès
 *  - Pas de vérification sur la limite des tableaux
 *
 * */
public class Game {

    /* Responsabilité qui ne concerne pas Game : gestion des players + Precise pas le type du générique */
    ArrayList players = new ArrayList();

    /* stockage des données Player : plutot créer une classe Player avec leurs informations (position, points, isInPenaltyBox) */
    int[] places = new int[6];
    int[] purses  = new int[6];
    boolean[] inPenaltyBox  = new boolean[6];

    /* Gestion des questions : non respect du principe OCP */
    LinkedList popQuestions = new LinkedList();
    LinkedList scienceQuestions = new LinkedList();
    LinkedList sportsQuestions = new LinkedList();
    LinkedList rockQuestions = new LinkedList();

    /* OK WHY NOT */
    int currentPlayer = 0;
    /* ? */
    boolean isGettingOutOfPenaltyBox;

    public  Game(){
        /* Non respect principe SRP */
        for (int i = 0; i < 50; i++) {
            popQuestions.addLast("Pop Question " + i);
            scienceQuestions.addLast(("Science Question " + i));
            sportsQuestions.addLast(("Sports Question " + i));
            rockQuestions.addLast(createRockQuestion(i));
        }
    }

    /* Création d'une fonction utilitaire obsolète + modificateur d'acces public ? */
    public String createRockQuestion(int index){
        return "Rock Question " + index;
    }

    /* pourquoi pas */
    public boolean isPlayable() {
        return (howManyPlayers() >= 2);
    }

    /* pas la responsabilité de Game : ajout et initialisation des positions des joueurs + aucune gestion des exceptions qui seront levé */
    public boolean add(String playerName) {


        players.add(playerName);
        // EXCEPTION howManyPlayers va retourner le nombre de joueurs et dépasser le tableau ce qui va lever une exception
        places[howManyPlayers()] = 0;
        purses[howManyPlayers()] = 0;
        inPenaltyBox[howManyPlayers()] = false;

        System.out.println(playerName + " was added");
        System.out.println("They are player number " + players.size());
        return true;
    }

    /* pas responsabilité de Game gestion players*/
    public int howManyPlayers() {
        return players.size();
    }

    public void roll(int roll) {
        // PAS DE CHECK DE LA VALEUR DE ROLL ? (si <= 0 ?)
        System.out.println(players.get(currentPlayer) + " is the current player");
        System.out.println("They have rolled a " + roll);

        if (inPenaltyBox[currentPlayer]) {
            if (roll % 2 != 0) {
                isGettingOutOfPenaltyBox = true;

                System.out.println(players.get(currentPlayer) + " is getting out of the penalty box");
                /* CODE DUPLIQUÉ VOIR PLUS BAS !!!!! */
                places[currentPlayer] = places[currentPlayer] + roll;
                if (places[currentPlayer] > 11) places[currentPlayer] = places[currentPlayer] - 12;

                System.out.println(players.get(currentPlayer)
                        + "'s new location is "
                        + places[currentPlayer]);
                System.out.println("The category is " + currentCategory());
                askQuestion();
                /* CODE DUPLIQUÉ VOIR PLUS BAS !!!!! */
            } else {
                System.out.println(players.get(currentPlayer) + " is not getting out of the penalty box");
                isGettingOutOfPenaltyBox = false;
            }

        } else {
            /* CODE DUPLIQUÉ VOIR PLUS HAUT !! */
            places[currentPlayer] = places[currentPlayer] + roll;
            if (places[currentPlayer] > 11) places[currentPlayer] = places[currentPlayer] - 12;

            System.out.println(players.get(currentPlayer)
                    + "'s new location is "
                    + places[currentPlayer]);
            System.out.println("The category is " + currentCategory());
            askQuestion();
            /* CODE DUPLIQUÉ VOIR PLUS HAUT !!!!! */
        }

    }

    /* EXCEPTION NON GERE + Non respect du principe OCP + comparaison de référence au lieu de la méthode equals de String */
    private void askQuestion() {
        if (currentCategory() == "Pop")
            System.out.println(popQuestions.removeFirst());
        if (currentCategory() == "Science")
            System.out.println(scienceQuestions.removeFirst());
        if (currentCategory() == "Sports")
            System.out.println(sportsQuestions.removeFirst());
        if (currentCategory() == "Rock")
            System.out.println(rockQuestions.removeFirst());
    }

    /* Non respect du principe OCP */
    private String currentCategory() {
        if (places[currentPlayer] == 0) return "Pop";
        if (places[currentPlayer] == 4) return "Pop";
        if (places[currentPlayer] == 8) return "Pop";
        if (places[currentPlayer] == 1) return "Science";
        if (places[currentPlayer] == 5) return "Science";
        if (places[currentPlayer] == 9) return "Science";
        if (places[currentPlayer] == 2) return "Sports";
        if (places[currentPlayer] == 6) return "Sports";
        if (places[currentPlayer] == 10) return "Sports";
        return "Rock";
    }

    /*  méthode qui devrait être privé +
        Code dupliqué à l'intérieur +
        Concaténation de string legacy +
        Méthode non utilisé +
        Modification de la position de l'utilisateur
        */
    public boolean wasCorrectlyAnswered() {
        if (inPenaltyBox[currentPlayer]){
            if (isGettingOutOfPenaltyBox) {
                System.out.println("Answer was correct!!!!");
                purses[currentPlayer]++;
                System.out.println(players.get(currentPlayer)
                        + " now has "
                        + purses[currentPlayer]
                        + " Gold Coins.");

                boolean winner = didPlayerWin();
                currentPlayer++;
                if (currentPlayer == players.size()) currentPlayer = 0;

                return winner;
            } else {
                currentPlayer++;
                if (currentPlayer == players.size()) currentPlayer = 0;
                return true;
            }



        } else {

            System.out.println("Answer was corrent!!!!");
            purses[currentPlayer]++;
            System.out.println(players.get(currentPlayer)
                    + " now has "
                    + purses[currentPlayer]
                    + " Gold Coins.");

            boolean winner = didPlayerWin();
            currentPlayer++;
            if (currentPlayer == players.size()) currentPlayer = 0;

            return winner;
        }
    }

    /* méthode qui devrait être privé + gestion de la position qui ne devrait pas être dans cette méthode mais plutot dans la boucle de jeu */
    public boolean wrongAnswer(){
        System.out.println("Question was incorrectly answered");
        System.out.println(players.get(currentPlayer)+ " was sent to the penalty box");
        inPenaltyBox[currentPlayer] = true;

        currentPlayer++;
        if (currentPlayer == players.size()) currentPlayer = 0;
        return true;
    }

    /* MAGIC NUMBER */
    private boolean didPlayerWin() {
        return !(purses[currentPlayer] == 6);
    }
}