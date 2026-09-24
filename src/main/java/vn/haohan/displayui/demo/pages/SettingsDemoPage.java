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
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.animation.Easings;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.CheckboxComponent;
import vn.haohan.displayui.api.component.SliderComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.container.DropdownAnimationType;
import vn.haohan.displayui.api.container.DropdownContainer;
import vn.haohan.displayui.api.interaction.UiControlChange;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interactive settings presentation page demonstrating {@link DropdownContainer}
 * with nested child containers, dropdown expand/collapse animations, and real-time controls.
 */
public final class SettingsDemoPage extends BaseDemoPage {

    private final Map<UUID, Set<String>> playerExpandedDropdowns = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerSettings> playerSettings = new ConcurrentHashMap<>();

    private static final class PlayerSettings {
        double brightness = 0.80;
        double guiScale = 2.0;
        boolean fancyGraphics = true;
        double masterVolume = 0.70;
        double musicVolume = 0.45;
        boolean ambientSound = true;
        boolean autoJump = false;
        double chatOpacity = 0.90;
    }

    private PlayerSettings getSettings(UUID playerId) {
        return playerSettings.computeIfAbsent(playerId, k -> new PlayerSettings());
    }

    private Set<String> getExpanded(UUID playerId) {
        return playerExpandedDropdowns.computeIfAbsent(playerId, k -> {
            Set<String> set = new HashSet<>();
            set.add("dropdown_graphics"); // Graphics dropdown open by default
            return set;
        });
    }

    @Override
    public String title() {
        return "SETTINGS & DROPDOWNS";
    }

    @Override
    public void build(Container container, DemoContext context) {
        UUID pid = context.playerId();
        PlayerSettings s = getSettings(pid);
        Set<String> expanded = getExpanded(pid);

        float currentY = 0.0f;
        float menuW = 174.0f;

        // 1. Graphics & Display Dropdown (SLIDE_AND_FADE animation)
        boolean graphicsOpen = expanded.contains("dropdown_graphics");
        DropdownContainer graphicsDropdown = DropdownContainer.builder("dropdown_graphics")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, currentY)
                .width(menuW)
                .headerHeight(13.0f)
                .headerTitle(Component.text("⚙  GRAPHICS & DISPLAY", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .headerTitleFontSize(4.2f)
                .headerBackgroundColor(Color.fromRGB(35, 42, 58))
                .headerExpandedBackgroundColor(Color.fromRGB(45, 60, 85))
                .indicatorColor(NamedTextColor.YELLOW)
                .animationType(DropdownAnimationType.SLIDE_AND_FADE)
                .animationDurationTicks(8)
                .animationEasing(Easings.OutCubic)
                .slideDistance(8.0f)
                .contentBackgroundColor(Color.fromRGB(20, 24, 34))
                .contentPadding(2.0f)
                .itemSpacing(2.0f)
                .clampToParent(true)
                .expanded(graphicsOpen)
                .build();

        // Items nested inside Graphics Dropdown
        graphicsDropdown.addDropdownItem(buildSliderItem("item_bright", "BRIGHTNESS",
                "setting_brightness", 0.0, 1.0, s.brightness, 0.05,
                (int) Math.round(s.brightness * 100.0) + "%", Color.fromRGB(255, 200, 60)));

        graphicsDropdown.addDropdownItem(buildCheckboxItem("item_fancy", "Fancy Particle Effects",
                "setting_fancy", s.fancyGraphics));

        container.addContainer(graphicsDropdown);
        currentY += graphicsDropdown.height() + 2.0f;

        // 2. Audio & Sound Dropdown (BOUNCE_OUT animation)
        boolean audioOpen = expanded.contains("dropdown_audio");
        DropdownContainer audioDropdown = DropdownContainer.builder("dropdown_audio")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, currentY)
                .width(menuW)
                .headerHeight(13.0f)
                .headerTitle(Component.text("♪  AUDIO & SOUND", NamedTextColor.AQUA, TextDecoration.BOLD))
                .headerTitleFontSize(4.2f)
                .headerBackgroundColor(Color.fromRGB(28, 45, 55))
                .headerExpandedBackgroundColor(Color.fromRGB(35, 65, 80))
                .indicatorColor(NamedTextColor.AQUA)
                .animationType(DropdownAnimationType.BOUNCE_OUT)
                .animationDurationTicks(10)
                .slideDistance(8.0f)
                .contentBackgroundColor(Color.fromRGB(16, 26, 34))
                .contentPadding(2.0f)
                .itemSpacing(2.0f)
                .clampToParent(true)
                .expanded(audioOpen)
                .build();

        // Items nested inside Audio Dropdown
        audioDropdown.addDropdownItem(buildSliderItem("item_mvol", "MASTER VOLUME",
                "setting_master_vol", 0.0, 1.0, s.masterVolume, 0.05,
                (int) Math.round(s.masterVolume * 100.0) + "%", Color.fromRGB(80, 190, 255)));

        audioDropdown.addDropdownItem(buildSliderItem("item_bgm", "MUSIC & BGM",
                "setting_music_vol", 0.0, 1.0, s.musicVolume, 0.05,
                (int) Math.round(s.musicVolume * 100.0) + "%", Color.fromRGB(150, 120, 255)));

        audioDropdown.addDropdownItem(buildCheckboxItem("item_ambient", "Ambient World Audio",
                "setting_ambient", s.ambientSound));

        container.addContainer(audioDropdown);
        currentY += audioDropdown.height() + 2.0f;

        // 3. Gameplay & Controls Dropdown (SLIDE_DOWN animation)
        boolean gameplayOpen = expanded.contains("dropdown_gameplay");
        DropdownContainer gameplayDropdown = DropdownContainer.builder("dropdown_gameplay")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, currentY)
                .width(menuW)
                .headerHeight(13.0f)
                .headerTitle(Component.text("⚔  GAMEPLAY & CONTROLS", NamedTextColor.GREEN, TextDecoration.BOLD))
                .headerTitleFontSize(4.2f)
                .headerBackgroundColor(Color.fromRGB(25, 48, 38))
                .headerExpandedBackgroundColor(Color.fromRGB(32, 68, 52))
                .indicatorColor(NamedTextColor.GREEN)
                .animationType(DropdownAnimationType.SLIDE_DOWN)
                .animationDurationTicks(8)
                .animationEasing(Easings.OutCubic)
                .slideDistance(8.0f)
                .contentBackgroundColor(Color.fromRGB(16, 28, 24))
                .contentPadding(2.0f)
                .itemSpacing(2.0f)
                .clampToParent(true)
                .expanded(gameplayOpen)
                .build();

        // Items nested inside Gameplay Dropdown
        gameplayDropdown.addDropdownItem(buildCheckboxItem("item_autojump", "Auto-Jump Assistance",
                "setting_autojump", s.autoJump));

        gameplayDropdown.addDropdownItem(buildSliderItem("item_chat", "CHAT OPACITY",
                "setting_chat_opacity", 0.1, 1.0, s.chatOpacity, 0.05,
                (int) Math.round(s.chatOpacity * 100.0) + "%", Color.fromRGB(80, 220, 120)));

        gameplayDropdown.addDropdownItem(buildButtonItem("item_reset_btn", "setting_reset",
                "RESET TO DEFAULTS", Color.fromRGB(120, 40, 40)));

        container.addContainer(gameplayDropdown);
    }

    private Container buildSliderItem(String containerId, String title, String controlId,
                                      double min, double max, double value, double step,
                                      String valueDisplay, Color fillColor) {
        Container row = Container.builder(containerId)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .size(170.0f, 10.0f)
                .backgroundColor(Color.fromRGB(26, 30, 42))
                .borderRound(2.0f)
                .build();

        // Label Left
        row.addComponent(TextComponent.builder(controlId + "_lbl_left")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(3.0f, 1.0f)
                .size(100.0f, 4.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(title, NamedTextColor.GRAY, TextDecoration.BOLD))
                .fontSize(3.2f)
                .build());

        // Value Right
        row.addComponent(TextComponent.builder(controlId + "_lbl_right")
                .anchor(UiAnchorPoint.TOP_RIGHT)
                .origin(UiAnchorPoint.TOP_RIGHT)
                .offset(-3.0f, 1.0f)
                .size(50.0f, 4.0f)
                .alignment(UiTextAlignment.RIGHT)
                .text(Component.text(valueDisplay, NamedTextColor.WHITE))
                .fontSize(3.2f)
                .build());

        // Slider Component
        row.addComponent(SliderComponent.builder(controlId)
                .anchor(UiAnchorPoint.BOTTOM_LEFT)
                .origin(UiAnchorPoint.BOTTOM_LEFT)
                .offset(3.0f, -1.0f)
                .size(164.0f, 4.5f)
                .range(min, max, value)
                .step(step)
                .borderRound(1.5f)
                .trackColor(Color.fromRGB(15, 18, 25))
                .fillColor(fillColor)
                .thumbColor(Color.WHITE)
                .build());

        return row;
    }

    private Container buildCheckboxItem(String containerId, String title, String controlId, boolean checked) {
        Container row = Container.builder(containerId)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .size(170.0f, 9.0f)
                .backgroundColor(Color.fromRGB(26, 30, 42))
                .borderRound(2.0f)
                .build();

        // Checkbox Component
        row.addComponent(CheckboxComponent.builder(controlId)
                .anchor(UiAnchorPoint.CENTER_LEFT)
                .origin(UiAnchorPoint.CENTER_LEFT)
                .offset(3.0f, 0.0f)
                .size(7.0f, 7.0f)
                .checked(checked)
                .activeBoxColor(Color.fromRGB(40, 180, 90))
                .inactiveBoxColor(Color.fromRGB(35, 40, 50))
                .borderRound(1.5f)
                .build());

        // Label
        row.addComponent(TextComponent.builder(controlId + "_lbl")
                .anchor(UiAnchorPoint.CENTER_LEFT)
                .origin(UiAnchorPoint.CENTER_LEFT)
                .offset(14.0f, 0.0f)
                .size(150.0f, 5.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(Component.text(title, checked ? NamedTextColor.WHITE : NamedTextColor.GRAY))
                .fontSize(3.4f)
                .build());

        return row;
    }

    private Container buildButtonItem(String containerId, String buttonId, String label, Color bgColor) {
        Container row = Container.builder(containerId)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .size(170.0f, 9.0f)
                .backgroundColor(Color.fromRGB(26, 30, 42))
                .borderRound(2.0f)
                .build();

        row.addComponent(ButtonComponent.builder(buttonId)
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(164.0f, 7.0f)
                .label(label)
                .backgroundColor(bgColor)
                .borderRound(1.5f)
                .build());

        return row;
    }

    @Override
    public boolean onClick(DemoContext context, String buttonId, Player player) {
        UUID pid = context.playerId();
        Set<String> expanded = getExpanded(pid);

        // Check if any of the dropdown toggle buttons was clicked
        if (buttonId.startsWith("dropdown_") && buttonId.endsWith("_toggle")) {
            String dropdownId = buttonId.substring(0, buttonId.length() - "_toggle".length());
            if (expanded.contains(dropdownId)) {
                expanded.remove(dropdownId);
                player.sendMessage("§7Folded dropdown §e" + dropdownId);
            } else {
                // Accordion behavior: close others so dropdown does not exceed container frame
                expanded.clear();
                expanded.add(dropdownId);
                player.sendMessage("§aPushed out child containers in §e" + dropdownId);
            }
            context.updateView();
            return true;
        }

        if ("setting_reset".equals(buttonId)) {
            playerSettings.put(pid, new PlayerSettings());
            player.sendMessage("§e⚙ Settings have been restored to defaults!");
            context.updateView();
            return true;
        }

        return false;
    }

    @Override
    public void onControlChange(DemoContext context, UiControlChange change) {
        UUID pid = context.playerId();
        PlayerSettings s = getSettings(pid);
        String id = change.control().id();

        switch (id) {
            case "setting_brightness" -> s.brightness = change.value();
            case "setting_fancy" -> s.fancyGraphics = change.checked();
            case "setting_master_vol" -> s.masterVolume = change.value();
            case "setting_music_vol" -> s.musicVolume = change.value();
            case "setting_ambient" -> s.ambientSound = change.checked();
            case "setting_autojump" -> s.autoJump = change.checked();
            case "setting_chat_opacity" -> s.chatOpacity = change.value();
            default -> { return; }
        }

        context.updateView();
    }
}
