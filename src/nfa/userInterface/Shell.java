package nfa.userInterface;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import nfa.lambdaNFA.LambdaAutomaton;

/**
 * User Interface to execute an {@code Automatom} using user input via shell.
 */
public final class Shell {

    /**
     * Minimum amount of States required for the LambdaAutomaton.
     */
    private final static int MINIMUM_SIZE = 1;

    /**
     * The {@code LambdaAutomaton} object, that is altered with commands.
     */
    private static LambdaAutomaton automat;

    /**
     * Private constructor to ensure no Shell object can be initialized.
     */
    private Shell() {
        // this point can never be reached
    }

    /**
     * {@code Main} method that is used as a starting point when executing the
     * Shell program. Reads the user input that is typed into the system shell.
     *
     * @param args Default values given to the shell at start. Not in use.
     * @throws IOException Input exception to be handled by the OS.
     */
    public static void main(String[] args) throws IOException {
        BufferedReader shellInput = new BufferedReader(
                new InputStreamReader(System.in));
        execute(shellInput);
        shellInput.close();
    }

    /**
     * Method, that writes a prompt in the shell and then takes user input to
     * execute commands.
     *
     * @param shellInput  {@code BufferedReader} that takes user input via the.
     *                    OS shell
     * @throws IOException Unexpected input errors passed on to other methods.
     */
    private static void execute(BufferedReader shellInput) throws IOException {
        // Continuous loop that reads user input, formats the input
        // and gives the input to executing methods.
        boolean quitExecution = false;
        while (!quitExecution) {
            System.out.print("nfa> ");
            Scanner userInput = new Scanner(shellInput.readLine());
            userInput.useDelimiter("\\s+");

            if (userInput.hasNext()) {
                char firstLetter;
                String command = userInput.next().trim();
                command = command.toLowerCase();
                firstLetter = command.charAt(0);

                switch (firstLetter) {
                    case 'h':
                        commandHelp(userInput);
                        break;
                    case 'd':
                        commandDisplay(userInput);
                        break;
                    case 'g':
                        commandGenerate(userInput);
                        break;
                    case 'q':
                        quitExecution = !hasAdditionalInput(userInput);
                        break;
                    case 'i':
                        commandInitialize(userInput);
                        break;
                    case 'a':
                        commandAdd(userInput);
                        break;
                    case 'c':
                        commandCheck(userInput);
                        break;
                    case 'p':
                        commandPrefix(userInput);
                        break;
                    default:
                        errorMessage("Invalid Command");
                        break;
                }
            } else {
                errorMessage("No input!");
            }

            // Close Scanner to avoid memory leaks.
            userInput.close();
        }
    }

    /**
     * Writes an error message in the shell.
     *
     * @param message String that will be added to the error message.
     */
    private static void errorMessage(String message) {
        System.out.println("Error! " + message);
    }

    /**
     * Writes a help text in the shell.
     *
     * @param userInput Scanner with the input String.
     */
    private static void commandHelp(Scanner userInput) {
        if (!hasAdditionalInput(userInput)) {
            System.out.println("Lambda NFA");
            System.out.println("Note:");
            System.out.println("1.The program dosen't distinguish between"
                    + "lower and upper case for commands.");
            System.out.println("2.The program dosent allow input with addition"
                    + "inforamtions.");
            System.out.println("3.Strings need to be enclosed in a quotation");
            System.out.println("4. [" + LambdaAutomaton.FIRST_SYMBOL + "-"
                    + LambdaAutomaton.LAST_SYMBOL
                    + "] and ~ are allowed transitions");
            System.out.println("Following abbreviations exists:");
            System.out.println("s - String input(see Note 3.)");
            System.out.println("n - integer number for amount of states");
            System.out.println("i - integer number of a state");
            System.out.println("j - integer number of a state");
            System.out.println("c - char for transition(See Note 4.");
            System.out.println("Following commands are available:");
            System.out.println("INIT n    - Inititalizes an Automat with n");
            System.out.println("ADD i j c - Add transition from i to j with "
                    + "c");
            System.out.println("CHECK s   - check if s is in the NFA");
            System.out.println("PREFIX s  - search the longest prefix for s");
            System.out.println("DISPLAY   - alphabetic sorted "
                    + "string represntation of the NFA");
            System.out.println("GENERATE  - generate hard coded NFA");
            System.out.println("HELP      - shows help text");
            System.out.println("QUIT      - quits the programm");
        }
    }

    /**
     * Checks if there is unallowed addition input after commands in a Scanner.
     *
     * @param userInput The Scanner that has to be checked
     * @return {@code True}, when there is additional input.
     *         {@code False}, when there is no additional input.
     */
    private static boolean hasAdditionalInput(Scanner userInput) {
        if (userInput.hasNext()) {
            errorMessage("Program dosent allow additional text.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prints out a string representation of the LambdaAutomaton into the
     * Shell.
     *
     * @param userInput Scanner with user input.
     */
    private static void commandDisplay(Scanner userInput) {
        if (!hasAdditionalInput(userInput) && automatIsInitialized()) {
            System.out.println(automat.toString());
        }
    }

    /**
     * Method to check if a object of the LambdaAutomaton class is initialized.
     *
     * @return {@code true} when a LambdaAutomaton object exists.
     *         {@code false} when no LambdaAutomat object exists.
     */
    private static boolean automatIsInitialized() {
        if (automat != null) {
            return true;
        } else {
            errorMessage("Lambda Automat isn't initialized");
            return false;
        }
    }

    /**
     * Initializes the lambdaAutomaton with the amount of States that was typed
     * into the Shell.
     *
     * @param userInput Scanner with user input to get the number of States.
     */
    private static void commandInitialize(Scanner userInput) {
        // When the user input has a int >= 1 a new Automaton will be
        // initialized, with 1 as start point and the user input as end.
        if (scannerHasInt(userInput)) {
            int size = userInput.nextInt();

            if (!hasAdditionalInput(userInput)) {
                if (size >= MINIMUM_SIZE) {
                    automat = new LambdaAutomaton(size, MINIMUM_SIZE, size);
                } else {
                    errorMessage("Cannot initialize with size <= 0");
                }
            }
        }
    }

    /**
     * Checks if a scanner contains a {@code int}. Returns an error
     * Message if it isn't the case.
     *
     * @param userInput The scanner that has to be checked
     * @return {@code True}, when the Scanner has a {@code int}.
     *         {@code False}, when the Scanner has no {@code int}.
     */
    private static boolean scannerHasInt(Scanner userInput) {
        if (userInput.hasNextInt()) {
            return true;
        } else {
            errorMessage("A int is needed for this command");
            return false;
        }
    }

    /**
     * Pre-tests the whether the user input was in the right format to
     * add a Transition to the LambdaAutomaton.
     *
     * @param userInput Scanner with user input, has to in the right format.
     */
    private static void commandAdd(Scanner userInput) {
        // Does the user input have the right Format? Meaning, 2
        // int values and a char that is defined within the alphabet.
        if (scannerHasInt(userInput) && automatIsInitialized()) {
            int source = userInput.nextInt();

            if (scannerHasInt(userInput)) {
                int target = userInput.nextInt();

                if (userInput.hasNext()) {
                    String c = userInput.next().trim();
                    char firstLetter = c.charAt(0);

                    if (c.length() != 1) {
                        errorMessage(c + " is no valid char input");
                    } else if (!hasAdditionalInput(userInput)) {
                        executeAdd(source, target, firstLetter);
                    }
                } else {
                    System.out.println("More information is needed");
                }
            }

        }
    }

    /**
     * Adds a Transition to the LambdaAutomaton.
     *
     * @param source Source State of the Transition.
     * @param target Target State of the Transition.
     * @param transition {@code Char}, that represents the Transition's name.
     */
    private static void executeAdd(int source, int target, char transition) {
        if (automat.isValidTransition(source, target, transition)) {
            automat.addTransition(source, target, transition);
        } else {
            errorMessage("Transition couldn't be added");
        }
    }

    /**
     * Tests if a string is allowed in the Automaton alphabet (excluding
     * the LAMBDA_SYMBOL of the alphabet).
     *
     * @param toTest The String that has to be tested.
     * @return {@code true} if the String is in the alphabet.
     *         {@code false} if it isn't in the alphabet.
     */
    private static boolean isWithinAlphabet(String toTest) {
        if (toTest == null) {
            errorMessage("Invalid String");
            return false;
        } else {
            // Go through the String and test each letter for validity
            for (int i = 0; i < toTest.length(); ++i) {
                char testSign = toTest.charAt(i);
                if ((testSign < LambdaAutomaton.FIRST_SYMBOL)
                        || (testSign > LambdaAutomaton.LAST_SYMBOL)) {
                    errorMessage("String not in alphabet");
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Pre-test to assure that the user is in the right format to check the
     * LambdaAutomaton for a certain String. The format needs to be "String".
     *
     * @param userInput Scanner with the user input that will be checked.
     */
    private static void commandCheck(Scanner userInput) {
        // When there is input and a Automaton exists, extract the String
        // that is enclosed by quotations marks and check it.
        if (userInput.hasNext()) {
            String toCheck = getTextInQuotations(userInput);

            if (automatIsInitialized() && isWithinAlphabet(toCheck)) {
                String result;
                result = automat.isElement(toCheck) ? "In language."
                        : "Not in language.";
                System.out.println(result);
            }
        } else {
            errorMessage("A string is needed for the CHECK command");
        }
    }

    /**
     * Extracts the String within a scanner that is enclosed by quotation
     * marks.
     *
     * @param userInput Scanner with the user input.
     * @return Returns the String within quotation marks, if the input format
     *         was right. Else returns null.
     */
    private static String getTextInQuotations(Scanner userInput) {
        StringBuilder textInQuotations = new StringBuilder();
        String inQuotation = null;

        // When there is user input, append all parts that are split
        // by the scanner and add the white spaces back. When the input
        // ends & starts with quotations it is in the right format and can
        // be returned.
        if (userInput.hasNext()) {
            int maxIndex;
            textInQuotations.append(userInput.next());

            while (userInput.hasNext()) {
                textInQuotations.append(" ");
                textInQuotations.append(userInput.next());
            }

            inQuotation = textInQuotations.toString();
            inQuotation = inQuotation.trim();
            maxIndex = inQuotation.length() - 1;

            if (inQuotation.charAt(0) == '"'
                    && inQuotation.charAt(maxIndex) == '"' && maxIndex != 0) {
                if (maxIndex == 1) {
                    inQuotation = "";
                } else {
                    inQuotation = inQuotation.substring(1, maxIndex);
                }
            } else {
                /* As the NFA dosent allow for additional Input attaching chars
                 * after the second quotation results in a false command
                 */
                inQuotation = null;
            }

        }
        return inQuotation;
    }

    /**
     * Checks if the user input was in the right format ("String").
     * Then searches the longest prefix and prints it, if possible.
     *
     * @param userInput Scanner with the user input that will be checked.
     */
    private static void commandPrefix(Scanner userInput) {
        // Has it input in quotations so prefix can be executed?
        if (userInput.hasNext()) {
            String toCheck = getTextInQuotations(userInput);

            if (automatIsInitialized() && !hasAdditionalInput(userInput)
                    && isWithinAlphabet(toCheck)) {
                String result = automat.longestPrefix(toCheck);
                if (result == null) {
                    System.out.println("No prefix in language.");
                } else {
                    System.out.println("\"" + result + "\"");
                }
            }
        } else {
            System.out.println("A String is needed for this command");
        }
    }

    /**
     * Generates a hard coded NFA to test the program.
     *
     * @param scanner Scanner with user input.
     */
    private static void commandGenerate(Scanner scanner) {
        if (!hasAdditionalInput(scanner)) {
            automat = new LambdaAutomaton(10, 1, 10);
            automat.addTransition(1, 2, '~');
            automat.addTransition(2, 2, '~');
            automat.addTransition(2, 4, '~');
            automat.addTransition(2, 3, 'a');
            automat.addTransition(3, 4, 'b');
            automat.addTransition(3, 4, '~');
            automat.addTransition(4, 5, 'a');
            automat.addTransition(4, 1, '~');
            automat.addTransition(5, 10, '~');
            automat.addTransition(5, 6, 'c');
            automat.addTransition(6, 7, 'l');
            automat.addTransition(7, 6, 'a');
            automat.addTransition(7, 8, 'e');
            automat.addTransition(8, 6, '~');
            automat.addTransition(7, 9, 'u');
            automat.addTransition(9, 10, 'n');
            automat.addTransition(10, 7, '~');
            automat.addTransition(5, 6, '~');
            automat.addTransition(9, 7, 'r');
            automat.addTransition(7, 7, 'd');
            automat.addTransition(7, 3, 'm');
            automat.addTransition(5, 9, '~');
        }
    }

}