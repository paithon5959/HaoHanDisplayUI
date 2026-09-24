/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 *
 * HaoHanDisplayUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package vn.haohan.displayui.demo.pages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.IconComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class ListLayoutsDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "LIST LAYOUTS";
    }

    @Override
    public void build(Container container, DemoContext context) {
        addListRow(container, "row_emerald", "Emerald Reward", 0.0f,
                NamedTextColor.GREEN, Material.EMERALD, "Daily quest claim");
        addListRow(container, "row_diamond", "Diamond Gear", 19.0f,
                NamedTextColor.AQUA, Material.DIAMOND_SWORD, "PvP loadout preset");
        addListRow(container, "row_gold", "Gold Coins", 38.0f,
                NamedTextColor.GOLD, Material.GOLD_INGOT, "Shop balance · 1,420g");
        addListRow(container, "row_netherite", "Netherite Ingot", 57.0f,
                NamedTextColor.DARK_PURPLE, Material.NETHERITE_INGOT, "Mastery crafting material");
    }

    private void addListRow(Container container, String id, String title,
                            float y, NamedTextColor accent, Material icon,
                            String description) {
        Container row = Container.builder(id + "_container")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, y)
                .size(174.0f, 16.0f)
                .backgroundColor(Color.fromRGB(25, 30, 42))
                .borderRound(3.0f)
                .build();

        // Icon
        row.addComponent(IconComponent.builder(id + "_icon")
                .anchor(UiAnchorPoint.CENTER_LEFT)
                .origin(UiAnchorPoint.CENTER_LEFT)
                .offset(4.0f, 0.0f)
                .size(12.0f, 12.0f)
                .material(icon)
                .build());

        // Title
        row.addComponent(TextComponent.builder(id + "_title")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(20.0f, 2.0f)
                .size(110.0f, 6.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(title, accent, TextDecoration.BOLD))
                .fontSize(4.8f)
                .shadow(true)
                .build());

        // Description
        row.addComponent(TextComponent.builder(id + "_desc")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(20.0f, 8.5f)
                .size(110.0f, 6.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(description, NamedTextColor.GRAY))
                .fontSize(3.5f)
                .build());

        // Action Button on the right
        row.addComponent(ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.CENTER_RIGHT)
                .origin(UiAnchorPoint.CENTER_RIGHT)
                .offset(-4.0f, 0.0f)
                .size(36.0f, 12.0f)
                .label("CLAIM")
                .borderRound(2.0f)
                .build());

        container.addContainer(row);
    }
}
