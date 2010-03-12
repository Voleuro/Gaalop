package de.gaalop.cfg;

import de.gaalop.dfg.Variable;

/**
 * This class models the operation that copies the value of a variable to the output of an
 * algorithm. This is the case for a CLUCalc line that starts with a <code>?</code> to output the contents of a
 * variable.
 */
public final class StoreResultNode extends SequentialNode {

    private Variable value;

    /**
     * Constructs a node that copies a variable to an output parameter.
     *
     * @param graph The control flow graph this node should belong to.
     * @param value The variable that should be copied.
     */
    public StoreResultNode(ControlFlowGraph graph, Variable value) {
        super(graph);
        this.value = value;
    }

    /**
     * Gets the variable that should be copied.
     * @return The variable.
     */
    public Variable getValue() {
        return value;
    }

    /**
     * Changes the variable that should be copied.
     * @param value The new variable.
     */
    public void setValue(Variable value) {
        this.value = value;
    }

    /**
     * Calls {@link de.gaalop.cfg.ControlFlowVisitor#visit(StoreResultNode)} on a visitor.
     * 
     * @param visitor The visitor object that the visit method will be called on.
     */
    @Override
    public void accept(ControlFlowVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
    	return "?" + value;
    }
}
