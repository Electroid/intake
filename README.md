# Intake [![Build Status](https://travis-ci.org/Electroid/intake.png?branch=master)](https://travis-ci.org/Electroid/intake)

Intake is a command parsing library that can be implemented along various platforms, such as Minecraft.

When a user inputs a command:

```
/sum 1 2
```

You can easily handle that request with only a couple lines of code.

```java
public class Commands {
   @Command(
       aliases = "sum",
       desc = "Adds two numbers and returns their result",
       perms = "math.sum",
       usage = "[number] [number]"
   )
   public void sum(CommandSender user, int a, int b) {
       user.sendMessage(a + b);
   }
}
```

Intake allows you to define custom providers for your own classes and handle errors with ease. Check out the [detailed breakdown](core/README.md) of the project for more details.

## Example

To see how easy it is to implement Intake, take a look at the [Bukkit](bukkit/src/main/java/app/ashcon/intake/bukkit) module. For game developers that want to extend that module specifically, all you need to do is add this to your `JavaPlugin` class:
```java
@Override
public void onLoad() {
   BasicBukkitCommandGraph cmdGraph = new BasicBukkitCommandGraph();
   cmdGraph.getRootDispatcherNode().registerCommands(new Commands());
   new BukkitIntake(this, cmdGraph).register();
}
```

An example `Bukkit` plugin is provided for your convenience [here](examples/bukkit/src/main/java/app/ashcon/intake/example/).

## Installation

Release and snapshot artifacts are automatically deployed to my Nexus repo. Include the following snippet in your `pom.xml` to start using Intake.

```xml
<repositories>
  <repository>
    <id>ashcon.app</id>
    <url>https://repo.ashcon.app/nexus/content/repositories/snapshots/</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>app.ashcon.intake</groupId>
    <!-- Use "intake-core" if you don't want Minecraft -->
    <artifactId>intake-bukkit</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## Compiling

Use Maven to compile Intake.

```
mvn clean install
```

## Attributions

Intake was adapted from [sk89q's abandoned fork](https://github.com/EngineHub/Intake), but heavily modified and abstracted. Therefore, it is available under the GNU Lesser General Public License.

I happily accept contributions, especially through pull requests on GitHub!