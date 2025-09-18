package com.klanting.signclick.logicLayer.companyLogic.logs;

import org.bukkit.Material;

import java.util.UUID;

public record itemLogEntry(Material item, UUID issuer, int amount, double price) {}
