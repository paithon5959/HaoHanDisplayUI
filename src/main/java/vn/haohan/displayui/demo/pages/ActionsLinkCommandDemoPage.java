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
import vn.haohan.displayui.api.interaction.UiButtonAction;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class ActionsLinkCommandDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "LINK + COMMAND ACTIONS";
    }

    @Override
    public void build(Container container, DemoContext context) {
        addActionRow(container, "open_url", "Open Documentation Link", 0.0f,
                NamedTextColor.AQUA, Material.BOOK, "Opens wiki in client chat link prompt",
                UiButtonAction.openUrl("https://github.com/Hao-Han-SMP/HaoHanDisplayUI"));

        addActionRow(container, "player_command", "Run /help as Player", 19.0f,
                NamedTextColor.GREEN, Material.COMMAND_BLOCK, "Dispatches a command as the clicking player",
                UiButtonAction.playerCommand("/help"));

        addActionRow(container, "console_command", "Run Console Command", 38.0f,
                NamedTextColor.GOLD, Material.REPEATING_COMMAND_BLOCK, "Runs a privileged command from console",
                UiButtonAction.consoleCommand("say Player interacted with DisplayUI demo"));

        addActionRow(container, "execute_command", "Run /say as UI Action", 57.0f,
                NamedTextColor.LIGHT_PURPLE, Material.CHAIN_COMMAND_BLOCK, "Directly executes command template",
                UiButtonAction.executeCommand("/say Hello from DisplayUI actions"));
    }

    private void addActionRow(Container container, String id, String title,
                              float y, NamedTextColor accent, Material icon,
                              String description, UiButtonAction action) {
        Container row = Container.builder(id + "_container")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, y)
                .size(174.0f, 16.0f)
                .backgroundColor(Color.fromRGB(25, 30, 42))
                .borderRound(3.0f)
                .build();

        row.addComponent(IconComponent.builder(id + "_icon")
                .anchor(UiAnchorPoint.CENTER_LEFT)
                .origin(UiAnchorPoint.CENTER_LEFT)
                .offset(4.0f, 0.0f)
                .size(12.0f, 12.0f)
                .material(icon)
                .build());

        row.addComponent(TextComponent.builder(id + "_title")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(20.0f, 2.0f)
                .size(110.0f, 6.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(title, accent, TextDecoration.BOLD))
                .fontSize(4.8f)
                .build());

        row.addComponent(TextComponent.builder(id + "_desc")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(20.0f, 8.5f)
                .size(110.0f, 6.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(description, NamedTextColor.GRAY))
                .fontSize(3.5f)
                .build());

        row.addComponent(ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.CENTER_RIGHT)
                .origin(UiAnchorPoint.CENTER_RIGHT)
                .offset(-4.0f, 0.0f)
                .size(36.0f, 12.0f)
                .label("EXEC")
                .action(action)
                .borderRound(2.0f)
                .build());

        container.addContainer(row);
    }
}
