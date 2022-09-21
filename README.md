# Structurized Reborn
<p align="left">
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-brightgreen.svg"></a>
</p>

### THIS IS A REWORK OF DRAYLAR'S *STRUCTURIZED*. See the original here: https://github.com/omega-mc/structurized/tree/1.18

*Structurized Reborn* is a simple library that helps with the addition of custom village structures.

### Jigsaw Modification
Structurized provides a registry that allows you to add custom structures to `StructurePool`s in jigsaws such as villages. Say we wanted to add `village/plains/houses/plains_small_house_1` to the desert house pool. Simply register the new structure to the desired pool and give it a weight and some optional modifiers. Call these `register` methods in the same place you would call any other server-focused registry event (registering items or blocks, for example)
```kotlin
FabricStructurePoolRegistry.register(
    Identifier("minecraft:village/desert/houses"),                       //the target pool 
    Identifier("minecraft:village/plains/houses/plains_small_house_1"),  //the new structure nbt to add
    2,                                                                   //the weight of the structure in the pool
    StructureProcessorLists.MOSSIFY_10_PERCENT)                          //optional processor to add mossiness
```

If you don't have any special considerations, you can use `registerSimple` to make your life a bit easier:
```kotlin
FabricStructurePoolRegistry.registerSimple(
    Identifier("minecraft:village/desert/houses"),                       //the target pool 
    Identifier("minecraft:village/plains/houses/plains_small_house_1"),  //the new structure nbt to add
    2)                                                                   //the weight of the structure in the pool
```

### Flexible Registration
The register method is quite flexible, with several optional parameters to use as needed. In many cases you will be OK using the `registerSimple` method, but the main `register` method can be useful for doing something like adding the random mossy cobblestone that many village structures have.

Parameters:

`poolId`: required, the target pool of structures to modify

`structureId`: required, the new structure nbt location identifier

`weight`: required, the probability of a structure being chosen for generation. A weight of 1 to 3 is about 1 structure per village

`processor`: optional, defines custom generation tweaks to apply, like random mossy cobblestone

`projection`: optional, defines the way the structure interacts with the ground (rigid in space or conform to the landscape)

`type`: optional, defines the type of `structurePoolElement` you want. This isn't needed the majority of the time

### Callback Registration
If you want to do something more advanced with a structure pool, you can also directly register to the callback and add whatever event code you'd like. Registering to the callback looks like so:
```kotlin
StructurePoolAddCallback.EVENT.register(structurePool -> {
    if(structurePool.getUnderlying().getId().toString().equals("minecraft:village/plains/houses")) {
        structurePool.addStructurePoolElement(new SinglePoolElement("village/desert/houses/desert_small_house_1"), 50);
    }
});
```

### Adding Dependency
You can add this as a dependency to your project using modrinth's built in maven repository. The {VERSION} will be the version number of the version you are trying to work with. For example, the first version of this library was uploaded under version number **1.18.2-01**.

In a build.gradle:
```java
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modImplementation "maven.modrinth:Wd844r7Q:{VERSION}"
    include("maven.modrinth:Wd844r7Q:{VERSION}")
}
```

In a build.gradle.kts
```kotlin
repositories {
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    modImplementation("maven.modrinth:Wd844r7Q:{VERSION}")
    include("maven.modrinth:Wd844r7Q:{VERSION}")
}
```
