![](common/src/main/resources/icon.png)

# Rayon
[![Discord](https://discordapp.com/api/guilds/719662192601071747/widget.png?style=shield)](https://discord.gg/NNPPHN7b3P)

Rigid body simulation in Minecraft.

## Examples
* [Fabric - Example Mod](https://github.com/LazuriteMC/Rayon-Example-Mod-Fabric)
* [Forge - Example Mod](https://github.com/LazuriteMC/Rayon-Example-Mod-Forge)

## Developing with Rayon
Add the following lines to your `build.gradle`.
```java
repositories {
    maven { url "https://lazurite.dev/releases" }
}

dependencies {
    /* Fabric */
    modImplementation "dev.lazurite:rayon-fabric:$rayon_version" // Fabric Only
    
    // or
    
    /* Forge */
    implementation fg.deobf("dev.lazurite:rayon-forge:$rayon_version")
}
```

For a list of versions, visit our [maven](https://lazurite.dev/maven/releases/dev/lazurite/rayon-fabric).