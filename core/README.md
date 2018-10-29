## Overview

Intake consists of four parts:

### Command Framework

The command framework consists of some basic interfaces and classes:

* Commands are modeled by `CommandCallable`
* Groups of sub-commands are modeled by `Dispatcher`
* Descriptions of commands are modeled by `Description`
* Individual parameters (for introspection) are modeled by `Parameter`
* Commands that can suggest completions are modeled by `CommandCompleter`
* Arguments (accessed as a stack) are represented by `CommandArgs`

There is also support for:

* Boolean single-character flags (`/command -f`)
* Value flags (`/command -v value`)
* Testing whether a user has permission to execute a command

The goal of the framework is to provide a compromise between a heavily-opinionated framework and a flexible one.

### Parameter Injection

The parameter injection framework provides IoC-oriented argument parsing and completion.

Raw use of the injection framework can be best seen in an example:

```java
Injector injector = Intake.createInjector();
injector.install(new PrimitivesModule());
injector.install(new UniverseModule());

Builder argParserBuilder = new Builder(injector);
argParserBuilder.addParameter(Body.class);
argParserBuilder.addParameter(CelestialType.class);

ArgumentParser parser = argParserBuilder.build();
parser.parseArguments(Arguments.of("pluto", "dwarfplanet")));
```

ArgumentParser finds "providers" for the Body and CelestialType Java types, which are then later utilized to create object instances from the provided arguments.

`UniverseModule` might look like this:

```java
public class UniverseModule extends AbstractModule {

    private final Universe universe;

    public UniverseModule(Universe universe) {
        this.universe = universe;
    }

    @Override
    protected void configure() {
        bind(Universe.class).toInstance(universe);
        bind(Body.class).toProvider(new BodyProvider(universe));
        bind(CelestialType.class).toProvider(
				new EnumProvider<CelestialType>(CelestialType.class));
    }

}
```

The parameter injection framework has strong similarity to Google Guice's API.

### Parametric Commands

The parametric command framework provides an opinionated method of defining commands using classes:

```java
public class UniverseCommands {

    @Command(aliases = "settype", desc = "Set the type of an object")
    public void setType(Body body, CelestialType type) {
        body.setType(type);
    }

}
```

It makes use of the parameter injection framework.

### Fluent API

There is also a fluent API that combines the command framework with the parametric command framework.

The fluent API uses CommandGraphs and DispatcherNodes to combine frameworks. A basic command graph holds references to the root dispatcher node and to the parametric builder. The root dispatcher node can be used to register commands or to register child dispatcher nodes that can register sub-commands. The builder is responsible for supplying commands with their parameters and holds a reference to the injector which can be used to install modules.

A basic implementation of the fluent API can be found in the bukkit-intake module. All users have to do is create a new instance of the BasicBukkitCommandGraph class and begin registering commands to the root dispatcher node. If they need to add providers, they can do that in the constructor or by getting a reference to the injector from the command graph's builder.

The following is a basic (Bukkit) example of the fluent API:
```java
BasicBukkitCommandGraph cmdGraph = new BasicBukkitCommandGraph();

cmdGraph.getRootDispatcherNode().registerCommands(new SimpleCommand());

DispatcherNode testNode = cmdGraph.getRootDispatcherNode().registerNode("test");

testNode.registerCommands(new TestCommands());
testNode.registerNode("math").registerCommands(new MathCommands());
```


For a more thorough look at any of intake's APIs, check out the example bukkit command [here](examples/bukkit/src/main/java/app/ashcon/intake/example/).