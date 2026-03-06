package com.klanting.signclick.utils.statefulSQLSerializers;

import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;
import org.bukkit.ChatColor;

public class ChatColorSerializer extends SQLSerializer<ChatColor> {
    public ChatColorSerializer(Class<ChatColor> type) {
        super(type);
    }

    @Override
    public String serialize(ChatColor value) {
        return value.toString();
    }

    @Override
    public ChatColor deserialize(String value) {
        return ChatColor.valueOf(value);
    }
}
