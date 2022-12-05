# Emi Loot
<p align="left">
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-brightgreen.svg"></a>
</p>

Emi Loot is a plugin for [EMI](https://github.com/emilyploszaj/emi) that displays loot drops from chests, mobs, blocks, and more.

Credit for loot condition icon art goes to [lxly9](https://github.com/lxly9). Thank you!!

## Known Compatible Loot or Content Mods:
* [Loot Config](https://www.curseforge.com/minecraft/mc-mods/loot-config)
* [Dark Loot](https://www.curseforge.com/minecraft/mc-mods/darkloot-better-mob-loot)
* [Amethyst Imbuement](https://modrinth.com/mod/amethyst-imbuement)
* [Immersive Weathering](https://modrinth.com/mod/immersive-weathering)
* [Botania](https://modrinth.com/mod/botania)

## Known INCOMPATIBLE Mods
* [LootJS](https://modrinth.com/mod/lootjs): Affects drops on generation, doesn't modify loot tables themselves.
* [Better Nether](https://www.curseforge.com/minecraft/mc-mods/betternether): Hardcoded drops
* [Better End](https://www.curseforge.com/minecraft/mc-mods/betterend): Hardcoded drops
* [Paradise Lost](https://www.curseforge.com/minecraft/mc-mods/paradise-lost): Hardcoded drops

## TODO LIST

|Task|Status|Planned Release|
|----|------|---------------|
|_Released_|---|---|
|Add chest loot|Complete|0.1.0|
|Add mob loot|Complete|0.2.0|
|Add block drops|Complete|0.2.0|
|Add new widgets for gui updating|Complete|0.3.0|
|Update mob/block guis|Complete|0.3.0|
|Update Mob EMI recipes for new layouts|Complete|0.3.0|
|Update Block EMI recipes for new layouts|Complete|0.3.0|
|Fix smelting condition showing the unsmelted item|Complete|0.3.0|
|Add option for small mob loot tables shown to the right instead of below|Complete|0.3.1|
|Review and fix mob/block table percentages|Complete|0.3.1|
|Implement EntityPredicate Parser|Complete|0.4.0|
|Update icon widget impl to handle custom sprites|Complete|0.4.0|
|Implement other missing parsers as needed|Complete|0.4.0|
|_Not yet released_|---|---|
|Remove GUI wiggles|Complete|0.5.0|
|Starting impl of basic config|In work|0.5.0|
|Check mob/block table percentages actually right|Not started|0.5.0|
|Port to 1.19.3 to match to EMI's update|Not started|0.5.0|
|Impl resource-driven mob offsetting overrides|In work|0.5.0|
|Optimize packet sending size/number|In work|0.5.0|
|Impl handler for code-based mob drops (nether star etc)|In work|0.5.0?|
|Refactor client table builders for commonality?|Not started|0.5.0?|
|Impl fishing/gameplay loot?|Not started|?.?.?|
|Implement resource-pack driven custom functions/conditions|Not started|?.?.?|
|Add method for displaying experience drops from mobs|Not started|?.?.?|
|Investigate the possibility to add LootJs support|Not started|?.?.?|
