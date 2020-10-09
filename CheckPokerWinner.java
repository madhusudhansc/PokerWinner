import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CheckPokerWinner {

    private static String PLAYER1_HAND_CARDS = "";
    private static String PLAYER2_HAND_CARDS = "";
    private static String PLAYER1_HAND_SUITES = "";
    private static String PLAYER2_HAND_SUITES= "";
    private static final String THE_ORDER = "E123456789ABCDE";
    private static final String THE_HIGH_ORDER = "123456789ABCDE";
    private static  boolean PLAYER1_SAME_SUITE = false;
    private static  boolean PLAYER2_SAME_SUITE = false;
    private static HashMap<Character, Integer> PLAYER1_DUPLICATE_CARDS = new HashMap<>();
    private static HashMap<Character, Integer> PLAYER2_DUPLICATE_CARDS = new HashMap<>();
    private static int PLAYER1_MAX_DUPLICATE = 0;
    private static int PLAYER2_MAX_DUPLICATE = 0;

    //Public class to be called to check winner
    public static int DeclareWinner(String[] hand1, String[] hand2) {
        initializeValues(hand1, hand2);
        int winner = 0;

        //Royal FLush Check
        boolean p1GotRoyalFlush = checkForRoyalFlush(1);
        boolean p2GotRoyalFlush = checkForRoyalFlush(2);
        winner = checkForWinner(p1GotRoyalFlush, p2GotRoyalFlush);
        if (winner != -1) {
            return winner;
        }

        //Straight Flush Check
        boolean p1GotStraightFlush = checkForStraightFlush(1);
        boolean p2GotStraightFlush = checkForStraightFlush(2);
        winner = checkForWinner(p1GotStraightFlush, p2GotStraightFlush);
        if (winner >0 ) {
            return winner;
        } else if (winner ==0) {
            //Both have Straight flush, who won?
            if(THE_ORDER.indexOf(PLAYER1_HAND_CARDS) > THE_ORDER.indexOf(PLAYER2_HAND_CARDS)) {
                return 1;
            } else if(THE_ORDER.indexOf(PLAYER1_HAND_CARDS) < THE_ORDER.indexOf(PLAYER2_HAND_CARDS)) {
                return 2;
            } else {
                return 0;
            }
        }

        //Four of a Kind Check
        winner  = checkForFourOfAKind();
        if (winner != -1) {
            return winner;
        }

        //Full House (three of a Kind)
        winner = checkForFullHouse();
        if (winner != -1) {
            return winner;
        }

        winner = checkForFlush();
        if (winner != -1) {
            return winner;
        }

        winner = checkForStraight();
        if (winner != -1) {
            return winner;
        }

        winner = checkForTwoPairAndPair();
        if (winner != -1) {
            return winner;
        }

        winner = checkForHighCards();


        return winner;
    }

    private static int checkForHighCards() {
        if (THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS.substring(4)) > THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS.substring(4))) {
            return 1;
        } else if (THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS.substring(4)) < THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS.substring(4))) {
            return 2;
        } else {
            return 0;
        }
    }

    private static int checkForTwoPairAndPair() {
        // Player should have three of a Kind and a pair.
        if ((PLAYER1_MAX_DUPLICATE <2) && (PLAYER2_MAX_DUPLICATE<2)) {
            return -1;   // Both dont have any pair
        }

        //At least one player has a pair
        if((PLAYER1_MAX_DUPLICATE ==2) && (PLAYER2_MAX_DUPLICATE==2)) {
            //Both have pair, Does any one have another pair?
            String p1DupChar = getDupCard(2, 1);
            String p2DupChar = getDupCard(2, 2);

            // Now remove these from duplicate hash maps
            PLAYER1_DUPLICATE_CARDS.remove(p1DupChar.charAt(0));
            PLAYER2_DUPLICATE_CARDS.remove(p2DupChar.charAt(0));
            //Look for par again.
            p1DupChar = getDupCard(2, 1);
            p2DupChar = getDupCard(2, 2);
            if(p1DupChar.equals("0") && (p2DupChar.equals("0"))) {
                // Both don't have pair, so we need to check had has Higher card and call them has winner.
                p1DupChar = getDupCard(2, 1);
                p2DupChar = getDupCard(2, 2);
                return WhoWonFrom(p1DupChar, p2DupChar);
            }
            return WhoWonFrom(p1DupChar, p2DupChar);
        } else if (PLAYER1_MAX_DUPLICATE==2) {
            return 1;
        } else {
            return 2;
        }
    }

    private static int checkForStraight() {
        int p1GotStraight = THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS);
        int p2GotStraight = THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS);
        if((p1GotStraight==-1) && (p2GotStraight==-1)) {
            return -1; //Nobody got straight
        } else if(p1GotStraight == -1) {
            return 2;
        } else if(p2GotStraight == -1) {
            return 1;
        }
        // We are here means, we got straight in both hands... Look for high card.
        if(THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS.substring(4)) > THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS.substring(4))) {
            return 1;
        } else if(THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS.substring(4)) < THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS.substring(4))){
            return 2;
        } else {
            return 0;
        }
    }

    private static int checkForFlush() {
        if(!(PLAYER1_SAME_SUITE || PLAYER2_SAME_SUITE)) {
            return -1; //None have Flush
        }

        if (PLAYER1_SAME_SUITE && PLAYER2_SAME_SUITE) {
            //Both have FLUSH? WHo got High Card?
            if(THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS.substring(4)) > THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS.substring(4))) {
                return 1;
            } else if(THE_HIGH_ORDER.indexOf(PLAYER1_HAND_CARDS.substring(4)) < THE_HIGH_ORDER.indexOf(PLAYER2_HAND_CARDS.substring(4))){
                return 2;
            } else {
                return 0;
            }
        } else if(PLAYER1_SAME_SUITE) {
            return 1;
        } else {
            return 2;
        }

    }

    private static int checkForFullHouse() {
        // Player should have three of a Kind and a pair.
        if ((PLAYER1_MAX_DUPLICATE <3) && (PLAYER2_MAX_DUPLICATE<3)) {
            return -1;   // Both dont have 3 kind lets move on
        }

        //At least one player has 3 of a Kind
        if((PLAYER1_MAX_DUPLICATE ==3) && (PLAYER2_MAX_DUPLICATE==3)) {
            //Check do they have pair?
            String p1DupChar = getDupCard(2, 1);
            String p2DupChar = getDupCard(2, 2);
            if(p1DupChar.equals("0") && (p2DupChar.equals("0"))) {
                // Both don't have pair, so we need to check had has Higher card and call them has winner.
                p1DupChar = getDupCard(3, 1);
                p2DupChar = getDupCard(3, 2);
                return WhoWonFrom(p1DupChar, p2DupChar);
            }
            return WhoWonFrom(p1DupChar, p2DupChar);
        } else if (PLAYER1_MAX_DUPLICATE==3) {
            return 1;
        } else {
            return 2;
        }

    }

    private static int WhoWonFrom(String p1DupChar, String p2DupChar) {
        if (p1DupChar.equals(p2DupChar)) {
            return 0;
        } else if (THE_HIGH_ORDER.indexOf(p1DupChar) > THE_HIGH_ORDER.indexOf(p2DupChar)) {
            return 1;
        } else {
            return 2;
        }
    }

    private static int checkForFourOfAKind() {
        setUpTOFIndDuplicates();
        if ((PLAYER1_MAX_DUPLICATE <4) && (PLAYER2_MAX_DUPLICATE<4)) {
            return -1;
        } else if((PLAYER1_MAX_DUPLICATE ==4) && (PLAYER2_MAX_DUPLICATE==4)) {
            String p1DupChar = getDupCard(4, 1);
            String p2DupChar = getDupCard(4, 2);
            return WhoWonFrom(p1DupChar, p2DupChar);

        } else if (PLAYER1_MAX_DUPLICATE ==4) {
            return 1;
        } else {
            return 2;
        }

    }

    private static String getDupCard(int i, int player) {
        if (player==1) {
            for (Map.Entry mapKeyValue : PLAYER1_DUPLICATE_CARDS.entrySet()) {
                char key = (char) mapKeyValue.getKey();
                Integer value = (Integer) mapKeyValue.getValue();
                if (i == value) return String.valueOf(key);
            }
        } else {
            for (Map.Entry mapKeyValue : PLAYER2_DUPLICATE_CARDS.entrySet()) {
                char key = (char) mapKeyValue.getKey();
                Integer value = (Integer) mapKeyValue.getValue();
                if (i == value) return String.valueOf(key);
            }

        }
        return "0";

    }

    private static void setUpTOFIndDuplicates() {
        char[] _temp1 = PLAYER1_HAND_CARDS.toCharArray();
        char[] _temp2 = PLAYER2_HAND_CARDS.toCharArray();
        for(char c : _temp1) {
            if (PLAYER1_DUPLICATE_CARDS.containsKey(c)) {
                int duplicateCount = PLAYER1_DUPLICATE_CARDS.get(c);
                duplicateCount = duplicateCount + 1;
                PLAYER1_DUPLICATE_CARDS.put(c, duplicateCount);
                if (PLAYER1_MAX_DUPLICATE < duplicateCount) PLAYER1_MAX_DUPLICATE = duplicateCount;
            } else {
                PLAYER1_DUPLICATE_CARDS.put(c, 1);
            }
        }
        for(char c : _temp2) {
            if (PLAYER2_DUPLICATE_CARDS.containsKey(c)) {
                int duplicateCount = PLAYER2_DUPLICATE_CARDS.get(c);
                duplicateCount = duplicateCount + 1;
                PLAYER2_DUPLICATE_CARDS.put(c, duplicateCount);
                if (PLAYER2_MAX_DUPLICATE < duplicateCount) PLAYER2_MAX_DUPLICATE = duplicateCount;
            } else {
                PLAYER2_DUPLICATE_CARDS.put(c, 1);
            }
        }
    }

    private static boolean checkForStraightFlush(int player) {
        String playerCards="";
        boolean sameSuite = false;
        if (player==1) {
            playerCards = PLAYER1_HAND_CARDS;
            sameSuite = PLAYER1_SAME_SUITE;
        } else {
            playerCards = PLAYER2_HAND_CARDS;
            sameSuite = PLAYER2_SAME_SUITE;
        }
        return (THE_ORDER.contains(playerCards) && sameSuite);
    }

    private static int checkForWinner(boolean p1, boolean p2) {
        if (p1 && p2) {
            return 0;
        } else if (p1) {
            return 1;
        } else if (p2) {
            return 2;
        } else {
            return -1;
        }
    }

    private static boolean checkForRoyalFlush(int player) {
        String playerCards = "";
        boolean sameSuite = false;
        if (player==1) {
            playerCards = PLAYER1_HAND_CARDS;
            sameSuite = PLAYER1_SAME_SUITE;
        } else {
            playerCards = PLAYER2_HAND_CARDS;
            sameSuite = PLAYER2_SAME_SUITE;
        }

        return playerCards.equals("ABCDE") && sameSuite;

    }

    private static void initializeValues(String[] hand1, String [] hand2) {
        // Populate player1 Hand and Suite cards
        for(String hand : hand1) {
            PLAYER1_HAND_CARDS = PLAYER1_HAND_CARDS + hand.substring(0,1).toUpperCase();
            PLAYER1_HAND_SUITES = PLAYER1_HAND_SUITES + hand.substring(1,2).toUpperCase();
        }
        for(String hand : hand2) {
            PLAYER2_HAND_CARDS = PLAYER2_HAND_CARDS + hand.substring(0,1).toUpperCase();
            PLAYER2_HAND_SUITES = PLAYER2_HAND_SUITES + hand.substring(1,2).toUpperCase();
        }


        //Check weather player has same suite
        PLAYER1_SAME_SUITE = allSameSuite(PLAYER1_HAND_SUITES);
        PLAYER2_SAME_SUITE = allSameSuite(PLAYER2_HAND_SUITES);

        PLAYER1_HAND_CARDS = customSort(PLAYER1_HAND_CARDS.toCharArray());
        PLAYER2_HAND_CARDS = customSort(PLAYER2_HAND_CARDS.toCharArray());
    }

    //This one does custom sort, We want to save Player cards in the format of THE_ORDER

    private static String customSort(char[] cardsValues) {
        boolean containAce = false;
        boolean containsAlphabet = false;
        boolean containMultipleAlphabets = false;
        for(int i=0; i<cardsValues.length; i++) {
            if (cardsValues[i] == 'T') {
                cardsValues[i] = 'A';
                if(containsAlphabet) containMultipleAlphabets=true;
                containsAlphabet = true;
            }
            if (cardsValues[i] == 'J') {
                cardsValues[i] = 'B';
                if (containsAlphabet) containMultipleAlphabets = true;
                containsAlphabet = true;
            }
            if (cardsValues[i] == 'Q') {
                cardsValues[i] = 'C';
                if (containsAlphabet) containMultipleAlphabets = true;
                containsAlphabet = true;
            }
            if (cardsValues[i] == 'K') {
                cardsValues[i] = 'D';
                if (containsAlphabet) containMultipleAlphabets = true;
                containsAlphabet = true;
            }
            if (cardsValues[i] == 'A') {
                cardsValues[i] = 'E';
                containAce = true;
                if (containsAlphabet) containMultipleAlphabets = true;
                containsAlphabet = true;
            }
        }
        String replacedString = new String(cardsValues);
        if(containAce && !containMultipleAlphabets) {
            replacedString = "E"+replacedString.substring(0,4);
        } else {
            Arrays.sort(cardsValues);
            replacedString = new String(cardsValues);
        }
        return replacedString;
    }

    private static boolean allSameSuite(String playerHandSuites) {
        int lengthOfString = playerHandSuites.length();
        char firstChar = playerHandSuites.charAt(0);
        for (int i=0;i<lengthOfString;i++) {
            if (playerHandSuites.charAt(i) != firstChar) return false;
        }
        return true;
    }

}
