package nfa.lambdaNFA;

/**
 * Automaton interface.
 */
public interface Automaton {

    /**
     * The first character of the alphabet. Must build a continuous range with
     * {@code LAST_SYMBOL}.
     */
    char FIRST_SYMBOL = 'a';

    /**
     * The last character of the alphabet. Must build a continuous range with
     * {@code FIRST_SYMBOL}.
     */
    char LAST_SYMBOL = 'z';

    /**
     * Checks a transition on validity, i.e., if it can be part of the
     * automaton.
     *
     * @param source The id of the source state.
     * @param target The id of the target state.
     * @param symbol The symbol to be red.
     * @return {@code true} if and only if the parameters represent a valid
     * transition of this automation.
     */
    boolean isValidTransition(int source, int target, char symbol);

    /**
     * Adds a transition to the automaton. Multitransitions, i.e. transitions
     * with equal {@code source}s, {@code target}s and {@code symbol}s, and self
     * loops, i.e., with {@code source} equal to {@code target} are explicitly
     * allowed. Needs O(1) time.
     *
     * @param source The source state.
     * @param target The target state.
     * @param symbol The symbol to read.
     */
    void addTransition(int source, int target, char symbol);

    /**
     * Decides the element problem for the regular language defined by this
     * automaton. Needs O((n + m) * |word|) time, with n as number of states and
     * m as number of transitions of the automaton.
     *
     * @param word The word to check.
     * @return {@code true} if and only if {@code word} is in the language.
     */
    boolean isElement(String word);

    /**
     * Computes the longest prefix of {@code word} which is an element of the
     * language. Needs O((n + m) * |word|) time, with n as number of states and
     * m as number of transitions of the automaton.
     *
     * @param word The word whose prefixes will be checked.
     * @return The longest prefix, or {@code null} if none exists.
     */
    String longestPrefix(String word);

    /**
     * Returns a string representation of the automaton as sorted list of all
     * (lexicographically sorted) transitions.
     *
     * @return Automaton as string.
     */
    @Override
    String toString();

}