package com.klanting.signclick.utils;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.CompanyRef;
import com.klanting.signclick.logicLayer.companyLogic.LicenseSingleton;
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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.*;
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

    public static <T> String serialize(T value, Type token){
        GsonBuilder builder = new GsonBuilder();
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
        Gson gson = builder.create();
        return gson.toJson(value, token);
    }


    public static <T> void writeSave(String name, T value){
        /*
        * Save object inside a json file
        * */

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
        Gson gson = builder.create();

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
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Company.class, new CompanySerializer(SignClick.getConfigManager()));
        builder.registerTypeAdapter(CompanyRef.class, new CompanyRefSerializer());
        builder.registerTypeAdapter(Country.class, new CountrySerializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(UUID.class, new UUIDDeserializer());
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
        Gson gson = builder.create();

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
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Company.class, new CompanySerializer(SignClick.getConfigManager()));
        builder.registerTypeAdapter(CompanyRef.class, new CompanyRefSerializer());
        builder.registerTypeAdapter(Country.class, new CountrySerializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(UUID.class, new UUIDDeserializer());
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
        Gson gson = builder.create();

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
        materialMap.put("Farmer", Material.WHEAT);
        materialMap.put("Fisherman", Material.COD);
        materialMap.put("Woodcutter", Material.OAK_LOG);
        materialMap.put("Building", Material.BRICKS);
        materialMap.put("Decoration", Material.LANTERN);
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

}
