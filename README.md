# Rayon

![](common/src/main/resources/icon.png)

[![GitHub](https://img.shields.io/github/license/LazuriteMC/Rayon?color=A31F34&label=License&labelColor=8A8B8C)](https://github.com/LazuriteMC/Rayon/blob/main/LICENSE)
[![Discord](https://img.shields.io/discord/719662192601071747?color=7289DA&label=Discord&labelColor=2C2F33&logo=Discord)](https://discord.gg/NNPPHN7b3P)

## Examples
* [Fabric - Example Mod](https://github.com/LazuriteMC/Rayon-Example-Mod-Fabric)
* [Forge - Example Mod](https://github.com/LazuriteMC/Rayon-Example-Mod-Forge)

## How to develop using Rayon
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
More information can be found on [our wiki](https://docs.lazurite.dev/rayon/getting-started).

## Community
If you need help with Rayon or just wanna find out what we're working on, join our discord [here](https://discord.gg/NNPPHN7b3P).