# BenTech

A **GregTech-inspired** technology modification for **Minecraft 1.21.1** on the **Fabric** loader
(Java 21). It recreates the classic GregTech experience: a deep, interconnected material and
machine progression with dusts, ingots, plates, ores, energy (EU), automation and voltage tiers.

> This is an original re-implementation created as a learning/educational exercise using
> GregTech's public documentation as a *reference* for the gameplay systems. It does **not**
> copy GregTech's source code or assets.

## Features

### Materials & progression
19 materials spanning the classic voltage tiers:
`Bronze, Iron, Copper, Tin, Zinc, Lead, Silver, Gold, Steel, Nickel, Invar, Aluminium, Brass,
Titanium, Chrome, Tungsten, Platinum, Iridium, Osmium`.

Each material can be produced in several forms: **dust, ingot, nugget, plate, rod, gear**,
plus a metal block, and (where naturally occurring) an ore block. The materials list defines a
clear progression: you start with LV metals (iron, copper, tin, bronze) and work up to exotic
HV/EV/IV metals (titanium, iridium, osmium).

### Machines (all with block entities, energy & automation)
- **Macerator** — ore → 2× dust
- **Electric Furnace** — dust → ingot
- **Compressor** — ingot → plate
- **Alloy Smelter** — two components → alloy dust (bronze, steel, invar, brass)
- **Generator** — burns fuel into energy and pushes EU to adjacent machines

Machines hold an internal EU buffer, process items over time while consuming energy, are fully
hopper-automatable (they implement `Container`), and can be fed/extracted by right-clicking.

### Energy & automation
A simplified but functional EU model: the generator burns coal/charcoal to charge its buffer and
pushes energy into neighbouring machines. Batteries and cables are a natural next extension.

### Recipes
- **Crafting recipes** (vanilla shaped JSON) for every machine and component.
- **Machine recipes** (code-defined) implement the processing chain
  `ore → dust → ingot → plate`, plus the alloy smelter recipes.

### World generation
Ore blocks generate in the overworld via datapack `configured_feature`/`placed_feature` files,
attached to biomes through the Fabric Biome API.

### Tiers
A `Tier` enum (`ULV`…`UV`) records a nominal voltage for each stage of the progression.

## Build

Requires **Java 21** and network access to Maven/Fabric repos (Gradle + Loom resolve
Minecraft, Yarn/Official mappings and Fabric API automatically).

```bash
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows
```

The runnable mod JAR is generated at:

```
build/libs/bentech-1.0.0.jar
```

Drop that JAR into your `mods/` folder (with Fabric Loader + Fabric API for 1.21.1).

## Project layout

```
src/main/java/com/bentech
├── api/            # Tier, Material, Materials
├── block/          # MachineBlock, MachineType
├── block/entity/   # machine block entities + energy/processing logic
├── item/           # MaterialItem (material-form items)
├── registry/       # items, blocks, block entities, creative tab
├── util/           # code-defined machine recipes
└── world/          # overworld ore generation hook
src/main/resources
├── assets/bentech/ # textures, models, blockstates, lang
└── data/bentech/   # worldgen + crafting recipes
tools/generate_assets.sh  # regenerates all textures / models / datapack JSON
```

> **Note for the Arena sandbox:** the sandbox has no JDK and its network only reaches GitHub,
> so `./gradlew build` cannot run here (Gradle, Minecraft and the Fabric/Maven repositories are
> unreachable). Run the build in an environment that has Java 21 and normal Maven/Fabric access.
