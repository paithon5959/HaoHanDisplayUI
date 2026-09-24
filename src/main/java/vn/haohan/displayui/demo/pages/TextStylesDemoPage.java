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
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiText;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.api.text.UiTextOpticalPreset;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

public final class TextStylesDemoPage extends BaseDemoPage {

    @Override
    public String title() {
        return "TEXT STYLES";
    }

    @Override
    public void build(Container container, DemoContext context) {
        int frame = context.gradientFrame();
        addTextSample(container, "label_normal", "Normal text", 0.0f,
                Component.text("Normal text", NamedTextColor.WHITE), UiTextOpticalPreset.PLAIN);

        addTextSample(container, "label_bold", "Bold", 12.0f,
                Component.text("Bold text", NamedTextColor.GOLD, TextDecoration.BOLD),
                UiTextOpticalPreset.BOLD);

        addTextSample(container, "label_italic", "Italic", 24.0f,
                Component.text("Italic text", NamedTextColor.LIGHT_PURPLE, TextDecoration.ITALIC),
                UiTextOpticalPreset.ITALIC);

        addTextSample(container, "label_moving", "Moving", 36.0f,
                movingGradient(frame), UiTextOpticalPreset.BOLD_GRADIENT);

        addTextSample(container, "label_obf", "§k", 48.0f,
                Component.text("OBFUSCATED", NamedTextColor.AQUA, TextDecoration.OBFUSCATED),
                UiTextOpticalPreset.PLAIN);

        addTextSample(container, "label_mixed", "Mixed", 60.0f,
                UiText.builder().text("Bold ", NamedTextColor.RED, TextDecoration.BOLD)
                        .text("+ italic ", NamedTextColor.YELLOW, TextDecoration.ITALIC)
                        .text("underlined", NamedTextColor.GREEN, TextDecoration.UNDERLINED)
                        .build(),
                UiTextOpticalPreset.PLAIN);
    }

    @Override
    public void onTick(DemoContext context) {
        if (context.handle() == null || !context.handle().isAnimating()) {
            context.updateView();
        }
    }

    private void addTextSample(Container container, String id, String label, float y,
                               Component sample, UiTextOpticalPreset preset) {
        container.addComponent(TextComponent.builder(id + "_title")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, y)
                .size(46.0f, 10.0f)
                .alignment(UiTextAlignment.RIGHT)
                .text(Component.text(label, NamedTextColor.DARK_GRAY))
                .fontSize(4.5f)
                .build());

        container.addComponent(TextComponent.builder(id + "_sample")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(52.0f, y)
                .size(122.0f, 10.0f)
                .alignment(UiTextAlignment.LEFT)
                .text(sample)
                .opticalPreset(preset)
                .fontSize(5.0f)
                .shadow(true)
                .build());
    }

    private Component movingGradient(int frame) {
        float phase = (frame % 80) / 80.0f;
        TextColor c1 = TextColor.color(java.awt.Color.HSBtoRGB(phase, 0.85f, 1.0f) & 0xFFFFFF);
        TextColor c2 = TextColor.color(java.awt.Color.HSBtoRGB((phase + 0.33f) % 1.0f, 0.85f, 1.0f) & 0xFFFFFF);
        TextColor c3 = TextColor.color(java.awt.Color.HSBtoRGB((phase + 0.66f) % 1.0f, 0.85f, 1.0f) & 0xFFFFFF);
        return UiText.builder().gradient("Continuous rainbow motion",
                new TextColor[]{c1, c2, c3}, 1.0, TextDecoration.BOLD).build();
    }
}
