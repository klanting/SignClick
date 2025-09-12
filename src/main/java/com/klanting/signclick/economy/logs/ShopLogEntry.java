package com.klanting.signclick.economy.logs;

import org.bukkit.Material;

import java.util.UUID;

public record ShopLogEntry(Material item, UUID issuer, int amount, double price) {}
