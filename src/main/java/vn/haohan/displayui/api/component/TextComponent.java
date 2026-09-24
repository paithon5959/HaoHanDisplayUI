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
package vn.haohan.displayui.api.component;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.api.text.UiTextOpticalPreset;

import java.util.Objects;

/**
 * Text component rendering rich Kyori Adventure text with customizable alignment,
 * optical styling, shadows, and container-relative positioning.
 */
public class TextComponent extends AbstractComponent {

    private Component text = Component.empty();
    private UiTextAlignment alignment = UiTextAlignment.LEFT;
    private float fontSize = 8.0f;
    private boolean shadow = false;
    private boolean seeThrough = false;
    private UiTextOpticalPreset opticalPreset;

    public TextComponent(String id) {
        super(id);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public Component text() {
        return text;
    }

    public TextComponent text(Component text) {
        this.text = Objects.requireNonNullElse(text, Component.empty());
        return this;
    }

    public TextComponent text(String plainText) {
        this.text = plainText != null ? Component.text(plainText) : Component.empty();
        return this;
    }

    public UiTextAlignment alignment() {
        return alignment;
    }

    public TextComponent alignment(UiTextAlignment alignment) {
        this.alignment = Objects.requireNonNull(alignment, "alignment cannot be null");
        return this;
    }

    public float fontSize() {
        return fontSize;
    }

    public TextComponent fontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public boolean shadow() {
        return shadow;
    }

    public TextComponent shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public boolean seeThrough() {
        return seeThrough;
    }

    public TextComponent seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public UiTextOpticalPreset opticalPreset() {
        return opticalPreset;
    }

    public TextComponent opticalPreset(UiTextOpticalPreset opticalPreset) {
        this.opticalPreset = opticalPreset;
        return this;
    }

    public static final class Builder {
        private final String id;
        private Component text = Component.empty();
        private UiAnchorPoint anchor = UiAnchorPoint.CENTER;
        private UiAnchorPoint origin = UiAnchorPoint.CENTER;
        private float x = 0.0f;
        private float y = 0.0f;
        private float width = 100.0f;
        private float height = 16.0f;
        private float scale = 1.0f;
        private float fontSize = 8.0f;
        private boolean shadow = false;
        private boolean seeThrough = false;
        private UiTextAlignment alignment = UiTextAlignment.LEFT;
        private UiTextOpticalPreset opticalPreset;
        private Color color;
        private Color backgroundColor;
        private UiGradient gradient;
        private float borderRound = 0.0f;

        public Builder(String id) {
            this.id = id;
        }

        public Builder text(Component text) {
            this.text = text;
            return this;
        }

        public Builder text(String plainText) {
            this.text = plainText != null ? Component.text(plainText) : Component.empty();
            return this;
        }

        public Builder anchor(UiAnchorPoint anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder origin(UiAnchorPoint origin) {
            this.origin = origin;
            return this;
        }

        public Builder offset(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder fontSize(float fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder seeThrough(boolean seeThrough) {
            this.seeThrough = seeThrough;
            return this;
        }

        public Builder alignment(UiTextAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder opticalPreset(UiTextOpticalPreset preset) {
            this.opticalPreset = preset;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder gradient(UiGradient gradient) {
            this.gradient = gradient;
            return this;
        }

        public Builder borderRound(float borderRound) {
            this.borderRound = borderRound;
            return this;
        }

        public Builder opacity(float opacity) {
            this.opacity = opacity;
            return this;
        }

        private float opacity = 1.0f;

        public TextComponent build() {
            TextComponent comp = new TextComponent(id);
            comp.text(text);
            comp.anchor(anchor);
            comp.origin(origin);
            comp.offset(x, y);
            comp.size(width, height);
            comp.scale(scale);
            comp.fontSize(fontSize);
            comp.shadow(shadow);
            comp.seeThrough(seeThrough);
            comp.alignment(alignment);
            comp.opticalPreset(opticalPreset);
            comp.color(color);
            comp.backgroundColor(backgroundColor);
            comp.gradient(gradient);
            comp.borderRound(borderRound);
            comp.opacity(opacity);
            return comp;
        }
    }
}
