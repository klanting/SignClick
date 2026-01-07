package com.klanting.signclick.utils;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.CompanyRef;
import com.klanting.signclick.logicLayer.companyLogic.producible.LicenseSingleton;
import com.klanting.signclick.logicLayer.companyLogic.logs.*;
import com.klanting.signclick.logicLayer.companyLogic.patent.*;
import com.klanting.signclick.logicLayer.companyLogic.upgrades.*;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.parties.Election;
import com.klanting.signclick.logicLayer.countryLogic.policies.Policy;
import com.klanting.signclick.utils.Serializers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.*;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

import static org.bukkit.Bukkit.getServer;


public class Utils {
    /*
    * Basic utils used everywhere
    * */

    static RuntimeTypeAdapterFactory<PluginLogs> pluginLogTypes =
            RuntimeTypeAdapterFactory.of(PluginLogs.class, "type")
                    .registerSubtype(ContractChange.class, "ContractChange")
                    .registerSubtype(ContractPayment.class, "ContractPayment")
                    .registerSubtype(MoneyTransfer.class, "MoneyTransfer")
                    .registerSubtype(ShareholderChange.class, "ShareholderChange")
                    .registerSubtype(ResearchUpdate.class, "ResearchUpdate")
                    .registerSubtype(ShopLogs.class, "ShopLogs")
                    .registerSubtype(MachineProduction.class, "MachineProduction")
            ;

    static RuntimeTypeAdapterFactory<CompanyI> companyTypes =
            RuntimeTypeAdapterFactory.of(CompanyI.class, "classType")
                    .registerSubtype(Company.class, "company")
                    .registerSubtype(CompanyRef.class, "companyRef")
            ;

    static RuntimeTypeAdapterFactory<PatentUpgrade> patentUpgradeTypes =
            RuntimeTypeAdapterFactory.of(PatentUpgrade.class, "classType")
                    .registerSubtype(PatentUpgradeCunning.class, "cunning")
                    .registerSubtype(PatentUpgradeEvade.class, "evade")
                    .registerSubtype(PatentUpgradeJumper.class, "jumper")
                    .registerSubtype(PatentUpgradeRefill.class, "refill")
            ;

    static RuntimeTypeAdapterFactory<Upgrade> upgradeTypes =
            RuntimeTypeAdapterFactory.of(Upgrade.class, "classType")
                    .registerSubtype(UpgradeBoardSize.class, "boardSize")
                    .registerSubtype(UpgradeInvestReturnTime.class, "investReturnTime")
                    .registerSubtype(UpgradePatentSlot.class, "patentSlot")
                    .registerSubtype(UpgradePatentUpgradeSlot.class, "patentUpgradeSlot")
                    .registerSubtype(UpgradeProductModifier.class, "productModifier")
                    .registerSubtype(UpgradeProductSlot.class, "productSlot")
                    .registerSubtype(UpgradeResearchModifier.class, "researchModifier")
            ;

    private static GsonBuilder getBuilder(){
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(Company.class, new CompanySerializer(SignClick.getConfigManager()));
        builder.registerTypeAdapter(CompanyRef.class, new CompanyRefSerializer());
        builder.registerTypeAdapter(Country.class, new CountrySerializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(Election.class, new ElectionSerializer());
        builder.registerTypeAdapter(Auction.class, new AuctionSerializer());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        builder.registerTypeAdapterFactory(new CompanyOwnerManagerAdapterFactory());
        builder.registerTypeAdapter(Block.class, new BlockSerializer());
        builder.registerTypeAdapter(LicenseSingleton.class, new LicenseSingletonSerializer());
        builder.registerTypeAdapterFactory(pluginLogTypes);
        builder.registerTypeAdapterFactory(companyTypes);
        builder.registerTypeAdapterFactory(patentUpgradeTypes);
        builder.registerTypeAdapterFactory(upgradeTypes);
        builder.registerTypeAdapter(Policy.class, new PolicySerializer());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        builder.registerTypeAdapter(BlockPosKey.class, new BlockPosKeySerializer());

        return builder;
    }

    public static <T> String serialize(T value, Type token){
        Gson gson = getBuilder().create();
        return gson.toJson(value, token);
    }


    public static <T> void writeSave(String name, T value){
        /*
        * Save object inside a json file
        * */

        Gson gson = getBuilder().create();

        File file = new File(SignClick.getPlugin().getDataFolder()+"/"+name+".json");

        try {
            file.getParentFile().mkdir();
            file.createNewFile();

            if (gson.toJson(value).toString().isEmpty()){
                return;
            }

            Writer writer = new FileWriter(file, false);
            gson.toJson(value, writer);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T readSave(String name, Type token, T defaultValue){
        /*
        * Read object from a json file
        * */
        Gson gson = getBuilder().create();

        File file = new File(SignClick.getPlugin().getDataFolder()+"/"+name+".json");

        try {
            file.getParentFile().mkdir();
            file.createNewFile();
            Reader reader = new FileReader(file);

            T value = gson.fromJson(reader, token);
            if (value == null){
                value = defaultValue;
            }

            reader.close();
            return value;
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T deserialize(JsonElement js, Type token, T defaultValue){
        /*
         * Read object from a json file
         * */
        Gson gson = getBuilder().create();

        T value = gson.fromJson(js, token);
        if (value == null){
            value = defaultValue;
        }

        return value;

    }

    public static void setSign(SignChangeEvent sign, String[] content){
        /*
        * update a sign with the provided text
        * */

        assert content.length == 4;

        Sign s = (Sign) sign.getBlock().getState();

        for (int i=0; i<4; i++){
            sign.setLine(i, content[i]);
            s.setLine(i, content[i]);
        }

        s.update();

    }

    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) { // Traverse up the hierarchy
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass(); // Move to the superclass
        }
        return fields.toArray(new Field[0]);
    }

    public static Material getCompanyTypeMaterial(String type){
        Map<String, Material> materialMap = new HashMap<>();
        materialMap.put("Farming", Material.WHEAT);
        materialMap.put("Fisherman", Material.COD);
        materialMap.put("Woodcutter", Material.OAK_LOG);
        materialMap.put("Nature", Material.GRASS_BLOCK);
        materialMap.put("Miscellaneous", Material.LANTERN);
        materialMap.put("Nether", Material.NETHERRACK);
        materialMap.put("End", Material.END_STONE);
        materialMap.put("Mining", Material.STONE);
        materialMap.put("Fighter", Material.IRON_SWORD);
        materialMap.put("Hunter", Material.ROTTEN_FLESH);
        materialMap.put("Enchantment", Material.ENCHANTED_BOOK);
        materialMap.put("Brewery", Material.GLASS_BOTTLE);
        materialMap.put("Redstone", Material.REDSTONE);

        return materialMap.get(type);
    }

    private static <T> int indexOfSubList(
            List<T> source,
            List<T> target,
            BiPredicate<T, T> equals
    ) {
        int sourceSize = source.size();
        int targetSize = target.size();

        if (targetSize == 0) return 0;
        if (targetSize > sourceSize) return -1;

        for (int i = 0; i <= sourceSize - targetSize; i++) {
            boolean match = true;
            for (int j = 0; j < targetSize; j++) {
                if (!equals.test(source.get(i + j), target.get(j))) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }

        return -1;
    }

    public static ItemStack simulateCraft(ItemStack[] inputMatrix) {

        for (@NotNull Iterator<Recipe> it = getServer().recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();

            if (recipe instanceof ShapelessRecipe shapelessRecipe){
                List<ItemStack> array = Arrays.stream(inputMatrix).
                        filter(Objects::nonNull).sorted(Comparator.comparing(s -> s.getType().name())).toList();

                List<ItemStack> ingredients = shapelessRecipe.getIngredientList().stream().
                        sorted(Comparator.comparing(s -> s.getType().name())).toList();

                boolean b = array.size() == ingredients.size()
                        && IntStream.range(0, array.size())
                        .allMatch(i -> array.get(i).getType() == ingredients.get(i).getType());

                if (b){
                    return shapelessRecipe.getResult();
                }
            }

            if (recipe instanceof ShapedRecipe shapedRecipe){

                String s = "";
                for (String row: shapedRecipe.getShape()){
                    String tempRow = row;

                    while (tempRow.length() < 3){
                        tempRow += " ";
                    }

                    s += tempRow;
                }

                /*
                * make my own shape
                * */
                List<ItemStack> ingredients = new ArrayList<>();
                for (char s2: s.toCharArray()){
                    RecipeChoice rc = shapedRecipe.getChoiceMap().get(s2);
                    if (rc == null){
                        ingredients.add(null);
                    }else{
                        ingredients.add(rc.getItemStack());
                    }
                }

                int subsetIndex = indexOfSubList(Arrays.stream(inputMatrix).toList(), ingredients,
                        (a, b) ->  {

                    if (a == null && b == null){
                        return true;
                    }

                    if (a == null || b == null){
                        return false;
                    }

                    return a.getType() == b.getType();
                });

                boolean b = subsetIndex != -1;
                for (int i=0; i<subsetIndex;i++){
                    if (Arrays.stream(inputMatrix).toList().get(i) != null){
                        b = false;
                    }
                }
                for (int i=subsetIndex+ingredients.size(); i<Arrays.stream(inputMatrix).toList().size();i++){
                    if (Arrays.stream(inputMatrix).toList().get(i) != null){
                        b = false;
                    }
                }

                if (b){
                    return shapedRecipe.getResult();
                }
            }

        }

        return null;
    }

    public static double getFurnaceFuelAmount(ItemStack[] inputMatrix) {
        ItemStack toBurn = inputMatrix[0];
        ItemStack fuel = inputMatrix[1];

        /*
         * Not a valid fuel source
         * */
        if (fuel == null || !fuel.getType().isFuel()){
            return -1;
        }

        for (@NotNull Iterator<Recipe> it = getServer().recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();

            if (recipe instanceof FurnaceRecipe furnaceRecipe){
                RecipeChoice input = furnaceRecipe.getInputChoice();

                if (!(input instanceof RecipeChoice.MaterialChoice materialChoice)){
                    continue;
                }

                if (!materialChoice.getChoices().contains(toBurn.getType())){
                    continue;
                }

                int cookTime = furnaceRecipe.getCookingTime();

                return cookTime/ (double) getVanillaBurnTime(fuel.getType());
            }


        }

        return -1;
    }


    public static ItemStack simulateFurnace(ItemStack[] inputMatrix) {
        ItemStack toBurn = inputMatrix[0];
        ItemStack fuel = inputMatrix[1];

        /*
        * Not a valid fuel source
        * */
        if (fuel == null || !fuel.getType().isFuel()){
            return null;
        }

        for (@NotNull Iterator<Recipe> it = getServer().recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();

            if (recipe instanceof FurnaceRecipe furnaceRecipe){
                RecipeChoice input = furnaceRecipe.getInputChoice();

                if (!(input instanceof RecipeChoice.MaterialChoice materialChoice)){
                    continue;
                }

                if (!materialChoice.getChoices().contains(toBurn.getType())){
                    continue;
                }

                return furnaceRecipe.getResult();
            }


        }

        return null;
    }

    public static void AssertMet(boolean value, String reason){
        if(!value){
            throw new AssertionError(reason);
        }

    }

    public static String formatDuration(long totalSeconds) {
        long days = totalSeconds / 86400;          // 1 day = 86400 seconds
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public static int getVanillaBurnTime(Material material) {
        return switch (material) {
            case LAVA_BUCKET -> 20000;
            case COAL_BLOCK -> 16000;
            case DRIED_KELP_BLOCK -> 4000;
            case BLAZE_ROD -> 2400;
            case COAL, CHARCOAL -> 1600;
            // Wood-based logs and planks
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, MANGROVE_LOG,
                    STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG,
                    STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG,
                    OAK_PLANKS, SPRUCE_PLANKS, BIRCH_PLANKS, JUNGLE_PLANKS,
                    ACACIA_PLANKS, DARK_OAK_PLANKS, MANGROVE_PLANKS -> 300;

            // Wooden fixtures
            case OAK_FENCE, SPRUCE_FENCE, BIRCH_FENCE, JUNGLE_FENCE,
                    ACACIA_FENCE, DARK_OAK_FENCE, MANGROVE_FENCE,
                    OAK_FENCE_GATE, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE,
                    JUNGLE_FENCE_GATE, ACACIA_FENCE_GATE, DARK_OAK_FENCE_GATE,
                    MANGROVE_FENCE_GATE,
                    OAK_STAIRS, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS,
                    ACACIA_STAIRS, DARK_OAK_STAIRS, MANGROVE_STAIRS,
                    OAK_TRAPDOOR, SPRUCE_TRAPDOOR, BIRCH_TRAPDOOR,
                    JUNGLE_TRAPDOOR, ACACIA_TRAPDOOR, DARK_OAK_TRAPDOOR,
                    MANGROVE_TRAPDOOR,
                    OAK_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE,
                    BIRCH_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE,
                    ACACIA_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE,
                    MANGROVE_PRESSURE_PLATE,
                    CRAFTING_TABLE -> 300;

            // Wood tools / boats
            case WOODEN_SWORD, WOODEN_SHOVEL, WOODEN_PICKAXE,
                    WOODEN_AXE, WOODEN_HOE -> 200;

            // Smaller fuels
            case STICK, BOWL -> 100;
            case WHITE_CARPET, ORANGE_CARPET, MAGENTA_CARPET, LIGHT_BLUE_CARPET,
                    YELLOW_CARPET, LIME_CARPET, PINK_CARPET, GRAY_CARPET,
                    LIGHT_GRAY_CARPET, CYAN_CARPET, PURPLE_CARPET, BLUE_CARPET,
                    BROWN_CARPET, GREEN_CARPET, RED_CARPET, BLACK_CARPET -> 67;
            case BAMBOO, SCAFFOLDING -> 50;

            default -> 100;
        };
    }


}
