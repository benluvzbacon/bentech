#!/usr/bin/env bash
#
# Generates every texture, item model, block model, blockstate, lang entry,
# worldgen datapack JSON and a starter set of crafting recipes for BenTech.
#
# Run from the repository root:  bash tools/generate_assets.sh
#
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
RES="$ROOT/src/main/resources/assets/bentech"
TEX_ITEM="$RES/textures/item"
TEX_BLOCK="$RES/textures/block"
MODEL_ITEM="$RES/models/item"
MODEL_BLOCK="$RES/models/block"
BLOCKSTATE="$RES/blockstates"
LANG="$RES/lang"
WORLD="$ROOT/src/main/resources/data/bentech/worldgen"
CONF="$WORLD/configured_feature"
PLACED="$WORLD/placed_feature"
RECIPE="$ROOT/src/main/resources/data/bentech/recipe"

mkdir -p "$TEX_ITEM" "$TEX_BLOCK" "$MODEL_ITEM" "$MODEL_BLOCK" "$BLOCKSTATE" "$LANG" \
         "$ROOT/src/main/resources/data/bentech/worldgen/configured_feature" \
         "$ROOT/src/main/resources/data/bentech/worldgen/placed_feature" \
         "$RECIPE"

# form -> ImageMagick draw command (white shape; colour is passed via $COLOR)
form_draw() {
  case "$1" in
    dust) echo "point 3,3 point 8,5 point 12,9 point 5,12 point 9,14 point 13,3 point 2,9 point 14,12 point 6,8" ;;
    ingot) echo "roundrectangle 2,6 13,10 1,1" ;;
    nugget) echo "roundrectangle 5,5 10,10 1,1" ;;
    plate) echo "rectangle 2,7 13,9" ;;
    rod) echo "roundrectangle 7,2 9,13 1,1" ;;
    gear) echo "circle 8,8 8,3" ;;
    *) echo "rectangle 2,2 13,13" ;;
  esac
}

# name:display:color:hasOre
MATERIALS=(
  "bronze:Bronze:#BE7448:0"
  "iron:Iron:#C8C8C8:1"
  "copper:Copper:#B87333:1"
  "tin:Tin:#D0D0D0:1"
  "zinc:Zinc:#A0A0A0:1"
  "lead:Lead:#66666F:1"
  "silver:Silver:#C0C0C0:1"
  "gold:Gold:#F2D230:1"
  "steel:Steel:#8F8F8F:0"
  "nickel:Nickel:#D5D5D5:1"
  "invar:Invar:#B9C1B4:0"
  "aluminium:Aluminium:#D6D6D6:1"
  "brass:Brass:#E1BE5B:0"
  "titanium:Titanium:#9A9A9A:1"
  "chrome:Chrome:#B9F3E0:1"
  "tungsten:Tungsten:#9B9B9B:1"
  "platinum:Platinum:#E0E0E0:1"
  "iridium:Iridium:#DBDBDB:1"
  "osmium:Osmium:#A0A0A0:1"
)

# machine id:display:color
MACHINES=(
  "macerator:Macerator:#BE7448"
  "electric_furnace:Electric Furnace:#C8C8C8"
  "alloy_smelter:Alloy Smelter:#E1BE5B"
  "compressor:Compressor:#B9C1B4"
  "generator:Generator:#B22222"
)

lang="{}"
lang_lines=()

write_json() { printf '%s' "$2" > "$1"; }

# --- icon (a simple logo) ---
convert -size 64x64 xc:'#2b2b2b' -fill '#BE7448' -draw 'roundrectangle 4,4 60,60 6,6' \
        -fill '#C8C8C8' -draw 'roundrectangle 12,12 52,52 4,4' \
        -fill '#2b2b2b' -draw 'circle 32,32 32,20' -fill '#F2D230' -draw 'circle 32,32 32,24' \
        "$RES/icon.png" >/dev/null 2>&1 || true

# --- material forms ---
for entry in "${MATERIALS[@]}"; do
  IFS=':' read -r name display color hasore <<< "$entry"

  for form in dust ingot nugget plate rod gear; do
    draw="$(form_draw "$form")"
    tex="$TEX_ITEM/${name}_${form}.png"
    convert -size 16x16 xc:none -fill "$color" -draw "$draw" "$tex" >/dev/null 2>&1 || true
    write_json "$MODEL_ITEM/${name}_${form}.json" "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"bentech:item/${name}_${form}\"}}"
    lang_lines+=("\"item.bentech.${name}_${form}\": \"$display $(printf '%s' "$form" | sed 's/.*/\u&/g')\"")
  done

  # metal block
  bname="block_${name}"
  btex="$TEX_BLOCK/$bname.png"
  convert -size 16x16 xc:none -fill "$color" -draw 'rectangle 1,1 14,14' \
          -fill "$(python3 -c "print('#000000')")" -draw 'rectangle 1,1 14,2' \
          -draw 'rectangle 1,13 14,14' -draw 'rectangle 1,1 2,14' -draw 'rectangle 13,1 14,14' \
          -fill "$color" -draw 'rectangle 3,3 12,12' "$btex" >/dev/null 2>&1 || true
  write_json "$MODEL_BLOCK/$bname.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/$bname\"}}"
  write_json "$BLOCKSTATE/$bname.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/$bname\"}}}"
  write_json "$MODEL_ITEM/$bname.json" "{\"parent\":\"bentech:block/$bname\"}"
  lang_lines+=("\"block.bentech.${bname}\": \"Block of $display\"")

  # ore
  if [ "$hasore" = "1" ]; then
    oname="ore_${name}"
    otex="$TEX_BLOCK/$oname.png"
    convert -size 16x16 xc:'#6f6f6f' -fill '#4a4a4a' -draw 'rectangle 0,0 15,1' \
            -fill "$color" -draw 'point 4,4 point 11,6 point 7,11 point 13,13 point 3,12 point 10,3' \
            -fill "$(python3 -c "print('#000000')")" -draw 'point 1,8 point 8,14 point 15,9' \
            "$otex" >/dev/null 2>&1 || true
    write_json "$MODEL_BLOCK/$oname.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/$oname\"}}"
    write_json "$BLOCKSTATE/$oname.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/$oname\"}}}"
    write_json "$MODEL_ITEM/$oname.json" "{\"parent\":\"bentech:block/$oname\"}"
    lang_lines+=("\"block.bentech.${oname}\": \"$display Ore\"")
  fi
done

# --- machines ---
for entry in "${MACHINES[@]}"; do
  IFS=':' read -r id display color <<< "$entry"
  btex="$TEX_BLOCK/$id.png"
  convert -size 16x16 xc:none -fill '#4f4f4f' -draw 'rectangle 1,1 14,14' \
          -fill '#6a6a6a' -draw 'rectangle 3,3 12,12' \
          -fill "$color" -draw 'roundrectangle 6,6 9,9 1,1' \
          -fill '#2b2b2b' -draw 'rectangle 4,2 11,3' -draw 'rectangle 4,12 11,13' \
          "$btex" >/dev/null 2>&1 || true
  write_json "$MODEL_BLOCK/$id.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/$id\"}}"
  write_json "$BLOCKSTATE/$id.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/$id\"}}}"
  write_json "$MODEL_ITEM/$id.json" "{\"parent\":\"bentech:block/$id\"}"
  lang_lines+=("\"block.bentech.${id}\": \"$display\"")
done

# --- component items ---
COMPONENTS=(
  "machine_casing:Machine Casing:#9A9A9A"
  "basic_circuit:Basic Circuit:#F2D230"
  "advanced_circuit:Advanced Circuit:#DBDBDB"
  "electric_motor:Electric Motor:#B87333"
  "electric_pump:Electric Pump:#66AABB"
  "capacitor:Capacitor:#C0C0C0"
)
for entry in "${COMPONENTS[@]}"; do
  IFS=':' read -r id display color <<< "$entry"
  convert -size 16x16 xc:none -fill "$color" -draw 'roundrectangle 4,5 11,10 1,1' \
          -fill '#000000' -draw 'rectangle 7,2 8,4' -draw 'rectangle 7,11 8,13' \
          "$TEX_ITEM/$id.png" >/dev/null 2>&1 || true
  write_json "$MODEL_ITEM/$id.json" "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"bentech:item/$id\"}}"
  lang_lines+=("\"item.bentech.${id}\": \"$display\"")
done

# --- language file ---
lang_lines+=("\"itemGroup.bentech\": \"BenTech\"")
lang_lines+=("\"message.bentech.no_output\": \"No output to extract\"")
lang_lines+=("\"message.bentech.input_full\": \"Input slots are full\"")
{
  echo "{"
  last=$(( ${#lang_lines[@]} - 1 ))
  for i in "${!lang_lines[@]}"; do
    comma=","
    if [ "$i" -eq "$last" ]; then comma=""; fi
    printf '  %s%s\n' "${lang_lines[$i]}" "$comma"
  done
  echo "}"
} > "$LANG/en_us.json"

# --- crafting recipes (vanilla shaped) ---
write_json "$RECIPE/machine_casing.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["ppp","ppp","ppp"],
  "key": {"p": {"item": "bentech:iron_plate"}},
  "result": {"item": "bentech:machine_casing", "count": 4}
}'
write_json "$RECIPE/capacitor.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["iii","g g","iii"],
  "key": {"i": {"item": "bentech:copper_plate"}, "g": {"item": "bentech:gold_nugget"}},
  "result": {"item": "bentech:capacitor", "count": 2}
}'
write_json "$RECIPE/basic_circuit.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["ggg","gc g","ggg"],
  "key": {"g": {"item": "bentech:copper_plate"}, "c": {"item": "bentech:capacitor"}},
  "result": {"item": "bentech:basic_circuit", "count": 1}
}'
write_json "$RECIPE/advanced_circuit.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["ggg","gcg","ggg"],
  "key": {"g": {"item": "bentech:gold_plate"}, "c": {"item": "bentech:basic_circuit"}},
  "result": {"item": "bentech:advanced_circuit", "count": 1}
}'
write_json "$RECIPE/electric_motor.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["r r","rg r","r r"],
  "key": {"r": {"item": "bentech:copper_rod"}, "g": {"item": "bentech:iron_gear"}},
  "result": {"item": "bentech:electric_motor", "count": 1}
}'
write_json "$RECIPE/electric_pump.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["r r","pr p","r r"],
  "key": {"r": {"item": "bentech:iron_rod"}, "p": {"item": "bentech:copper_plate"}},
  "result": {"item": "bentech:electric_pump", "count": 1}
}'
write_json "$RECIPE/macerator.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["mpm","mcm","mmm"],
  "key": {"m": {"item": "bentech:machine_casing"}, "p": {"item": "bentech:electric_pump"}, "c": {"item": "bentech:basic_circuit"}},
  "result": {"item": "bentech:macerator", "count": 1}
}'
write_json "$RECIPE/electric_furnace.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["mbm","mcm","mmm"],
  "key": {"m": {"item": "bentech:machine_casing"}, "b": {"item": "minecraft:blast_furnace"}, "c": {"item": "bentech:basic_circuit"}},
  "result": {"item": "bentech:electric_furnace", "count": 1}
}'
write_json "$RECIPE/compressor.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["mm m","mcm","mmm"],
  "key": {"m": {"item": "bentech:machine_casing"}, "c": {"item": "bentech:basic_circuit"}},
  "result": {"item": "bentech:compressor", "count": 1}
}'
write_json "$RECIPE/alloy_smelter.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["   ","aca","   "],
  "key": {"a": {"item": "bentech:advanced_circuit"}, "c": {"item": "bentech:machine_casing"}},
  "result": {"item": "bentech:alloy_smelter", "count": 1}
}'
write_json "$RECIPE/generator.json" '{
  "type": "minecraft:crafting_shaped",
  "pattern": ["mmm","mcm","c m"],
  "key": {"m": {"item": "bentech:machine_casing"}, "c": {"item": "bentech:basic_circuit"}},
  "result": {"item": "bentech:generator", "count": 1}
}'

# --- worldgen datapack JSONs ---
for entry in "${MATERIALS[@]}"; do
  IFS=':' read -r name display color hasore <<< "$entry"
  if [ "$hasore" = "0" ]; then continue; fi
  write_json "$CONF/ore_${name}.json" "{\"type\":\"minecraft:ore\",\"config\":{\"discard_chance_on_air_exposure\":0.0,\"size\":8,\"targets\":[{\"target\":{\"predicate_type\":\"minecraft:tag_match\",\"tag\":\"minecraft:stone_ore_replaceables\"},\"state\":{\"Name\":\"bentech:ore_${name}\"}},{\"target\":{\"predicate_type\":\"minecraft:tag_match\",\"tag\":\"minecraft:deepslate_ore_replaceables\"},\"state\":{\"Name\":\"bentech:ore_${name}\"}}]}}"
  write_json "$PLACED/ore_${name}.json" "{\"feature\":\"bentech:ore_${name}\",\"placement\":[{\"type\":\"minecraft:count\",\"count\":8},{\"type\":\"minecraft:in_square\"},{\"type\":\"minecraft:height_range\",\"height\":{\"type\":\"minecraft:trapezoid\",\"min_inclusive\":{\"absolute\":-24},\"max_inclusive\":{\"absolute\":64}}},{\"type\":\"minecraft:biome\"}]}"
done

echo "Done. Generated assets for ${#MATERIALS[@]} materials and ${#MACHINES[@]} machines."
