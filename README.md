# Grappling Hook plugin
Simple bukkit/spigot plugin focused on adding grappling hook into the vanilla minecraft.
Forked from SnowGears. Many thanks to him for his amazing work :)


## Features
- 1.16, 1.15, 1.14, 1.13 support
- Crafting recipes with different materials and durabilities (even netherite)
- Permissions for each recipe and more
- Configurable uses for each material
- Configurable messages with different placeholders
- Option to disable grappling hook in specified worlds
- Option to turn hook messages off
- Configurable delay between uses
- Configurable speed/range of the grappling hook


## How to?
1. Grab the precompiled plugin in the releases tab or build it yourself (alternativelly you can download development builds from the actions tab).
2. Install it on your server.
3. Configure the plugin if needed
4. Reload the plugin with /gh reload or restart the server if you disabled crafting
5. Profit?
6. Profit!


## Original Grappling Hook plugin links

- [Spigot](https://www.spigotmc.org/resources/grappling-hook.22854/)
- [GitHub](https://github.com/snowgears/Grappling-Hook)

## Building the plugin

Maven is the recommended way to build the project.
You will have to download maven if you don't have it yet.
Use `mvn clean package` in the main project directory to build the project.
The output will be located at `/target/GrapplingHook.jar`.
