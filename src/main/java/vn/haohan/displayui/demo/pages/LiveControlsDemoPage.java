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
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.CheckboxComponent;
import vn.haohan.displayui.api.component.SliderComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.interaction.UiControlChange;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class LiveControlsDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "LIVE CONTROLS";
    }

    @Override
    public void build(Container container, DemoContext context) {
        // 1. Master Volume Text Labels
        container.addComponent(TextComponent.builder("vol_label_left")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 0.0f)
                .size(100.0f, 10.0f)
                .text(Component.text("MASTER VOLUME", NamedTextColor.WHITE, TextDecoration.BOLD))
                .fontSize(5.0f)
                .build());

        container.addComponent(TextComponent.builder("vol_label_right")
                .anchor(UiAnchorPoint.TOP_RIGHT)
                .origin(UiAnchorPoint.TOP_RIGHT)
                .offset(0.0f, 0.0f)
                .size(70.0f, 10.0f)
                .alignment(UiTextAlignment.RIGHT)
                .text(Component.text((int) Math.round(context.volume() * 100.0) + "%",
                        NamedTextColor.YELLOW, TextDecoration.BOLD))
                .fontSize(5.0f)
                .build());

        // 2. Master Volume Slider Component
        container.addComponent(SliderComponent.builder("demo_volume")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 11.0f)
                .size(174.0f, 12.0f)
                .range(0.0, 1.0, context.volume())
                .step(0.05)
                .borderRound(4.0f)
                .trackColor(Color.fromRGB(20, 20, 25))
                .fillColor(Color.fromRGB(80, 180, 255))
                .thumbColor(Color.WHITE)
                .build());

        // 3. Particle Effects Labels
        container.addComponent(TextComponent.builder("particle_label_left")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 29.0f)
                .size(100.0f, 10.0f)
                .text(Component.text("PARTICLE EFFECTS", NamedTextColor.WHITE, TextDecoration.BOLD))
                .fontSize(5.0f)
                .build());

        container.addComponent(TextComponent.builder("particle_label_right")
                .anchor(UiAnchorPoint.TOP_RIGHT)
                .origin(UiAnchorPoint.TOP_RIGHT)
                .offset(0.0f, 29.0f)
                .size(70.0f, 10.0f)
                .alignment(UiTextAlignment.RIGHT)
                .text(Component.text(context.enabled() ? "ENABLED" : "DISABLED",
                        context.enabled() ? NamedTextColor.GREEN : NamedTextColor.RED,
                        TextDecoration.BOLD))
                .fontSize(5.0f)
                .build());

        // 4. Checkbox Component
        container.addComponent(CheckboxComponent.builder("demo_enabled")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 40.0f)
                .size(16.0f, 16.0f)
                .checked(context.enabled())
                .activeBoxColor(Color.fromRGB(50, 200, 100))
                .inactiveBoxColor(Color.fromRGB(40, 45, 55))
                .borderRound(3.0f)
                .build());

        container.addComponent(TextComponent.builder("checkbox_hint")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(24.0f, 43.0f)
                .size(148.0f, 10.0f)
                .text(Component.text("Right-click checkbox or drag slider to mutate state live.", NamedTextColor.GRAY))
                .fontSize(4.0f)
                .build());

        // 5. Buttons Row
        container.addComponent(ButtonComponent.builder("vol_down")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 62.0f)
                .size(54.0f, 14.0f)
                .label("-10%")
                .borderRound(4.0f)
                .build());

        container.addComponent(ButtonComponent.builder("vol_up")
                .anchor(UiAnchorPoint.CENTER_TOP)
                .origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 62.0f)
                .size(54.0f, 14.0f)
                .label("+10%")
                .borderRound(4.0f)
                .build());

        container.addComponent(ButtonComponent.builder("toggle_btn")
                .anchor(UiAnchorPoint.TOP_RIGHT)
                .origin(UiAnchorPoint.TOP_RIGHT)
                .offset(0.0f, 62.0f)
                .size(54.0f, 14.0f)
                .label("TOGGLE")
                .borderRound(4.0f)
                .build());
    }

    @Override
    public boolean onClick(DemoContext context, String buttonId, Player player) {
        switch (buttonId) {
            case "vol_down" -> {
                context.volume(Math.max(0.0, Math.round((context.volume() - 0.1) * 100.0) / 100.0));
                context.updateView();
                return true;
            }
            case "vol_up" -> {
                context.volume(Math.min(1.0, Math.round((context.volume() + 0.1) * 100.0) / 100.0));
                context.updateView();
                return true;
            }
            case "toggle_btn" -> {
                context.enabled(!context.enabled());
                context.updateView();
                return true;
            }
            default -> { return false; }
        }
    }

    @Override
    public void onControlChange(DemoContext context, UiControlChange change) {
        if ("demo_volume".equals(change.control().id())) {
            context.volume(change.value());
            context.updateView();
        } else if ("demo_enabled".equals(change.control().id())) {
            context.enabled(change.checked());
            context.updateView();
        }
    }
}
