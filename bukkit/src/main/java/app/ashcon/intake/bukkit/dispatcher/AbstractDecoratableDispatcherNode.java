package app.ashcon.intake.bukkit.dispatcher;

import app.ashcon.intake.dispatcher.SimpleDispatcher;
import app.ashcon.intake.fluent.AbstractDispatcherNode;
import app.ashcon.intake.fluent.CommandGraph;

public class AbstractDecoratableDispatcherNode extends AbstractDispatcherNode {

    /**
     * Create a new instance.
     *
     * @param graph      the root fluent graph object
     * @param parent     the parent node, or null
     * @param dispatcher the dispatcher for this node
     */
    AbstractDecoratableDispatcherNode(CommandGraph graph, AbstractDispatcherNode parent, SimpleDispatcher dispatcher) {
        super(graph, parent, dispatcher);
    }
}
