package app.ashcon.intake.fluent;

import app.ashcon.intake.dispatcher.SimpleDispatcher;
import app.ashcon.intake.parametric.ParametricBuilder;

public class BasicCommandGraph extends CommandGraph<GroupDispatcherNode> {

    /**
     * Create a new command graph.
     */
    public BasicCommandGraph(ParametricBuilder builder) {
        super(builder, (tHis) -> new GroupDispatcherNode(tHis, null, new SimpleDispatcher(), builder));
    }
}
