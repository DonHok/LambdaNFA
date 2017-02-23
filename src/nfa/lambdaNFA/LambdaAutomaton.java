package nfa.lambdaNFA;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * LambdaAutomaton, that defines words with Transitions between States.
 */
public class LambdaAutomaton implements Automaton {

    /**
     * The lambda symbol of the NFA.
     */
    protected static final char LAMBDA_SYMBOL = '~';

    /**
     * The State that LambdaAutomaton uses as start.
     */
    private State startingPoint;

    /**
     * List of all States, that are part of the LambdaAutomaton.
     */
    private List<State> states;

    /**
     * Indicates a need to precompute the nextSet.
     */
    private boolean changed;

    /**
     * Creates a new LambdaAutomaton object with a certain number of States
     * and assigns a start State as well as end States.
     *
     * @param numberOfStates The number of States that should be within the
     *                       LambdaAutomaton.
     * @param start          The position that will be the start of the Automaton.
     * @param endings        The position(s) that will be end State(s).
     */
    public LambdaAutomaton(int numberOfStates, int start, int... endings) {
        states = new ArrayList<State>(numberOfStates);

        // Initialize all required ArrayList spots with States
        // and set the a start point as well as end point(s)
        for (int i = start; i <= numberOfStates; ++i) {
            State toAdd = new State(i);
            states.add(i - 1, toAdd);
        }
        setStartPoint(start);
        for (int end : endings) {
            setEndState(end);
        }

        changed = true;
    }

    /**
     * Sets the State where the LambdaAutomaton starts to a certain Position.
     *
     * @param startNumber The position, that is the start of the Automaton.
     */
    private void setStartPoint(int startNumber) {
        // Is it within the boundaries of the Automaton structure?
        if (isWithinBoundaries(startNumber)) {
            startingPoint = states.get(startNumber - 1);
        }
    }

    /**
     * Sets the end of the LambdaAutomaton to a certain Position.
     *
     * @param endNumber The position, that is the end of the Automaton.
     */
    private void setEndState(int endNumber) {
        // Is it within the boundaries of the Automaton?
        if (isWithinBoundaries(endNumber)) {
            State endState = states.get(endNumber - 1);
            endState.setIsEndNode(true);
        }
    }

    /**
     * Checks if a State number is valid in the Automaton.
     *
     * @param stateNumber The number that has to be tested.
     * @return True, when the number is within the bounds, else false.
     */
    private boolean isWithinBoundaries(int stateNumber) {
        return (stateNumber >= 1) && (stateNumber <= states.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTransition(int source, int target, char symbol) {
        // Add new Transition object to the States when the Transition
        // is within the boundaries and the symbol is defined in the NFA.
        if (isValidTransition(source, target, symbol)) {
            Transition toAdd;
            State sourceState = states.get(source - 1);
            State targetState = states.get(target - 1);
            toAdd = new Transition(sourceState, targetState, symbol);
            sourceState.addTransition(toAdd);
            changed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidTransition(int source, int target, char symbol) {
        return isWithinBoundaries(source) && isWithinBoundaries(target)
                && charIsInAlphabet(symbol);
    }

    /**
     * Test whether a certain char is allowed within the alphabet the Automaton
     * is using.
     *
     * @param toTest The char that has to be tested.
     * @return {@code True}, when the char is in the alphabet. {@code False},
     * when not.
     */
    private static boolean charIsInAlphabet(char toTest) {
        return (((toTest >= FIRST_SYMBOL) && (toTest <= LAST_SYMBOL))
                || (toTest == LAMBDA_SYMBOL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElement(String toCheck) {
        precomputeAllNextSets();

        // Is the word even legal?
        if (toCheck == null) {
            return false;
        } else {
            for (int i = 0; i < toCheck.length(); ++i) {
                if (!charIsInAlphabet(toCheck.charAt(i))) {
                    return false;
                }
            }
        }

        Queue<State> availableMoves = new LinkedList<State>();
        State dummy = new State(-1);
        char currentSymbol;
        int cursor = -1;
        availableMoves.offer(dummy);
        availableMoves.offer(startingPoint);
        availableMoves.addAll(startingPoint.getNextSet());

        // Checks whether a String is defined in the Automaton.
        while (!availableMoves.isEmpty()) {
            State currentState = availableMoves.poll();

            if (currentState.hasNumber(dummy)) {
                ++cursor;

                // One symbol was successfully reached.
                if (cursor < toCheck.length()) {
                    availableMoves.offer(dummy);
                    currentSymbol = toCheck.charAt(cursor);
                }
            } else if (currentState.getIsEndNode()
                    && (cursor == toCheck.length())) {
                return true; // The word is part of the Automaton.
            } else if (cursor < toCheck.length()) {
                currentSymbol = toCheck.charAt(cursor);

                // Add all States that can be reached from the State that was
                // taken from the Queue with the symbol that has to be checked.
                for (State toAdd : currentState.getTargets(currentSymbol)) {
                    availableMoves.offer(toAdd);
                    availableMoves.addAll(toAdd.getNextSet());
                }
            }
        }
        return false; // The word isn't part of the Automaton.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String longestPrefix(String word) {
        // Due to using buffers, this method is longer than 60 lines.
        precomputeAllNextSets();
        if (word != null) {
            String result = null;
            String tempWord = "";
            int cursor = -1;
            int bufferCursor = -1;
            Queue<State> bufferQueue = new LinkedList<State>();
            Queue<State> availableMoves = new LinkedList<State>();
            State dummy = new State(-1);
            availableMoves.offer(dummy);
            availableMoves.offer(startingPoint);
            availableMoves.addAll(startingPoint.getNextSet());

            // Searches the longest prefix by iterating over the partial-words.
            // Similar to isElement, with an additional buffer.
            for (int i = 0; i <= word.length(); ++i) {
                char currentSymbol;

                if (i == 0) {
                    bufferQueue.addAll(availableMoves);
                    bufferCursor = cursor;
                    tempWord = "";
                } else {
                    // Replace the used Queue/cursor with the buffered Queue
                    // and cursor, to check one partial word.
                    tempWord = tempWord + word.charAt(i - 1);
                    availableMoves = new LinkedList<State>();
                    availableMoves.addAll(bufferQueue);
                    cursor = bufferCursor;
                }

                while (!availableMoves.isEmpty()) {
                    State currentState = availableMoves.poll();

                    if (currentState.hasNumber(dummy)) {
                        ++cursor;

                        // The last char of the prefix was reached.
                        // Buffer all data for the next iteration.
                        if (cursor < tempWord.length()) {
                            availableMoves.offer(dummy);
                            bufferQueue = new LinkedList<State>();
                            bufferQueue.addAll(availableMoves);
                            bufferCursor = cursor;
                            currentSymbol = tempWord.charAt(cursor);
                        }

                        // Can the end be reached with current prefix?
                    } else if (currentState.getIsEndNode()
                            && (cursor == tempWord.length())) {
                        result = tempWord; // Replace with longer prefix
                    } else if (cursor < tempWord.length()) {
                        currentSymbol = tempWord.charAt(cursor);

                        // Add all reachable states with the current Symbol.
                        for (State toAdd : currentState
                                .getTargets(currentSymbol)) {
                            availableMoves.offer(toAdd);
                            availableMoves.addAll(toAdd.getNextSet());
                        }
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Method that computes all reachable States via Lambda Transitions.
     */
    private void precomputeAllNextSets() {
        if (changed) {
            for (State currentState : states) {
                currentState.precomputeNextSet();
            }
            changed = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder stringRep = new StringBuilder();
        String toReturn;

        for (State toRepresent : states) {
            stringRep.append(toRepresent.toString());
        }

        toReturn = stringRep.toString();

        // since one state ends with newline, this needs to be cut
        return toReturn.trim();
    }

}