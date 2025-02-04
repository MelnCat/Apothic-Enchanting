## 1.2.3
* Added back the enchanted book tooltips present in Apotheosis, these got lost in the split-off somehow.
  * This includes adding a description even if Enchantment Descriptions isn't installed, and showing various metadata information.

## 1.2.2
* Fixed Boon of the Earth being unlocalized.
* Translated Infusion JEI tooltips up by 100 on the Z axis (should fix some overlap issues).

## 1.2.1
* Updated to Placebo 9.5.1.

## 1.2.0
* Updated to Minecraft 1.21.1.
* Made Tridents and Shears able to accept all enchantments that were possible in 1.20.
  * This was not possible earlier as it relied on Neo's `Item#supportsEnchantment` hook.
* Fixed sheep-specific shear enchantments crashing when used.
* Tightened tooltip level access when resolving enchanting stat tooltips. Falls back to the default block state when unavailable.

## 1.1.2
* Fixed crossbows crashing on fire.
* Fixed the warden loot modifier failing when the TOOL parameter was not provided.
* Fixed enchantment redirect coremods triggering a sided crash on dedicated servers.
* Renamed `apothic_enchanting:earths_boon` to `apothic_enchanting:boon_of_the_earth`.
* Set Endless Quiver's max level to 1 (should always have been 1).
* Fixed all shear enchantments (pending merge of a recent Neo PR).

## 1.1.1
* Fixed various invalid tag paths and broken recipes. This should fix a wide variety of behaviors that were caused by tags not being loaded.
* Fixed Enchantment Libraries not displaying the number of stored enchantments in item form.
* Fixed Quantic Stability causing all enchantments to be rolled at max enchanting power (200).
* Fixed enchantment name colors to match the original values for corrupted / twisted / masterwork enchantments.

## 1.1.0
* Alpha update to Minecraft 1.21. Various things may be incomplete or missing!

## 1.0.0
* Initial Release