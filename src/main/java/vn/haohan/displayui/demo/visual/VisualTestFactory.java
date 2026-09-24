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
package vn.haohan.displayui.demo.visual;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import vn.haohan.displayui.api.animation.Easings;
import vn.haohan.displayui.api.component.*;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.container.DropdownAnimationType;
import vn.haohan.displayui.api.container.DropdownContainer;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiText;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.api.text.UiTextOpticalPreset;

import java.util.List;

/**
 * Factory that creates isolated visual test scenes for each individual component and container.
 */
public final class VisualTestFactory {

    public static final List<String> TEST_TYPES = List.of(
            "button", "slider", "checkbox", "text", "shape", "icon", "container", "dropdown"
    );

    private VisualTestFactory() {}

    public static LayerManager createTest(String type) {
        return switch (type.toLowerCase()) {
            case "button" -> createButtonTest();
            case "slider" -> createSliderTest();
            case "checkbox" -> createCheckboxTest();
            case "text" -> createTextTest();
            case "shape" -> createShapeTest();
            case "icon" -> createIconTest();
            case "container" -> createContainerTest();
            case "dropdown" -> createDropdownTest();
            default -> null;
        };
    }

    private static Container createBaseCard(String title, String subtitle, float width, float height) {
        Container root = Container.builder("test_root")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(width, height)
                .backgroundColor(Color.fromARGB(240, 18, 22, 32))
                .borderRound(5.0f)
                .build();

        // Header Title
        root.addComponent(TextComponent.builder("test_title")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(8.0f, 6.0f)
                .size(width - 16.0f, 10.0f)
                .text(Component.text(title, NamedTextColor.GOLD, TextDecoration.BOLD))
                .fontSize(5.5f)
                .shadow(true)
                .build());

        // Subtitle
        root.addComponent(TextComponent.builder("test_subtitle")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(8.0f, 16.0f)
                .size(width - 16.0f, 8.0f)
                .text(Component.text(subtitle, NamedTextColor.GRAY))
                .fontSize(3.8f)
                .build());

        return root;
    }

    // 1. ButtonComponent Visual Test
    public static LayerManager createButtonTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: BUTTON COMPONENT", "Right-click buttons to verify click detection & styling", 180.0f, 110.0f);

        // Standard Button
        root.addComponent(ButtonComponent.builder("test_btn_standard")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 30.0f)
                .size(75.0f, 15.0f)
                .label("Standard Button")
                .backgroundColor(Color.fromRGB(45, 55, 75))
                .borderRound(3.0f)
                .build());

        // Outlined Success Button
        root.addComponent(ButtonComponent.builder("test_btn_success")
                .anchor(UiAnchorPoint.TOP_RIGHT)
                .origin(UiAnchorPoint.TOP_RIGHT)
                .offset(-10.0f, 30.0f)
                .size(75.0f, 15.0f)
                .label("Outlined Green")
                .backgroundColor(Color.fromRGB(30, 90, 50))
                .outline(true)
                .outlineColor(Color.fromRGB(80, 220, 120))
                .outlineThickness(1.5f)
                .borderRound(4.0f)
                .build());

        // Danger Button
        root.addComponent(ButtonComponent.builder("test_btn_danger")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 52.0f)
                .size(75.0f, 15.0f)
                .label("Danger Action")
                .backgroundColor(Color.fromRGB(160, 40, 40))
                .borderRound(3.0f)
                .build());

        // Rounded Pill Button with HitSlop
        root.addComponent(ButtonComponent.builder("test_btn_pill")
                .anchor(UiAnchorPoint.TOP_RIGHT)
                .origin(UiAnchorPoint.TOP_RIGHT)
                .offset(-10.0f, 52.0f)
                .size(75.0f, 15.0f)
                .label("Pill (HitSlop 4px)")
                .backgroundColor(Color.fromRGB(110, 60, 160))
                .borderRound(7.5f)
                .hitSlop(4.0f)
                .build());

        // Full Width Action Button
        root.addComponent(ButtonComponent.builder("test_btn_full")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -10.0f)
                .size(160.0f, 16.0f)
                .label("Full Width Interactive Banner")
                .backgroundColor(Color.fromRGB(210, 140, 20))
                .borderRound(3.0f)
                .build());

        layer.addContainer(root);
        return lm;
    }

    // 2. SliderComponent Visual Test
    public static LayerManager createSliderTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: SLIDER COMPONENT", "Drag or click along track to verify live value change", 180.0f, 115.0f);

        // Continuous Slider (0.0 to 1.0, step 0.01)
        root.addComponent(TextComponent.builder("lbl_slider1")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 28.0f).size(160.0f, 6.0f)
                .text(Component.text("Continuous Slider (Range: 0.0 - 1.0, Default: 0.65)", NamedTextColor.WHITE)).fontSize(3.8f).build());

        root.addComponent(SliderComponent.builder("test_slider_continuous")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 36.0f).size(160.0f, 10.0f)
                .range(0.0, 1.0, 0.65).step(0.01)
                .fillColor(Color.fromRGB(80, 180, 255)).thumbColor(Color.WHITE).build());

        // Stepped Slider (0 to 100, step 10)
        root.addComponent(TextComponent.builder("lbl_slider2")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 52.0f).size(160.0f, 6.0f)
                .text(Component.text("Stepped Slider (Range: 0 - 100, Step: 10, Default: 40)", NamedTextColor.WHITE)).fontSize(3.8f).build());

        root.addComponent(SliderComponent.builder("test_slider_stepped")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 60.0f).size(160.0f, 10.0f)
                .range(0.0, 100.0, 40.0).step(10.0)
                .fillColor(Color.fromRGB(240, 160, 40)).thumbColor(Color.fromRGB(255, 230, 150)).build());

        // Rainbow Custom Colored Slider
        root.addComponent(TextComponent.builder("lbl_slider3")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 76.0f).size(160.0f, 6.0f)
                .text(Component.text("Accent Green Slider (Range: 1 - 5, Step: 1)", NamedTextColor.WHITE)).fontSize(3.8f).build());

        root.addComponent(SliderComponent.builder("test_slider_accent")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 84.0f).size(160.0f, 10.0f)
                .range(1.0, 5.0, 3.0).step(1.0)
                .trackColor(Color.fromRGB(15, 30, 20)).fillColor(Color.fromRGB(50, 200, 100)).thumbColor(Color.WHITE).build());

        layer.addContainer(root);
        return lm;
    }

    // 3. CheckboxComponent Visual Test
    public static LayerManager createCheckboxTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: CHECKBOX COMPONENT", "Right-click checkboxes to toggle states and verify styles", 180.0f, 110.0f);

        // Checkbox 1: Default active
        root.addComponent(CheckboxComponent.builder("test_check_active")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(15.0f, 32.0f).size(12.0f, 12.0f)
                .checked(true).activeBoxColor(Color.fromRGB(50, 180, 90)).build());
        root.addComponent(TextComponent.builder("lbl_check1")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(32.0f, 34.0f).size(130.0f, 8.0f)
                .text(Component.text("Active Checkbox (Checked by default)", NamedTextColor.WHITE)).fontSize(4.2f).build());

        // Checkbox 2: Default inactive
        root.addComponent(CheckboxComponent.builder("test_check_inactive")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(15.0f, 52.0f).size(12.0f, 12.0f)
                .checked(false).inactiveBoxColor(Color.fromRGB(40, 45, 60)).build());
        root.addComponent(TextComponent.builder("lbl_check2")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(32.0f, 54.0f).size(130.0f, 8.0f)
                .text(Component.text("Inactive Checkbox (Click to toggle)", NamedTextColor.GRAY)).fontSize(4.2f).build());

        // Checkbox 3: Cyan Theme
        root.addComponent(CheckboxComponent.builder("test_check_cyan")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(15.0f, 72.0f).size(12.0f, 12.0f)
                .checked(true).activeBoxColor(Color.fromRGB(0, 180, 220)).borderRound(4.0f).build());
        root.addComponent(TextComponent.builder("lbl_check3")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(32.0f, 74.0f).size(130.0f, 8.0f)
                .text(Component.text("Custom Cyan Box with Rounding", NamedTextColor.AQUA)).fontSize(4.2f).build());

        layer.addContainer(root);
        return lm;
    }

    // 4. TextComponent Visual Test
    public static LayerManager createTextTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: TEXT COMPONENT", "Verifies font sizes, alignments, optical presets & gradients", 180.0f, 125.0f);

        // Bold Gradient Text
        root.addComponent(TextComponent.builder("txt_gradient")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 28.0f).size(160.0f, 10.0f)
                .text(UiText.builder().gradient("Gradient Title Preset", new TextColor[]{
                        UiText.hex("#FF3366"), UiText.hex("#FF9933"), UiText.hex("#33CCFF")
                }, 1.0, TextDecoration.BOLD).build())
                .fontSize(6.5f)
                .opticalPreset(UiTextOpticalPreset.BOLD_GRADIENT)
                .shadow(true)
                .build());

        // Alignments: Left, Center, Right
        root.addComponent(TextComponent.builder("txt_left")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 44.0f).size(50.0f, 8.0f)
                .text(Component.text("◄ Left Align", NamedTextColor.GREEN))
                .alignment(UiTextAlignment.LEFT).fontSize(4.0f).build());

        root.addComponent(TextComponent.builder("txt_center")
                .anchor(UiAnchorPoint.CENTER_TOP).origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 44.0f).size(60.0f, 8.0f)
                .text(Component.text("◆ Center Align ◆", NamedTextColor.YELLOW))
                .alignment(UiTextAlignment.CENTER).fontSize(4.0f).build());

        root.addComponent(TextComponent.builder("txt_right")
                .anchor(UiAnchorPoint.TOP_RIGHT).origin(UiAnchorPoint.TOP_RIGHT)
                .offset(-10.0f, 44.0f).size(50.0f, 8.0f)
                .text(Component.text("Right Align ►", NamedTextColor.AQUA))
                .alignment(UiTextAlignment.RIGHT).fontSize(4.0f).build());

        // Background card on Text
        root.addComponent(TextComponent.builder("txt_boxed")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 60.0f).size(160.0f, 14.0f)
                .backgroundColor(Color.fromARGB(150, 40, 50, 70))
                .text(Component.text("Text with integrated background badge", NamedTextColor.WHITE))
                .alignment(UiTextAlignment.CENTER).fontSize(4.5f).shadow(true).build());

        // Small description text
        root.addComponent(TextComponent.builder("txt_small")
                .anchor(UiAnchorPoint.BOTTOM_LEFT).origin(UiAnchorPoint.BOTTOM_LEFT)
                .offset(10.0f, -8.0f).size(160.0f, 8.0f)
                .text(Component.text("Subpixel font scaling: 3.0px crisp resolution text", NamedTextColor.DARK_GRAY))
                .fontSize(3.0f).build());

        layer.addContainer(root);
        return lm;
    }

    // 5. ShapeComponent Visual Test
    public static LayerManager createShapeTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: SHAPE COMPONENT", "Verifies rounded rects, circles, outlines and colors", 180.0f, 115.0f);

        // Solid Rounded Rect
        root.addComponent(ShapeComponent.builder("shape_rect")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(15.0f, 32.0f).size(45.0f, 30.0f)
                .color(Color.fromRGB(40, 120, 220))
                .borderRound(6.0f)
                .build());

        // Outlined Circle / Oval
        root.addComponent(ShapeComponent.builder("shape_circle")
                .anchor(UiAnchorPoint.CENTER_TOP).origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 32.0f).size(40.0f, 30.0f)
                .color(Color.fromARGB(160, 200, 80, 50))
                .borderRound(15.0f)
                .outline(true)
                .outlineColor(Color.fromRGB(255, 180, 80))
                .outlineThickness(2.0f)
                .build());

        // Neon Border Box
        root.addComponent(ShapeComponent.builder("shape_neon")
                .anchor(UiAnchorPoint.TOP_RIGHT).origin(UiAnchorPoint.TOP_RIGHT)
                .offset(-15.0f, 32.0f).size(45.0f, 30.0f)
                .color(Color.fromARGB(80, 160, 40, 200))
                .borderRound(4.0f)
                .outline(true)
                .outlineColor(Color.fromRGB(220, 80, 255))
                .outlineThickness(2.0f)
                .build());

        // Wide Bottom Divider Shape
        root.addComponent(ShapeComponent.builder("shape_bar")
                .anchor(UiAnchorPoint.CENTER_BOTTOM).origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -15.0f).size(150.0f, 6.0f)
                .color(Color.fromRGB(60, 200, 150))
                .borderRound(3.0f)
                .build());

        layer.addContainer(root);
        return lm;
    }

    // 6. IconComponent Visual Test
    public static LayerManager createIconTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: ICON COMPONENT", "Verifies 3D item stacks, materials & entity display scaling", 180.0f, 115.0f);

        Material[] mats = {Material.NETHERITE_SWORD, Material.ENCHANTED_GOLDEN_APPLE, Material.TOTEM_OF_UNDYING, Material.DRAGON_EGG};
        String[] titles = {"Sword", "Gapple", "Totem", "Dragon Egg"};

        for (int i = 0; i < mats.length; i++) {
            float xOffset = 15.0f + i * 40.0f;
            Container slot = Container.builder("slot_" + i)
                    .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                    .offset(xOffset, 32.0f).size(32.0f, 38.0f)
                    .backgroundColor(Color.fromARGB(160, 30, 36, 50))
                    .borderRound(3.0f)
                    .build();

            slot.addComponent(IconComponent.builder("icon_" + i)
                    .anchor(UiAnchorPoint.CENTER_TOP).origin(UiAnchorPoint.CENTER_TOP)
                    .offset(0.0f, 3.0f).size(18.0f, 18.0f)
                    .material(mats[i])
                    .build());

            slot.addComponent(TextComponent.builder("txt_" + i)
                    .anchor(UiAnchorPoint.CENTER_BOTTOM).origin(UiAnchorPoint.CENTER_BOTTOM)
                    .offset(0.0f, -2.0f).size(30.0f, 6.0f)
                    .alignment(UiTextAlignment.CENTER)
                    .text(Component.text(titles[i], NamedTextColor.YELLOW))
                    .fontSize(3.2f)
                    .build());

            root.addContainer(slot);
        }

        layer.addContainer(root);
        return lm;
    }

    // 7. Standard Container Visual Test
    public static LayerManager createContainerTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: CONTAINER HIERARCHY", "Verifies nested subcontainers, scale cascade & proportional scaling", 180.0f, 120.0f);

        // SubContainer A (Left Box)
        Container subA = Container.builder("sub_left")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, 30.0f).size(75.0f, 75.0f)
                .backgroundColor(Color.fromARGB(180, 35, 45, 65))
                .borderRound(4.0f)
                .build();

        subA.addComponent(TextComponent.builder("sub_a_title")
                .anchor(UiAnchorPoint.CENTER_TOP).origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 4.0f).size(70.0f, 6.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("SubContainer A", NamedTextColor.AQUA, TextDecoration.BOLD))
                .fontSize(4.0f).build());

        subA.addComponent(ButtonComponent.builder("sub_a_btn")
                .anchor(UiAnchorPoint.CENTER).origin(UiAnchorPoint.CENTER)
                .size(60.0f, 14.0f).label("Nested Button").build());

        // SubContainer B (Right Box with scale = 1.1)
        Container subB = Container.builder("sub_right")
                .anchor(UiAnchorPoint.TOP_RIGHT).origin(UiAnchorPoint.TOP_RIGHT)
                .offset(-10.0f, 30.0f).size(75.0f, 75.0f)
                .backgroundColor(Color.fromARGB(180, 55, 35, 45))
                .borderRound(4.0f)
                .build();

        subB.addComponent(TextComponent.builder("sub_b_title")
                .anchor(UiAnchorPoint.CENTER_TOP).origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 4.0f).size(70.0f, 6.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("SubContainer B", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                .fontSize(4.0f).build());

        subB.addComponent(CheckboxComponent.builder("sub_b_check")
                .anchor(UiAnchorPoint.CENTER).origin(UiAnchorPoint.CENTER)
                .offset(0.0f, -6.0f).size(12.0f, 12.0f).checked(true).build());

        subB.addComponent(SliderComponent.builder("sub_b_slider")
                .anchor(UiAnchorPoint.CENTER_BOTTOM).origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -6.0f).size(65.0f, 8.0f).range(0.0, 1.0, 0.5).build());

        root.addContainer(subA);
        root.addContainer(subB);

        layer.addContainer(root);
        return lm;
    }

    // 8. DropdownContainer Visual Test
    public static LayerManager createDropdownTest() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("test_layer", 0);
        Container root = createBaseCard("VISUAL TEST: DROPDOWN CONTAINER", "Click header to trigger animation & push out child containers", 180.0f, 130.0f);

        float currentY = 28.0f;

        // Dropdown 1: SLIDE_AND_FADE animation (Expanded by default)
        DropdownContainer dd1 = DropdownContainer.builder("test_dd_slide")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, currentY).width(160.0f).headerHeight(16.0f)
                .headerTitle(Component.text("▼  SLIDE & FADE DROPDOWN", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .animationType(DropdownAnimationType.SLIDE_AND_FADE)
                .animationDurationTicks(8)
                .slideDistance(12.0f)
                .contentPadding(3.0f).itemSpacing(2.5f)
                .contentBackgroundColor(Color.fromARGB(150, 25, 30, 42))
                .expanded(true)
                .build();

        // Add 2 child containers inside dd1
        Container itemA = Container.builder("dd1_item_a").size(154.0f, 12.0f)
                .backgroundColor(Color.fromARGB(160, 35, 42, 58)).borderRound(2.0f).build();
        itemA.addComponent(TextComponent.builder("dd1_txt_a").anchor(UiAnchorPoint.CENTER_LEFT).origin(UiAnchorPoint.CENTER_LEFT)
                .offset(4.0f, 0.0f).size(90.0f, 6.0f).text(Component.text("Child Container 1 (Button)", NamedTextColor.WHITE)).fontSize(3.8f).build());
        itemA.addComponent(ButtonComponent.builder("btn_dd1").anchor(UiAnchorPoint.CENTER_RIGHT).origin(UiAnchorPoint.CENTER_RIGHT)
                .offset(-3.0f, 0.0f).size(45.0f, 8.0f).label("Action").build());
        dd1.addDropdownItem(itemA);

        Container itemB = Container.builder("dd1_item_b").size(154.0f, 12.0f)
                .backgroundColor(Color.fromARGB(160, 35, 42, 58)).borderRound(2.0f).build();
        itemB.addComponent(CheckboxComponent.builder("chk_dd1").anchor(UiAnchorPoint.CENTER_LEFT).origin(UiAnchorPoint.CENTER_LEFT)
                .offset(4.0f, 0.0f).size(8.0f, 8.0f).checked(true).build());
        itemB.addComponent(TextComponent.builder("dd1_txt_b").anchor(UiAnchorPoint.CENTER_LEFT).origin(UiAnchorPoint.CENTER_LEFT)
                .offset(16.0f, 0.0f).size(130.0f, 6.0f).text(Component.text("Child Container 2 (Toggle)", NamedTextColor.GRAY)).fontSize(3.8f).build());
        dd1.addDropdownItem(itemB);

        root.addContainer(dd1);
        currentY += dd1.height() + 4.0f;

        // Dropdown 2: BOUNCE_OUT animation (Collapsed by default)
        DropdownContainer dd2 = DropdownContainer.builder("test_dd_bounce")
                .anchor(UiAnchorPoint.TOP_LEFT).origin(UiAnchorPoint.TOP_LEFT)
                .offset(10.0f, currentY).width(160.0f).headerHeight(16.0f)
                .headerTitle(Component.text("▶  BOUNCE OUT DROPDOWN", NamedTextColor.AQUA, TextDecoration.BOLD))
                .animationType(DropdownAnimationType.BOUNCE_OUT)
                .animationDurationTicks(10)
                .slideDistance(14.0f)
                .contentPadding(3.0f).itemSpacing(2.5f)
                .contentBackgroundColor(Color.fromARGB(150, 20, 32, 42))
                .expanded(false)
                .build();

        Container itemC = Container.builder("dd2_item_c").size(154.0f, 14.0f)
                .backgroundColor(Color.fromARGB(160, 28, 48, 62)).borderRound(2.0f).build();
        itemC.addComponent(SliderComponent.builder("sld_dd2").anchor(UiAnchorPoint.CENTER).origin(UiAnchorPoint.CENTER)
                .size(145.0f, 7.0f).range(0.0, 1.0, 0.7).build());
        dd2.addDropdownItem(itemC);

        root.addContainer(dd2);

        layer.addContainer(root);
        return lm;
    }
}
