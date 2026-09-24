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
import vn.haohan.displayui.api.UiHandle;
import vn.haohan.displayui.api.animation.Easings;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.IconComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

/**
 * Animation laboratory demonstrating smooth transitions with modern Container cards.
 */
public final class PresetEffectGalleryDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "SMOOTH ANIMATION LAB";
    }

    @Override
    public void build(Container container, DemoContext context) {
        container.addComponent(TextComponent.builder("anim_sub")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 0.0f)
                .size(174.0f, 6.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("Each test moves the complete scene from A to B with one shared progress curve.", NamedTextColor.GRAY))
                .fontSize(3.8f)
                .build());

        float cardW = 54.0f;
        float cardH = 32.0f;
        float[] colsX = {0.0f, 60.0f, 120.0f};
        float[] rowsY = {9.0f, 44.0f};

        addPresetCard(container, "anim_card_slide", colsX[0], rowsY[0], cardW, cardH, Material.ARROW,
                "Smooth Slide", "Cubic-out slide");
        addPresetCard(container, "anim_card_scale", colsX[1], rowsY[0], cardW, cardH, Material.SLIME_BALL,
                "Smooth Scale", "Scale bounce");
        addPresetCard(container, "anim_card_depth", colsX[2], rowsY[0], cardW, cardH, Material.ENDER_EYE,
                "Depth Travel", "Z-axis travel");

        addPresetCard(container, "anim_card_fade", colsX[0], rowsY[1], cardW, cardH, Material.GLOWSTONE_DUST,
                "Clean Fade", "Opacity only");
        addPresetCard(container, "anim_card_float", colsX[1], rowsY[1], cardW, cardH, Material.AMETHYST_SHARD,
                "Float", "Small Y/Z drift");
        addPresetCard(container, "anim_card_linear", colsX[2], rowsY[1], cardW, cardH, Material.FEATHER,
                "Linear A → B", "Constant speed");
    }

    private void addPresetCard(Container container, String id, float x, float y,
                               float w, float h, Material icon, String title, String subtitle) {
        Container card = Container.builder(id + "_card")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(x, y)
                .size(w, h)
                .backgroundColor(Color.fromRGB(25, 30, 42))
                .borderRound(4.0f)
                .build();

        // Icon
        card.addComponent(IconComponent.builder(id + "_icon")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(4.0f, 4.0f)
                .size(14.0f, 14.0f)
                .material(icon)
                .build());

        // Title
        card.addComponent(TextComponent.builder(id + "_title")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(20.0f, 4.0f)
                .size(w - 24.0f, 7.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(title, NamedTextColor.AQUA, TextDecoration.BOLD))
                .fontSize(3.8f)
                .shadow(true)
                .build());

        // Subtitle
        card.addComponent(TextComponent.builder(id + "_sub")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(4.0f, 18.0f)
                .size(w - 8.0f, 5.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(subtitle, NamedTextColor.GRAY))
                .fontSize(3.0f)
                .build());

        // Button
        card.addComponent(ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -2.0f)
                .size(w - 8.0f, 7.0f)
                .label("RUN")
                .borderRound(2.0f)
                .build());

        container.addContainer(card);
    }

    @Override
    public void onShow(DemoContext context) {
        playPresetAnimation(context, "anim_card_fade");
    }

    @Override
    public boolean onClick(DemoContext context, String buttonId, Player player) {
        if (!buttonId.startsWith("anim_card_")) return false;
        playPresetAnimation(context, buttonId);
        return true;
    }

    private void playPresetAnimation(DemoContext context, String buttonId) {
        UiHandle handle = context.handle();
        if (handle == null || !handle.isValid()) return;
        handle.animate(animationFor(buttonId));
    }

    private UiAnimation animationFor(String buttonId) {
        return switch (buttonId) {
            case "anim_card_slide" -> UiAnimation.builder().durationTicks(18)
                    .easing(Easings.OutCubic).opacity(0.0f, 1.0f)
                    .offset(UiAnimation.Direction.LEFT, 28.0f).build();
            case "anim_card_scale" -> UiAnimation.builder().durationTicks(18)
                    .easing(Easings.OutCubic).opacity(0.0f, 1.0f)
                    .scale(0.76f, 1.0f).build();
            case "anim_card_depth" -> UiAnimation.builder().durationTicks(20)
                    .easing(Easings.OutCubic).opacity(0.0f, 1.0f)
                    .offset(UiAnimation.Direction.FRONT, 0.55f).build();
            case "anim_card_float" -> UiAnimation.builder().durationTicks(20)
                    .easing(Easings.OutCubic).opacity(0.0f, 1.0f)
                    .offset(0.0f, 12.0f, 0.12f).build();
            case "anim_card_linear" -> UiAnimation.builder().durationTicks(20)
                    .easing(Easings.Linear).opacity(0.0f, 1.0f)
                    .offset(UiAnimation.Direction.RIGHT, 24.0f).build();
            default -> UiAnimation.fadeIn(16, Easings.OutCubic);
        };
    }
}
