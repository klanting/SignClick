package tools;

import com.earth2me.essentials.*;
import com.earth2me.essentials.api.IItemDb;
import com.earth2me.essentials.api.IJails;
import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.PlayerNotFoundException;
import com.earth2me.essentials.items.CustomItemResolver;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.updatecheck.UpdateChecker;
import com.earth2me.essentials.userstorage.IUserMap;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.ess3.api.IEssentials;
import net.ess3.nms.refl.providers.ReflOnlineModeProvider;
import net.ess3.provider.*;
import net.essentialsx.api.v2.services.BalanceTop;
import net.essentialsx.api.v2.services.mail.MailService;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class MockEssentials implements IEssentials{

    @Override
    public Collection<String> getVanishedPlayersNew() {
        return List.of();
    }

    @Override
    public SpawnEggProvider getSpawnEggProvider() {
        return null;
    }

    @Override
    public PotionMetaProvider getPotionMetaProvider() {
        return null;
    }

    @Override
    public CustomItemResolver getCustomItemResolver() {
        return null;
    }

    @Override
    public void addReloadListener(IConf listener) {

    }

    @Override
    public void reload() {

    }

    @Override
    public Map<String, IEssentialsCommand> getCommandMap() {
        return Map.of();
    }

    @Override
    public List<String> onTabCompleteEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix, IEssentialsModule module) {
        return List.of();
    }

    @Override
    public boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix, IEssentialsModule module) {
        return false;
    }

    @Override
    public User getUser(Object base) {
        return null;
    }

    @Override
    public User getUser(UUID base) {
        return null;
    }

    @Override
    public User getUser(String base) {
        return null;
    }

    @Override
    public User getUser(Player base) {
        return null;
    }

    @Override
    public User matchUser(Server server, User sourceUser, String searchTerm, Boolean getHidden, boolean getOffline) throws PlayerNotFoundException {
        return null;
    }

    @Override
    public boolean canInteractWith(CommandSource interactor, User interactee) {
        return false;
    }

    @Override
    public boolean canInteractWith(User interactor, User interactee) {
        return false;
    }

    @Override
    public I18n getI18n() {
        return null;
    }

    @Override
    public User getOfflineUser(String name) {
        return null;
    }

    @Override
    public World getWorld(String name) {
        return null;
    }

    @Override
    public int broadcastMessage(String message) {
        return 0;
    }

    @Override
    public int broadcastMessage(IUser sender, String message) {
        return 0;
    }

    @Override
    public int broadcastMessage(IUser sender, String message, Predicate<IUser> shouldExclude) {
        return 0;
    }

    @Override
    public int broadcastMessage(String permission, String message) {
        return 0;
    }

    @Override
    public ISettings getSettings() {
        return null;
    }

    @Override
    public BukkitScheduler getScheduler() {
        return null;
    }

    @Override
    public IJails getJails() {
        return null;
    }

    @Override
    public IWarps getWarps() {
        return null;
    }

    @Override
    public Worth getWorth() {
        return null;
    }

    @Override
    public Backup getBackup() {
        return null;
    }

    @Override
    public Kits getKits() {
        return null;
    }

    @Override
    public RandomTeleport getRandomTeleport() {
        return null;
    }

    @Override
    public UpdateChecker getUpdateChecker() {
        return null;
    }

    @Override
    public BukkitTask runTaskAsynchronously(Runnable run) {
        return null;
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Runnable run, long delay) {
        return null;
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Runnable run, long delay, long period) {
        return null;
    }

    @Override
    public int scheduleSyncDelayedTask(Runnable run) {
        return 0;
    }

    @Override
    public int scheduleSyncDelayedTask(Runnable run, long delay) {
        return 0;
    }

    @Override
    public int scheduleSyncRepeatingTask(Runnable run, long delay, long period) {
        return 0;
    }

    @Override
    public PermissionsHandler getPermissionsHandler() {
        return null;
    }

    @Override
    public AlternativeCommandsHandler getAlternativeCommandsHandler() {
        return null;
    }

    @Override
    public void showError(CommandSource sender, Throwable exception, String commandLabel) {

    }

    @Override
    public IItemDb getItemDb() {
        return null;
    }

    @Override
    public IUserMap getUsers() {
        return null;
    }

    @Override
    public UserMap getUserMap() {
        return null;
    }

    @Override
    public BalanceTop getBalanceTop() {
        return null;
    }

    @Override
    public EssentialsTimer getTimer() {
        return null;
    }

    @Override
    public MailService getMail() {
        return null;
    }

    @Override
    public List<String> getVanishedPlayers() {
        return List.of();
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return List.of();
    }

    @Override
    public Iterable<User> getOnlineUsers() {
        return null;
    }

    @Override
    public SpawnerItemProvider getSpawnerItemProvider() {
        return null;
    }

    @Override
    public SpawnerBlockProvider getSpawnerBlockProvider() {
        return null;
    }

    @Override
    public ServerStateProvider getServerStateProvider() {
        return null;
    }

    @Override
    public MaterialTagProvider getMaterialTagProvider() {
        return null;
    }

    @Override
    public ContainerProvider getContainerProvider() {
        return null;
    }

    @Override
    public KnownCommandsProvider getKnownCommandsProvider() {
        return null;
    }

    @Override
    public SerializationProvider getSerializationProvider() {
        return null;
    }

    @Override
    public FormattedCommandAliasProvider getFormattedCommandAliasProvider() {
        return null;
    }

    @Override
    public SyncCommandsProvider getSyncCommandsProvider() {
        return null;
    }

    @Override
    public PersistentDataProvider getPersistentDataProvider() {
        return null;
    }

    @Override
    public ReflOnlineModeProvider getOnlineModeProvider() {
        return null;
    }

    @Override
    public ItemUnbreakableProvider getItemUnbreakableProvider() {
        return null;
    }

    @Override
    public WorldInfoProvider getWorldInfoProvider() {
        return null;
    }

    @Override
    public SignDataProvider getSignDataProvider() {
        return null;
    }

    @Override
    public PluginCommand getPluginCommand(String cmd) {
        return null;
    }

    @Override
    public @NotNull File getDataFolder() {
        return null;
    }

    @Override
    public @NotNull PluginDescriptionFile getDescription() {
        return null;
    }

    @Override
    public @NotNull PluginMeta getPluginMeta() {
        return null;
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return null;
    }

    @Override
    public @Nullable InputStream getResource(@NotNull String s) {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(@NotNull String s, boolean b) {

    }

    @Override
    public void reloadConfig() {

    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull PluginLoader getPluginLoader() {
        return null;
    }

    @Override
    public @NotNull Server getServer() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean b) {

    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String s, @Nullable String s1) {
        return null;
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String s, @Nullable String s1) {
        return null;
    }

    @Override
    public @NotNull Logger getLogger() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return "";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
