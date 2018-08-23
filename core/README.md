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
