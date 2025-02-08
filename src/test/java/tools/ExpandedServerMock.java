package tools;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpandedServerMock extends ServerMock {
    // Override methods to add custom behavior

    @Override
    public int getCurrentTick() {
        return (int) this.getScheduler().getCurrentTick();
    }

    @Override
    public @NotNull PlayerMock addPlayer(){
        return addPlayer(false);
    }

    public @NotNull PlayerMock addPlayer(boolean op){
        PlayerMock pm = super.addPlayer();

        Plugin plugin = super.getPluginManager().getPlugin("SignClick");
        assert plugin != null;

        InputStream in = SignClick.getPlugin().getResource("plugin.yml");
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(in);

        Map<String, Map<String, Object>> permissions = (Map<String, Map<String, Object>>) data.get("permissions");
        List<String> defaultPermsList = new ArrayList<>();
        loadPerms(defaultPermsList, permissions, op);
        for (String perm: defaultPermsList){
            pm.addAttachment(plugin, perm, true);
        }

        return pm;
    }

    private static void loadPerms(List<String> defaultPermsList, Map<String, Map<String, Object>> permissionOptions, boolean isDefault){

        for (Map.Entry<String, Map<String, Object>> entry: permissionOptions.entrySet()){
            if (! (entry.getValue() instanceof Map<String, Object>)){
                continue;
            }

            Map<String, Object> param = entry.getValue();
            boolean store = isDefault;
            if (param.containsKey("default")){
                if (!param.get("default").equals("op") && !param.get("default").toString().equals("false")){
                    store = true;
                }

            }

            if (store){
                defaultPermsList.add(entry.getKey());
            }

            if (param.containsKey("children")){
                loadPerms(defaultPermsList, (Map<String, Map<String, Object>>) param.get("children"), store);
            }
        }
    }


}
