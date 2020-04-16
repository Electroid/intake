package app.ashcon.intake.example;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import app.ashcon.intake.fluent.DispatcherNode;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

  @Override
  public void onLoad() {
    super.onLoad();

    BasicBukkitCommandGraph cmdGraph = new BasicBukkitCommandGraph();

    DispatcherNode testNode = cmdGraph.getRootDispatcherNode().registerNode("test");

    testNode.registerCommands(new TestCommands());
    testNode.registerNode("math").registerCommands(new MathCommands());

    BukkitIntake intake = new BukkitIntake(this, cmdGraph);

    intake.register();
  }
}
