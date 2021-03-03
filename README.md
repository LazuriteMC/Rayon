# Rayon

![](https://github.com/LazuriteMC/Rayon/blob/main/src/main/resources/assets/rayon/icon.png?raw=true)

[![GitHub](https://img.shields.io/github/license/LazuriteMC/Rayon?color=A31F34&label=License&labelColor=8A8B8C)](https://github.com/LazuriteMC/Thimble/blob/main/LICENSE)
[![Discord](https://img.shields.io/discord/719662192601071747?color=7289DA&label=Discord&labelColor=2C2F33&logo=Discord)](https://discord.gg/NNPPHN7b3P)
[![Trello](https://img.shields.io/static/v1?label=Trello&message=Board&color=FFFFFF&logo=Trello&labelColor=0052CC)](https://trello.com/b/SNaD9BVr/rayon)

## What is Rayon?
Rayon is a rigid body simulation for entities library designed to work with the Fabric API.

## What isn't Rayon?
Rayon isn't a rewrite of Minecraft's physics. Rather, it runs alongside Minecraft's original physics
and simulates the motion of custom entities using [JBullet](http://jbullet.advel.cz/).

## Features
* Register a custom entity to become a rigid body
* Contains event callbacks such as block collision events for implementing custom behavior
* Ability to add custom collision shapes
* Server-Client compatibility (i.e. multiplayer support)
* Customizable config settings to control simulation performance and preferences
* Debugging tools (F3 + r)

## How do I use it in my mod?
Add the following lines to your `build.gradle` where `VERSION` is the latest version.
```java
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modApi 'com.github.LazuriteMC:Rayon:VERSION'

    // Optional, for jar-in-jar:
    include 'com.github.LazuriteMC:Rayon:VERSION'
}
```

More information can be found on [our wiki](https://docs.lazurite.dev/rayon/getting-started).

## Special Thanks
Rayon wouldn't be possible without the following Fabric library:
* [OnyxStudios/Cardinal-Components-API](https://github.com/OnyxStudios/Cardinal-Components-API)

## Community
If you need help with Rayon or just wanna find out what we're working on, join our discord [here](https://discord.gg/NNPPHN7b3P). We also have a [trello board](https://trello.com/b/SNaD9BVr/rayon).
