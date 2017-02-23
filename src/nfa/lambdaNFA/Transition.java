package nfa.lambdaNFA;

/**
 * Class that represents a Transition in a NFA. Transitions are from one
 * State to another and have a name that is defined in the Automaton.
 */
public class Transition implements Comparable<Transition> {

    /**
     * The name the Transition has within the NFA.
     */
    private char transitionName;

    /**
     * The State the Transition ends at.
     */
    private State sourceState;

    /**
     * The State from which the Transition starts.
     */
    private State targetState;

    /**
     * Creates a new Transition object, that connects a source State with a
     * target State and has a name assigned to the Transition.
     *
     * @param start The State where the Transition starts.
     * @param end   The State where the Transition ends.
     * @param name  The name of the Transition.
     */
    protected Transition(State start, State end, char name) {
        this.transitionName = name;
        this.sourceState = start;
        this.targetState = end;
    }

    /**
     * Returns the target State, the State where the Transition ends, of a
     * Transition.
     *
     * @return Target State of the Transition.
     */
    protected State getTarget() {
        return this.targetState;
    }

    /**
     * Returns the name of a Transition object.
     *
     * @return Name as char.
     */
    protected char getTransitionName() {
        return this.transitionName;
    }

    /**
     * Checks if the Transition allows a transition with a {@code char}.
     *
     * @param toTest {@code Char} that should be tested.
     * @return {@code True}, when the char and the name of the Transition are
     * equal. {@code False}, when this is not the case.
     */
    protected boolean isAllowed(char toTest) {
        return transitionName == toTest;
    }

    /**
     * Compares the a Transition object to another transition using their
     * {@code transitionName}. Uses a lexicographic order with the LAMBDA
     * always being the last symbol.
     *
     * @param toCheck The Transition this object will be compared with.
     * @return {@code < 0} if {@code this < toCheck},
     * {@code = 0} if equal, and
     * {@code > 0} if {@code this > toCheck}.
     */
    @Override
    public int compareTo(Transition toCheck) {
        char thisName = this.transitionName;
        char toCheckName = toCheck.transitionName;
        int thisNumber = this.getTarget().getNumber();
        int toCheckNumber = toCheck.getTarget().getNumber();

        // Order the Transitions, first by their target number
        // and if that is equal, by their name(alphabetic).
        if (thisNumber != toCheckNumber) {
            return thisNumber - toCheckNumber;
        } else if (thisName != LambdaAutomaton.LAMBDA_SYMBOL
                && toCheckName != LambdaAutomaton.LAMBDA_SYMBOL) {
            return thisName - toCheckName;
        } else if (thisName == toCheckName) {
            return 0;
        } else {
            return (thisName == LambdaAutomaton.LAMBDA_SYMBOL) ? 1 : -1;
        }
    }

    /**
     * Returns a String representation of a Transition, listing its source
     * and target State and its name.
     *
     * @return Transition as String.
     */
    @Override
    public String toString() {
        StringBuilder stringRep = new StringBuilder("(");
        stringRep.append(sourceState.getNumber() + ", ");
        stringRep.append(targetState.getNumber() + ") " + transitionName);
        return stringRep.toString();
    }

}