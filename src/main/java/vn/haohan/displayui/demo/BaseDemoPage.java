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
package vn.haohan.displayui.demo;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import vn.haohan.displayui.api.component.*;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;

/**
 * Base class for demo presentation pages providing declarative component builders.
 */
public abstract class BaseDemoPage implements DemoPage {

    public static ButtonComponent createButton(String id, float width, float height, String label, String description) {
        return ButtonComponent.builder(id)
                .size(width, height)
                .label(label)
                .borderRound(4.0f)
                .outline(true)
                .outlineColor(Color.fromRGB(80, 140, 240))
                .backgroundColor(Color.fromRGB(35, 45, 65))
                .build();
    }

    public static ButtonComponent createButton(String id, float x, float y, float width, float height, String label) {
        return ButtonComponent.builder(id)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(x, y)
                .size(width, height)
                .label(label)
                .borderRound(4.0f)
                .outline(true)
                .outlineColor(Color.fromRGB(80, 140, 240))
                .backgroundColor(Color.fromRGB(35, 45, 65))
                .build();
    }

    public static SliderComponent createSlider(String id, float width, double min, double max, double val, double step) {
        return SliderComponent.builder(id)
                .size(width, 14.0f)
                .range(min, max, val)
                .step(step)
                .borderRound(4.0f)
                .build();
    }

    public static CheckboxComponent createCheckbox(String id, boolean checked) {
        return CheckboxComponent.builder(id)
                .checked(checked)
                .borderRound(3.0f)
                .build();
    }

    public static Container createCard(String id, float width, float height, Color bg, float borderRound) {
        return Container.builder(id)
                .size(width, height)
                .backgroundColor(bg)
                .borderRound(borderRound)
                .build();
    }

    public static TextComponent createText(String id, Component text, float width, float height,
                                          UiTextAlignment alignment, float fontSize) {
        return TextComponent.builder(id)
                .size(width, height)
                .text(text)
                .alignment(alignment)
                .fontSize(fontSize)
                .build();
    }

    public static ShapeComponent createShape(String id, String shapeType, float width, float height,
                                            Color color, float borderRound) {
        return ShapeComponent.builder(id)
                .shapeType(shapeType)
                .size(width, height)
                .color(color)
                .borderRound(borderRound)
                .build();
    }

    public static IconComponent createIcon(String id, Material material, float width, float height) {
        return IconComponent.builder(id)
                .material(material)
                .size(width, height)
                .build();
    }

    public static IconComponent createIcon(String id, ItemStack item, float width, float height) {
        return IconComponent.builder(id)
                .item(item)
                .size(width, height)
                .build();
    }
}
