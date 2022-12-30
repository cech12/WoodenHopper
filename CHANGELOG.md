# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Forge Recommended Versioning](https://mcforge.readthedocs.io/en/latest/conventions/versioning/).

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
