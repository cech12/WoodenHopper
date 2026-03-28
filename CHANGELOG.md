# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [this versioning scheme](https://gist.github.com/cech12/69319028e88c50349a6b044000a6607b).

## [26.1-4.0.0.0] - 2026-03-28
### Changed
- updated to Minecraft 26.1 (Fabric 0.144.3+26.1, NeoForge 26.1.0.7-beta, Forge 62.0.4)
- updated Cloth Config support (26.1.154) (Fabric/Quilt)
- updated ModMenu support (18.0.0-alpha.8) (Fabric/Quilt)

## [1.21.11-3.8.0.0] - 2025-12-13
### Changed
- updated to Minecraft 1.21.11 (Fabric 0.139.5+1.21.11, NeoForge 21.11.6-beta, Forge 61.0.2)
- updated Cloth Config support (21.11.151) (Fabric/Quilt)
- updated ModMenu support (17.0.0-alpha.1) (Fabric/Quilt)

## [1.21.9-3.7.0.0] - 2025-10-12
- updated to Minecraft 1.21.9 (Fabric 0.134.0+1.21.9, NeoForge 21.9.15-beta, Forge 59.0.5)
- updated Cloth Config support (20.0.148) (Fabric/Quilt)
- updated ModMenu support (16.0.0-rc.1) (Fabric/Quilt)

## [1.21.6-3.6.1.0] - 2025-07-10
### Added
- Added Hungarian translation (thanks to bayi for the contribution) #39

## [1.21.6-3.6.0.0] - 2025-06-25
### Changed
- updated to Minecraft 1.21.6 (Fabric 0.127.1+1.21.6, NeoForge 21.6.16-beta, Forge 56.0.3)
- updated Cloth Config support (19.0.147) (Fabric/Quilt)
- updated ModMenu support (15.0.0-beta.2) (Fabric/Quilt)

## [1.21.5-3.5.0.0] - 2025-04-28
### Changed
- Updated to Minecraft 1.21.5 (Fabric 0.120.0+1.21.5, NeoForge 21.5.47-beta, Forge 55.0.9)
- Updated Cloth Config support (18.0.145) (Fabric/Quilt)
- Updated ModMenu support (14.0.0-rc.2) (Fabric/Quilt)

## [1.21.4-3.4.0.0] - 2025-02-11
### Changed
- updated to Minecraft 1.21.4 (Fabric 0.116.0+1.21.4, Neoforge 21.4.88-beta, Forge 54.0.26)
- updated Cloth Config support (17.0.144) (Fabric/Quilt)
- updated ModMenu support (13.0.1) (Fabric/Quilt)

## [1.21.3-3.3.0.0] - 2024-11-09
### Changed
- updated to Minecraft 1.21.3 (Fabric 0.107.0+1.21.3, Neoforge 21.3.10-beta, Forge 53.0.7)
- updated Cloth Config support (16.0.141) (Fabric/Quilt)
- updated ModMenu support (12.0.0-beta.1) (Fabric/Quilt)

## [1.21.1-3.2.0.0] - 2024-10-15
- updated to Minecraft 1.21.1 (Fabric 0.105.0+1.21.1, NeoForge 21.1.62, Forge 52.0.21)
- updated Cloth Config support (15.0.140) (Fabric/Quilt)
- updated ModMenu support (11.0.2) (Fabric/Quilt)

### Fixed
- fixed hoppers recipe and advancement by using the new common tags in Forge

## [1.21-3.1.1.0] - 2024-08-30
### Changed
- cooldown config option is now text field instead of a slider (Fabric)

## [1.21-3.1.0.0] - 2024-07-14
### Changed
- updated NeoForge to 21.0.94-beta
- the `config` directory is used for the default configuration (NeoForge)

### Fixed
- crashed on startup with NeoForge (caused by a breaking change in 21.0.82-beta) (thanks to ffuentesm for the report) #26

## [1.21-3.0.0.0] - 2024-06-21
### Changed
- Updated to Minecraft 1.21 (Fabric 0.100.3+1.21, Neoforge 21.0.21-beta, Forge 51.0.16)
- Updated Cloth Config support (15.0.127) (Fabric/Quilt)
- Updated ModMenu support (11.0.0) (Fabric/Quilt)

## [1.20.6-2.3.0.0] - 2024-05-14
### Added
- use the vanilla "does_not_block_hoppers" block tag to enhance performance
- support more modded container entities

### Changed
- Updated to Minecraft 1.20.6 (Fabric 0.98.0+1.20.6, Neoforge 20.6.62-beta, Forge 50.0.20)
- Updated Cloth Config support (14.0.126) (Fabric/Quilt)
- Updated ModMenu support (10.0.0-beta.1) (Fabric/Quilt)
- changed advancements & recipes to match the reworked conventional tags (Fabric/Quilt & Neoforge)
- internal code refactoring

## [1.20.4-2.2.1.0] - 2024-05-11
### Added
- better compatibility with mods that do not support ItemHandlers (Forge & NeoForge)

## [1.20.4-2.2.0.1] - 2024-04-24
### Fixed
- fixed Wooden Hopper crafting recipe (Fabric, Quilt)

## [1.20.4-2.2.0.0] - 2024-04-24
### Added
- add Fabric (>=0.96.11+1.20.4) support (Fabric, Quilt)

## [1.20.4-2.1.0.1] - 2024-04-21
### Fixed
- block entities without an item handler above a wooden hopper could crash the game (Neoforge) (thanks to randomleech for the report) #24

## [1.20.4-2.1.0.0] - 2024-01-16
### Changed
- Update to Minecraft 1.20.4 (Forge 49.0.19, Neoforge 20.4.109-beta)

## [1.20.2-2.0.0.1] - 2024-01-16
### Fixed
- Forge mod could not start

## [1.20.2-2.0.0.0] - 2024-01-16
### Changed
- Move to Multiloader mod template to support Forge and Neoforge
- Update to Minecraft 1.20.2 (Forge 48.1.0, Neoforge 20.2.86)

## [1.20.2-1.7.0.0] - 2023-10-17
### Changed
- update and move back to Forge 1.20.2-48.0.23 (from NeoForge) until it is stable

## [1.20.1-1.6.0.1] - 2023-08-14
### Fixed
- Incompatibility with Tubes Reloaded (and maybe other mods) (thanks to Dam-Buty for the report) #17

## [1.20.1-1.6.0.0] - 2023-08-09
### Changed
- Changed Forge to NeoForge 1.20.1-47.1.54 (compatible with Forge 47.1.0)

## [1.20.1-1.5.1.0] - 2023-06-27
### Changed
- Update to Forge 1.20.1-47.0.34 to add compatibility with Chiseled Bookshelf

## [1.20-1.5.0.0] - 2023-06-08
### Changed
- Update to Forge 1.20-46.0.1 #16

## [1.19.4-1.4.0.0] - 2023-03-18
### Changed
- Update to Forge 1.19.4-45.0.9

## [1.19.3-1.3.3.1] - 2023-02-05
### Changed
- Translation fixes for pt_br and addition of translation for pt_pt #14 (thanks to sanduicheirainox)

## [1.19.3-1.3.3.0] - 2022-12-30
### Changed
- Update to Forge 1.19.3-44.0.41

### Fixed
- The wooden hopper did not fill up with the item it contained even though there was space.

## [1.19-1.3.2.1] - 2022-12-28
### Fixed
- incompatibility with comparators when Canary is installed

## [1.19-1.3.2.0] - 2022-07-14
### Changed
- Update mod to Forge 1.19-41.0.96 to fix startup crashes since Forge 1.19-41.0.94

## [1.19-1.3.1.0] - 2022-06-30
### Changed
- Update mod to Forge 1.19-41.0.56 to support interaction with chest boats

## [1.19-1.3.0.0] - 2022-06-23
### Added
- Support filling and emptying for entities with item handlers (Chest Minecart ...)

### Changed
- Update mod to Forge 1.19-41.0.45

## [1.18.1-1.2.1.0] - 2022-01-16
### Changed
- Update mod to Forge 1.18.1-39.0.0 (fix Log4J security issue)

### Fixed
- Items disappeared from hoppers inventory when closing the world #7 (thanks to JoeThePanda for the report)

## [1.18-1.2.0.1] - 2021-12-04
### Changed
- Update mod to Forge 1.18-38.0.4

## [1.17.1-1.2.0.1] - 2021-08-12
### Fixed
- Wooden Hopper had no effective tool

## [1.17.1-1.2.0.0] - 2021-08-11
### Changed
- Update mod to Forge 1.17.1-37.0.32

## [1.16.5-1.2.0.0] - 2021-08-11
### Added
- Russian and Ukrainian translation #5 (thanks to vstannumdum aka DMHYT)

### Changed
- Update mod to Forge 1.16.5-36.1.0
- changed versioning to fit [Forge Recommended Versioning](https://mcforge.readthedocs.io/en/latest/conventions/versioning/)

## [1.1.0_1.16] - 2021-04-03
### Added
- Russian language support added #2 (thanks to sashafiesta)
- two new config options added #3 (thanks to martiesim for the idea)
- woodenHopperPullItemsFromWorldEnabled - enable/disable pulling item entities from world (default enabled)
- woodenHopperPullItemsFromInventoriesEnabled - enable/disable pulling items from inventories (default enabled)

## [1.0.1_1.16] - 2021-03-17
### Added
- pt_br language support added (thanks to Mikeliro)

## [1.0.0_1.16] - 2021-03-17
### Fixed
- Bugfix: Wooden Hopper did not interact with the Composter

## [0.2.0_1.16] - 2021-01-28
### Fixed
- Bugfix: Wooden Hopper didn't drop after breaking it.

## [0.1.0_1.16] - 2021-01-28
### Added
- Adds a Wooden Hopper to the game.
- It is half as fast as a vanilla hopper. (16 ticks instead of 8 - configurable)
- It has only one slot.
