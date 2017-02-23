package nfa.lambdaNFA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;

/**
 * State used in an NFA, that serves as a Node in between transitions.
 */
public class State {

    /**
     * Length of the alphabet [FIRST_SYMBOL-LAST_SYMBOL]
     */
    private static final int ALPHABET_LENGTH = LambdaAutomaton.LAST_SYMBOL
            - LambdaAutomaton.FIRST_SYMBOL;

    /**
     * The number in the NFA this particular State has.
     */
    private int numberOfState;

    /**
     * Indicates the role of the State as an end-State.
     */
    private boolean isEndState;

    /**
     * Transitions starting from this State as collision-free hashed
     * adjacency-list.
     */
    private List<Collection<Transition>> charAdj =
            new ArrayList<Collection<Transition>>(ALPHABET_LENGTH + 1);

    /**
     * Saves States reachable from this State via Lambda transitions.
     */
    private List<State> nextSet;

    /**
     * Initializes an object of the State class with a certain number and
     * marks the state as non-endState.
     *
     * @param number The number within the NFA that is assigned to the State.
     */
    protected State(int number) {
        this.numberOfState = number;
        this.isEndState = false;

        for (int i = 0; i < ALPHABET_LENGTH + 1; ++i) {
            charAdj.add(null);
        }
    }

    /**
     * Retrieves information whether this State serves as end-State in an NFA.
     *
     * @return {@code True}, when the State is an end-State.
     * {@code False}, when the State is not an end-State.
     */
    protected boolean getIsEndNode() {
        return this.isEndState;
    }

    /**
     * Assigns information about the role as an end-State(or not) to a
     * particular State.
     *
     * @param isEnd Boolean value that indicates the role: {@code true}, when
     *              the State should be set to end-State; {@code false}, else.
     */
    protected void setIsEndNode(Boolean isEnd) {
        this.isEndState = isEnd;
    }

    /**
     * Returns the number this particular State has within the NFA.
     *
     * @return Number of the state as {@code int} value.
     */
    protected int getNumber() {
        return this.numberOfState;
    }

    /**
     * Adds a Transition that starts at this State.
     *
     * @param toAdd Transition that has to be added.
     */
    protected void addTransition(Transition toAdd) {
        int index = getIndex(toAdd);
        Collection<Transition> transitions = charAdj.get(index);

        // Is no entry at the position, a new entry will be created.
        // Else it will be attached to the entry.
        if (transitions == null) {
            transitions = new LinkedList<Transition>();
            charAdj.set(index, transitions);
        }
        transitions.add(toAdd);
    }

    /**
     * Computes the index a certain char, a Transition is using as name,
     * within the alphabet the NFA is using.
     *
     * @param toCheck The Transition with the name that has to be checked.
     * @return The index of the Transition's name within the alphabet.
     */
    private int getIndex(Transition toCheck) {
        char transitionName = toCheck.getTransitionName();
        if (transitionName != LambdaAutomaton.LAMBDA_SYMBOL) {

            // Due to the alphabet being a closed range.
            return (transitionName - LambdaAutomaton.FIRST_SYMBOL);
        } else {

            // Lambda is always assigned to the last spot.
            return ALPHABET_LENGTH;
        }
    }

    /**
     * Sorts the Transitions in the adjacency-list by alphabetic order,
     * with {@code LAMBDA_SYMBOL} being the last one, and returns as list
     * with the sorted Transitions.
     *
     * @return Sorted list by alphabetic order.
     */
    protected List<Transition> getOrderedTransitions() {
        List<Transition> toSortAdj = new LinkedList<Transition>();

        for (Collection<Transition> transitions : charAdj) {
            if (transitions != null) {
                toSortAdj.addAll(transitions);
                Collections.sort(toSortAdj);
            }
        }

        return toSortAdj;
    }

    /**
     * Searches for target-States, that can be reached by using Transitions
     * with a certain name and gives all reachable Transitions back.
     *
     * @param symbol The name of the Transitions that should be used.
     * @return Collection of all reachable target-States using
     * {@code 'symbol'}-Transitions.
     */
    protected Collection<State> getTargets(char symbol) {
        Collection<State> reachableTargets = new LinkedList<State>();

        // Take all entries from the adjacency list and add all elements
        // that are part of these entries, that allow a Transition with
        // the symbol.
        for (Collection<Transition> possibleTransitions : charAdj) {
            if (possibleTransitions != null) {
                for (Transition possibleTransition : possibleTransitions) {
                    if (possibleTransition.isAllowed(symbol)) {
                        State allowedTarget = possibleTransition.getTarget();
                        reachableTargets.add(allowedTarget);
                    }
                }
            }
        }
        return reachableTargets;
    }

    /**
     * Test whether this State and another State are equal by comparing their
     * number within the NFA.
     *
     * @param toTest The State this State should be compared with.
     * @return {@code True}, when the number of the States are equal.
     * {@code False}, if they are not equal.
     */
    protected boolean hasNumber(State toTest) {
        return (this.numberOfState == toTest.numberOfState);
    }

    /**
     * Returns the list of States that are reachable from this State with
     * {@code LAMBDA_SYMBOL}-Transitions.
     *
     * @return List of reachable States.
     */
    protected List<State> getNextSet() {
        return this.nextSet;
    }

    /**
     * Pre-computes the List of States that are reachable from this State
     * by using {@code LAMBDA_SYMBOL}-Transitions.
     */
    protected void precomputeNextSet() {
        this.nextSet = new LinkedList<State>();
        Set<State> visited = new HashSet<State>();
        Queue<State> bfsQueue = new LinkedList<State>();
        bfsQueue.offer(this);
        visited.add(this);

        // add all States that are reachable from this State
        // over those lambda Transitions to one collection.
        while (!bfsQueue.isEmpty()) {
            State state = bfsQueue.poll();
            if (state != this) {
                nextSet.add(state);
            }
            Collection<State> lambdaTargets = state
                    .getTargets(LambdaAutomaton.LAMBDA_SYMBOL);
            for (State lambdaTarget : lambdaTargets) {
                if (!visited.contains(lambdaTarget)) {
                    bfsQueue.offer(lambdaTarget);
                    visited.add(lambdaTarget);
                }
            }
        }
    }

    /**
     * Puts out an String representation of the adjacency-list sorted by
     * alphabetic order. Each Transition has its own line.
     *
     * @return Adjacency-list as String
     */
    @Override
    public String toString() {
        List<Transition> orderedTransitions = getOrderedTransitions();
        StringBuilder stringRepresentation = new StringBuilder();
        for (Transition toRepresent : orderedTransitions) {
            stringRepresentation.append(toRepresent.toString() + "\n");
        }
        return stringRepresentation.toString();
    }

}