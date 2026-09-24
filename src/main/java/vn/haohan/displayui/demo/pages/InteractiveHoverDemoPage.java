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
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.IconComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class InteractiveHoverDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "INTERACTION + HOVER";
    }

    @Override
    public void build(Container container, DemoContext context) {
        container.addComponent(TextComponent.builder("hover_subtitle")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 0.0f)
                .size(174.0f, 8.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("Aim at any button to reveal its description overlay.", NamedTextColor.GRAY))
                .fontSize(4.0f)
                .build());

        addActionButton(container, "diamond_action", 0.0f, 14.0f,
                Material.DIAMOND, "Diamond Action", NamedTextColor.AQUA, "Right-click to claim diamonds");

        addActionButton(container, "gold_action", 90.0f, 14.0f,
                Material.GOLD_INGOT, "Gold Action", NamedTextColor.GOLD, "Right-click to spend gold");

        addActionButton(container, "emerald_action", 0.0f, 44.0f,
                Material.EMERALD, "Emerald Action", NamedTextColor.GREEN, "Right-click to trade emeralds");

        addActionButton(container, "custom_action", 90.0f, 44.0f,
                Material.NETHER_STAR, "Special Action", NamedTextColor.LIGHT_PURPLE, "Right-click for custom event");
    }

    @Override
    public boolean onClick(DemoContext context, String buttonId, Player player) {
        return switch (buttonId) {
            case "diamond_action" -> {
                player.sendMessage("§bDiamond action clicked.");
                yield true;
            }
            case "gold_action" -> {
                player.sendMessage("§6Gold action clicked.");
                yield true;
            }
            case "emerald_action" -> {
                player.sendMessage("§aEmerald action clicked.");
                yield true;
            }
            case "custom_action" -> {
                player.sendMessage("§dCustom special action clicked.");
                yield true;
            }
            default -> false;
        };
    }

    private void addActionButton(Container container, String id, float x, float y,
                                 Material icon, String label, NamedTextColor color, String description) {
        Container card = Container.builder(id + "_card")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(x, y)
                .size(84.0f, 26.0f)
                .backgroundColor(Color.fromRGB(28, 34, 46))
                .borderRound(4.0f)
                .build();

        card.addComponent(IconComponent.builder(id + "_icon")
                .anchor(UiAnchorPoint.CENTER_LEFT)
                .origin(UiAnchorPoint.CENTER_LEFT)
                .offset(6.0f, 0.0f)
                .size(16.0f, 16.0f)
                .material(icon)
                .build());

        card.addComponent(TextComponent.builder(id + "_text")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(26.0f, 4.0f)
                .size(54.0f, 8.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(label, color, TextDecoration.BOLD))
                .fontSize(4.5f)
                .shadow(true)
                .build());

        card.addComponent(ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(26.0f, 13.0f)
                .size(52.0f, 9.0f)
                .label("INTERACT")
                .borderRound(2.0f)
                .build());

        container.addContainer(card);
    }
}
