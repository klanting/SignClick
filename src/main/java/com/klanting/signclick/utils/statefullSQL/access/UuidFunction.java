package com.klanting.signclick.utils.statefullSQL.access;

import java.sql.SQLException;
import java.util.UUID;

@FunctionalInterface
public interface UuidFunction {
    void apply(UUID uuid) throws SQLException;
}
