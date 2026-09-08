#!/usr/bin/env bash
#
# Generates every texture, item model, block model, blockstate, lang entry,
# mining tags, worldgen datapack JSON and crafting recipe for BenTech.
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
DATA="$ROOT/src/main/resources/data"
MC_TAG="$DATA/minecraft/tags/block"
RECIPE="$DATA/bentech/recipe"
CONF="$DATA/bentech/worldgen/configured_feature"
PLACED="$DATA/bentech/worldgen/placed_feature"
LOOT="$DATA/bentech/loot_table/blocks"
GUI="$RES/textures/gui"

mkdir -p "$TEX_ITEM" "$TEX_BLOCK" "$MODEL_ITEM" "$MODEL_BLOCK" "$BLOCKSTATE" "$LANG" \
         "$MC_TAG" "$MC_TAG/mineable" "$RECIPE" "$CONF" "$PLACED" "$LOOT" "$GUI"

# --- colour helpers ---------------------------------------------------------
darken() { python3 -c "import sys;c=sys.argv[1].lstrip('#');r,g,b=[int(c[i:i+2],16) for i in (0,2,4)];r,g,b=[int(x*0.6) for x in (r,g,b)];print(f'#{r:02x}{g:02x}{b:02x}')" "$1"; }
lighten() { python3 -c "import sys;c=sys.argv[1].lstrip('#');r,g,b=[int(c[i:i+2],16) for i in (0,2,4)];r,g,b=[min(240,int(x*1.25+12)) for x in (r,g,b)];print(f'#{r:02x}{g:02x}{b:02x}')" "$1"; }

write_json() { printf '%s' "$2" > "$1"; }

# Standard block loot table that drops the block itself.
write_loot() {
  local name="$1"
  write_json "$LOOT/$name.json" "{\"type\":\"minecraft:block\",\"pools\":[{\"bonus_rolls\":0,\"conditions\":[{\"condition\":\"minecraft:survives_explosion\"}],\"entries\":[{\"type\":\"minecraft:item\",\"name\":\"bentech:$name\"}],\"rolls\":1}]}"
}

# --- form draw shapes -------------------------------------------------------
form_shape() {
  case "$1" in
    dust) echo "point 3,3 point 8,5 point 12,9 point 5,12 point 9,14 point 13,3 point 2,9 point 14,12 point 6,8" ;;
    ingot) echo "roundrectangle 2,6 13,10 1,1" ;;
    nugget) echo "roundrectangle 5,5 10,10 1,1" ;;
    plate) echo "rectangle 2,7 13,9" ;;
    rod) echo "roundrectangle 7,2 9,13 1,1" ;;
    gear) echo "rectangle 3,7 12,9 rectangle 7,3 9,12 circle 8,8 8,4" ;;
    wire) echo "rectangle 3,4 12,5 rectangle 3,8 12,9 rectangle 3,12 12,13" ;;
    *) echo "rectangle 2,2 13,13" ;;
  esac
}

# --- icon -------------------------------------------------------------------
convert -size 64x64 xc:'#1b1b1f' -fill '#3a3a42' -draw 'roundrectangle 2,2 61,61 8,8' \
        -fill '#BE7448' -draw 'roundrectangle 8,8 55,55 6,6' \
        -fill '#1b1b1f' -draw 'circle 32,32 32,22' -fill '#F2D230' -draw 'circle 32,32 32,26' \
        "$RES/icon.png" >/dev/null 2>&1 || true

# machine id:display:harvest
MACHINES=(
  "macerator:Macerator:#BE7448"
  "electric_furnace:Electric Furnace:#C8C8C8"
  "alloy_smelter:Alloy Smelter:#E1BE5B"
  "compressor:Compressor:#B9C1B4"
  "wiremill:Wiremill:#9AB8C8"
  "recycler:Recycler:#7FBF7F"
  "centrifuge:Centrifuge:#B9F3E0"
  "generator:Generator:#B22222"
)

# name:display:color:hasOre:tier(harvest)
MATERIALS=(
  "bronze:Bronze:#BE7448:0:LV"
  "iron:Iron:#C8C8C8:0:LV"
  "copper:Copper:#B87333:0:LV"
  "tin:Tin:#D0D0D0:1:LV"
  "zinc:Zinc:#A0A0A0:1:LV"
  "lead:Lead:#66666F:1:LV"
  "silver:Silver:#C0C0C0:1:LV"
  "gold:Gold:#F2D230:0:LV"
  "steel:Steel:#8F8F8F:0:MV"
  "nickel:Nickel:#D5D5D5:1:MV"
  "invar:Invar:#B9C1B4:0:MV"
  "aluminium:Aluminium:#D6D6D6:1:MV"
  "brass:Brass:#E1BE5B:0:MV"
  "titanium:Titanium:#9A9A9A:1:HV"
  "chrome:Chrome:#B9F3E0:1:HV"
  "tungsten:Tungsten:#9B9B9B:1:HV"
  "platinum:Platinum:#E0E0E0:1:EV"
  "iridium:Iridium:#DBDBDB:1:EV"
  "osmium:Osmium:#A0A0A0:1:IV"
  "ruby:Ruby:#E0115F:1:MV"
  "sapphire:Sapphire:#0F52BA:1:MV"
  "iridium_alloy:Iridium Alloy:#DCDCDC:0:IV"
)

lang_lines=()
add_lang() { lang_lines+=("\"$1\": \"$2\""); }

# --- icon note: forge "wire" item models ------------------------------------
# materials: forms + block + optional ore
for entry in "${MATERIALS[@]}"; do
  IFS=':' read -r name display color hasore tier <<< "$entry"
  dark="$(darken "$color")"
  light="$(lighten "$color")"

  for form in dust ingot nugget plate rod gear wire; do
    shape="$(form_shape "$form")"
    tex="$TEX_ITEM/${name}_${form}.png"
    convert -size 16x16 xc:none \
      -fill "$dark" -draw "translate 1,1 $shape" \
      -fill "$color" -draw "translate 0,0 $shape" \
      -fill 'rgba(255,255,255,0.20)' -draw "translate -1,-1 $shape" \
      "$tex" >/dev/null 2>&1 || true
    write_json "$MODEL_ITEM/${name}_${form}.json" "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"bentech:item/${name}_${form}\"}}"
    add_lang "item.bentech.${name}_${form}" "$display $(printf '%s' "$form" | sed 's/.*/\u&/g')"
  done

  # metal block
  bname="block_${name}"
  btex="$TEX_BLOCK/$bname.png"
  convert -size 16x16 xc:none \
    -fill "$dark" -draw 'rectangle 0,0 15,15' \
    -fill "$(lighten "$color")" -draw 'rectangle 1,1 14,14' \
    -fill "$color" -draw 'rectangle 3,3 12,12' \
    -fill "$(darken "$color")" -draw 'rectangle 3,3 12,4' \
    -draw 'rectangle 3,11 12,12' -draw 'rectangle 3,3 4,12' -draw 'rectangle 11,3 12,12' \
    -fill 'rgba(255,255,255,0.18)' -draw 'rectangle 4,4 11,5' \
    "$btex" >/dev/null 2>&1 || true
  write_json "$MODEL_BLOCK/$bname.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/$bname\"}}"
  write_json "$BLOCKSTATE/$bname.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/$bname\"}}}"
  write_json "$MODEL_ITEM/$bname.json" "{\"parent\":\"bentech:block/$bname\"}"
  add_lang "block.bentech.${bname}" "Block of $display"

  # ore
  if [ "$hasore" = "1" ]; then
    oname="ore_${name}"
    otex="$TEX_BLOCK/$oname.png"
    convert -size 16x16 xc:none \
      -fill '#4a4a4a' -draw 'rectangle 0,0 15,15' \
      -fill '#8a8a8a' -draw 'rectangle 1,1 14,14' \
      -fill '#6f6f6f' -draw 'rectangle 2,2 13,13' \
      -fill '#606060' -draw 'point 1,5 point 4,1 point 9,1 point 14,6 point 15,12 point 11,15 point 5,15 point 1,11' \
      -fill "$color" -draw 'point 4,4 point 7,6 point 11,5 point 9,10 point 5,9 point 12,12 point 3,12 point 11,3' \
      -fill "$light" -draw 'point 3,5 point 8,4 point 12,6 point 6,11 point 10,13' \
      -fill "$dark" -draw 'point 5,7 point 10,9 point 8,12 point 3,10' \
      "$otex" >/dev/null 2>&1 || true
    write_json "$MODEL_BLOCK/$oname.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/$oname\"}}"
    write_json "$BLOCKSTATE/$oname.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/$oname\"}}}"
    write_json "$MODEL_ITEM/$oname.json" "{\"parent\":\"bentech:block/$oname\"}"
    add_lang "block.bentech.${oname}" "$display Ore"
  fi
done

# --- machines & cable -------------------------------------------------------
for entry in "${MACHINES[@]}"; do
  IFS=':' read -r id display color <<< "$entry"
  dark="$(darken "$color")"
  btex="$TEX_BLOCK/$id.png"
  convert -size 16x16 xc:none \
    -fill '#2b2b31' -draw 'rectangle 0,0 15,15' \
    -fill '#5a5a62' -draw 'rectangle 1,1 14,14' \
    -fill '#6a6a72' -draw 'rectangle 1,1 14,2' -draw 'rectangle 1,1 2,14' \
    -fill '#33333a' -draw 'rectangle 1,13 14,14' -draw 'rectangle 13,1 14,14' \
    -fill "$color" -draw 'roundrectangle 4,4 11,11 2,2' \
    -fill "$dark" -draw 'roundrectangle 4,4 11,5 2,2' -draw 'roundrectangle 4,10 11,11 2,2' -draw 'roundrectangle 4,4 5,11 2,2' -draw 'roundrectangle 10,4 11,11 2,2' \
    -fill '#1b1b1f' -draw 'rectangle 6,6 9,9' \
    -fill 'rgba(255,255,255,0.25)' -draw 'rectangle 3,2 5,3' \
    -fill 'rgba(255,255,255,0.6)' -draw 'point 4,3' \
    "$btex" >/dev/null 2>&1 || true
  write_json "$MODEL_BLOCK/$id.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/$id\"}}"
  write_json "$BLOCKSTATE/$id.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/$id\"}}}"
  write_json "$MODEL_ITEM/$id.json" "{\"parent\":\"bentech:block/$id\"}"
  add_lang "block.bentech.${id}" "$display"
done

# cable block
convert -size 16x16 xc:none \
  -fill '#2b2b31' -draw 'rectangle 0,0 15,15' \
  -fill '#B87333' -draw 'rectangle 2,6 13,9' \
  -fill '#E0C090' -draw 'rectangle 3,7 12,8' \
  -fill '#33333a' -draw 'rectangle 0,0 15,1' -draw 'rectangle 0,14 15,15' \
  "$TEX_BLOCK/cable.png" >/dev/null 2>&1 || true
write_json "$MODEL_BLOCK/cable.json" "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"bentech:block/cable\"}}"
write_json "$BLOCKSTATE/cable.json" "{\"variants\":{\"\":{\"model\":\"bentech:block/cable\"}}}"
write_json "$MODEL_ITEM/cable.json" "{\"parent\":\"bentech:block/cable\"}"
add_lang "block.bentech.cable" "Power Cable"

# --- loot tables (drop-the-block-self) --------------------------------------
# Without these, hand-mined blocks (ores/machines/metal blocks) drop nothing.
for entry in "${MATERIALS[@]}"; do
  IFS=':' read -r name display color hasore tier <<< "$entry"
  write_loot "block_${name}"
  if [ "$hasore" = "1" ]; then
    write_loot "ore_${name}"
  fi
done
for entry in "${MACHINES[@]}"; do
  IFS=':' read -r id display color <<< "$entry"
  write_loot "$id"
done
write_loot "cable"

# --- component items (with nicer sprites) -----------------------------------
COMPONENTS=(
  "machine_casing:Machine Casing:#9A9A9A"
  "basic_circuit:Basic Circuit:#F2D230"
  "advanced_circuit:Advanced Circuit:#DBDBDB"
  "electric_motor:Electric Motor:#B87333"
  "electric_pump:Electric Pump:#66AABB"
  "capacitor:Capacitor:#C0C0C0"
  "battery:Battery:#4CAF50"
  "emitter:Emitter:#FF7043"
  "sensor:Sensor:#29B6F6"
)
for entry in "${COMPONENTS[@]}"; do
  IFS=':' read -r id display color <<< "$entry"
  dark="$(darken "$color")"
  shape="roundrectangle 4,5 11,10 1,1"
  convert -size 16x16 xc:none \
    -fill "$dark" -draw "translate 1,1 $shape" \
    -fill "$color" -draw "$shape" \
    -fill 'rgba(255,255,255,0.25)' -draw "translate -1,-1 $shape" \
    -fill '#1b1b1f' -draw 'rectangle 7,2 8,4' -draw 'rectangle 7,11 8,13' \
    "$TEX_ITEM/$id.png" >/dev/null 2>&1 || true
  write_json "$MODEL_ITEM/$id.json" "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"bentech:item/$id\"}}"
  add_lang "item.bentech.${id}" "$display"
done

# --- language file ----------------------------------------------------------
add_lang "itemGroup.bentech" "BenTech"
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

# --- mining tags (pickaxe + harvest levels) ----------------------------------
MINEABLE=""
STONE=""
IRON=""
DIAMOND=""

all_blocks() { echo "bentech:macerator bentech:electric_furnace bentech:alloy_smelter bentech:compressor bentech:wiremill bentech:recycler bentech:centrifuge bentech:generator bentech:cable"; }

for entry in "${MATERIALS[@]}"; do
  IFS=':' read -r name display color hasore tier <<< "$entry"
  MINEABLE="$MINEABLE bentech:block_${name}"
  case "$tier" in
    LV) STONE="$STONE bentech:block_${name}"; IRON="$IRON"; DIAMOND="$DIAMOND" ;;
    MV) IRON="$IRON bentech:block_${name}"; DIAMOND="$DIAMOND" ;;
    *) DIAMOND="$DIAMOND bentech:block_${name}"; IRON="$IRON" ;;
  esac
  if [ "$hasore" = "1" ]; then
    MINEABLE="$MINEABLE bentech:ore_${name}"
    case "$tier" in
      LV) STONE="$STONE bentech:ore_${name}" ;;
      MV) IRON="$IRON bentech:ore_${name}" ;;
      *) DIAMOND="$DIAMOND bentech:ore_${name}" ;;
    esac
  fi
done

json_values() {
  local out=""
  for b in $1; do out="$out\"$b\","; done
  echo "${out%,}"
}

# machines are stone-mineable
STONE="$STONE $(all_blocks)"

write_json "$MC_TAG/mineable/pickaxe.json" "{\"replace\":false,\"values\":[$(json_values "$MINEABLE $STONE")]}"
write_json "$MC_TAG/needs_stone_tool.json" "{\"replace\":false,\"values\":[$(json_values "$STONE")]}"
write_json "$MC_TAG/needs_iron_tool.json" "{\"replace\":false,\"values\":[$(json_values "$IRON")]}"
write_json "$MC_TAG/needs_diamond_tool.json" "{\"replace\":false,\"values\":[$(json_values "$DIAMOND")]}"

# --- worldgen datapack JSONs -------------------------------------------------
for entry in "${MATERIALS[@]}"; do
  IFS=':' read -r name display color hasore tier <<< "$entry"
  if [ "$hasore" = "0" ]; then continue; fi
  write_json "$CONF/ore_${name}.json" "{\"type\":\"minecraft:ore\",\"config\":{\"discard_chance_on_air_exposure\":0.0,\"size\":8,\"targets\":[{\"target\":{\"predicate_type\":\"minecraft:tag_match\",\"tag\":\"minecraft:stone_ore_replaceables\"},\"state\":{\"Name\":\"bentech:ore_${name}\"}},{\"target\":{\"predicate_type\":\"minecraft:tag_match\",\"tag\":\"minecraft:deepslate_ore_replaceables\"},\"state\":{\"Name\":\"bentech:ore_${name}\"}}]}}"
  write_json "$PLACED/ore_${name}.json" "{\"feature\":\"bentech:ore_${name}\",\"placement\":[{\"type\":\"minecraft:count\",\"count\":8},{\"type\":\"minecraft:in_square\"},{\"type\":\"minecraft:height_range\",\"height\":{\"type\":\"minecraft:trapezoid\",\"min_inclusive\":{\"absolute\":-24},\"max_inclusive\":{\"absolute\":64}}},{\"type\":\"minecraft:biome\"}]}"
done

# --- crafting recipes ---------------------------------------------------------
write_json "$RECIPE/iron_plate.json" '{"type":"minecraft:crafting_shaped","pattern":["i","i"],"key":{"i":{"item":"minecraft:iron_ingot"}},"result":{"item":"bentech:iron_plate","count":2}}'
write_json "$RECIPE/copper_plate.json" '{"type":"minecraft:crafting_shaped","pattern":["c","c"],"key":{"c":{"item":"minecraft:copper_ingot"}},"result":{"item":"bentech:copper_plate","count":2}}'
write_json "$RECIPE/tin_plate.json" '{"type":"minecraft:crafting_shaped","pattern":["c","c"],"key":{"c":{"item":"bentech:tin_ingot"}},"result":{"item":"bentech:tin_plate","count":2}}'
write_json "$RECIPE/copper_wire.json" '{"type":"minecraft:crafting_shaped","pattern":["ccc"],"key":{"c":{"item":"minecraft:copper_ingot"}},"result":{"item":"bentech:copper_wire","count":1}}'
write_json "$RECIPE/machine_casing.json" '{"type":"minecraft:crafting_shaped","pattern":["ppp","ppp","ppp"],"key":{"p":{"item":"bentech:iron_plate"}},"result":{"item":"bentech:machine_casing","count":4}}'
write_json "$RECIPE/capacitor.json" '{"type":"minecraft:crafting_shaped","pattern":["iii","g g","iii"],"key":{"i":{"item":"bentech:copper_plate"},"g":{"item":"minecraft:gold_nugget"}},"result":{"item":"bentech:capacitor","count":2}}'
write_json "$RECIPE/basic_circuit.json" '{"type":"minecraft:crafting_shaped","pattern":["ggg","gc g","ggg"],"key":{"g":{"item":"bentech:copper_plate"},"c":{"item":"bentech:capacitor"}},"result":{"item":"bentech:basic_circuit","count":1}}'
write_json "$RECIPE/advanced_circuit.json" '{"type":"minecraft:crafting_shaped","pattern":["ggg","gcg","ggg"],"key":{"g":{"item":"minecraft:gold_ingot"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:advanced_circuit","count":1}}'
write_json "$RECIPE/electric_motor.json" '{"type":"minecraft:crafting_shaped","pattern":["r r","rg r","r r"],"key":{"r":{"item":"bentech:copper_rod"},"g":{"item":"bentech:iron_gear"}},"result":{"item":"bentech:electric_motor","count":1}}'
write_json "$RECIPE/electric_pump.json" '{"type":"minecraft:crafting_shaped","pattern":["r r","pr p","r r"],"key":{"r":{"item":"bentech:iron_rod"},"p":{"item":"bentech:copper_plate"}},"result":{"item":"bentech:electric_pump","count":1}}'
write_json "$RECIPE/battery.json" '{"type":"minecraft:crafting_shaped","pattern":[" g ","gcg"," g "],"key":{"g":{"item":"minecraft:gold_ingot"},"c":{"item":"bentech:capacitor"}},"result":{"item":"bentech:battery","count":1}}'
write_json "$RECIPE/sensor.json" '{"type":"minecraft:crafting_shaped","pattern":[" g ","grg"," c "],"key":{"g":{"item":"bentech:gold_plate"},"r":{"item":"bentech:ruby_ingot"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:sensor","count":1}}'
write_json "$RECIPE/emitter.json" '{"type":"minecraft:crafting_shaped","pattern":[" g ","gsg"," c "],"key":{"g":{"item":"bentech:gold_plate"},"s":{"item":"bentech:sapphire_ingot"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:emitter","count":1}}'
write_json "$RECIPE/cable.json" '{"type":"minecraft:crafting_shaped","pattern":["www","www","www"],"key":{"w":{"item":"bentech:copper_wire"}},"result":{"item":"bentech:cable","count":4}}'
write_json "$RECIPE/macerator.json" '{"type":"minecraft:crafting_shaped","pattern":["mpm","mcm","mmm"],"key":{"m":{"item":"bentech:machine_casing"},"p":{"item":"bentech:electric_pump"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:macerator","count":1}}'
write_json "$RECIPE/electric_furnace.json" '{"type":"minecraft:crafting_shaped","pattern":["mbm","mcm","mmm"],"key":{"m":{"item":"bentech:machine_casing"},"b":{"item":"minecraft:blast_furnace"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:electric_furnace","count":1}}'
write_json "$RECIPE/compressor.json" '{"type":"minecraft:crafting_shaped","pattern":["mmm","mcm","mmm"],"key":{"m":{"item":"bentech:machine_casing"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:compressor","count":1}}'
write_json "$RECIPE/wiremill.json" '{"type":"minecraft:crafting_shaped","pattern":["mmm","mcm","mpm"],"key":{"m":{"item":"bentech:machine_casing"},"c":{"item":"bentech:basic_circuit"},"p":{"item":"bentech:electric_motor"}},"result":{"item":"bentech:wiremill","count":1}}'
write_json "$RECIPE/recycler.json" '{"type":"minecraft:crafting_shaped","pattern":["mmm","mcm","mem"],"key":{"m":{"item":"bentech:machine_casing"},"c":{"item":"bentech:advanced_circuit"},"e":{"item":"bentech:emitter"}},"result":{"item":"bentech:recycler","count":1}}'
write_json "$RECIPE/centrifuge.json" '{"type":"minecraft:crafting_shaped","pattern":["mmm","mcm","msm"],"key":{"m":{"item":"bentech:machine_casing"},"c":{"item":"bentech:advanced_circuit"},"s":{"item":"bentech:sensor"}},"result":{"item":"bentech:centrifuge","count":1}}'
write_json "$RECIPE/alloy_smelter.json" '{"type":"minecraft:crafting_shaped","pattern":["mmm","mcm","mmm"],"key":{"m":{"item":"bentech:machine_casing"},"c":{"item":"bentech:advanced_circuit"}},"result":{"item":"bentech:alloy_smelter","count":1}}'
write_json "$RECIPE/generator.json" '{"type":"minecraft:crafting_shaped","pattern":["mmm","mcm","c m"],"key":{"m":{"item":"bentech:machine_casing"},"c":{"item":"bentech:basic_circuit"}},"result":{"item":"bentech:generator","count":1}}'

echo "Done. Generated assets for ${#MATERIALS[@]} materials, ${#MACHINES[@]} machines."
