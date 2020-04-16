package app.ashcon.intake.bukkit.graph;

import app.ashcon.intake.Intake;
import app.ashcon.intake.bukkit.BukkitAuthorizer;
import app.ashcon.intake.bukkit.BukkitModule;
import app.ashcon.intake.dispatcher.SimpleDispatcher;
import app.ashcon.intake.fluent.CommandGraph;
import app.ashcon.intake.fluent.DispatcherNode;
import app.ashcon.intake.parametric.Module;
import app.ashcon.intake.parametric.ParametricBuilder;
import app.ashcon.intake.parametric.provider.PrimitivesModule;
import java.util.Arrays;

public class BasicBukkitCommandGraph extends CommandGraph<DispatcherNode> {

  /** Create a new command graph with a simple dispatcher node. */
  public BasicBukkitCommandGraph(Module... modules) {
    ParametricBuilder builder = new ParametricBuilder(Intake.createInjector());
    builder.setAuthorizer(new BukkitAuthorizer());

    builder.getInjector().install(new BukkitModule());
    builder.getInjector().install(new PrimitivesModule());

    Arrays.stream(modules).forEach(builder.getInjector()::install);

    setBuilder(builder);
    setRootDispatcherNode(new DispatcherNode(this, null, new SimpleDispatcher()));
  }
}
