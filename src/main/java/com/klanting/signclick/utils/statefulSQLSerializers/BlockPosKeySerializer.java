package com.klanting.signclick.utils.statefulSQLSerializers;

import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;

import java.util.List;

public class BlockPosKeySerializer extends SQLSerializer<BlockPosKey> {
    public BlockPosKeySerializer(Class<BlockPosKey> type) {
        super(type);
    }

    @Override
    public String serialize(BlockPosKey value) {
        return value.x()+";"+ value.y()+";"+value.z()+";"+value.world();
    }

    @Override
    public BlockPosKey deserialize(String value) {
        String[] s = value.split(";");

        return BlockPosKey.from(s[3], Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
    }
}
