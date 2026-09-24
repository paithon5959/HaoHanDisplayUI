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

import org.bukkit.Color;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.Objects;

/**
 * Versatile 2D shape component supporting rounded rectangles, polygons, circles,
 * rotation, borders, and fills matching the visual GUI system.
 */
public class ShapeComponent extends AbstractComponent {

    private String shapeType = "rounded_rect";
    private boolean outline = false;
    private Color outlineColor = Color.WHITE;
    private float outlineThickness = 2.0f;
    private String outlineStyle = "solid";
    private float rotation = 0.0f;

    public ShapeComponent(String id) {
        super(id);
        this.color = Color.fromRGB(50, 100, 200);
        this.borderRound = 6.0f;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String shapeType() {
        return shapeType;
    }

    public ShapeComponent shapeType(String shapeType) {
        this.shapeType = Objects.requireNonNull(shapeType, "shapeType cannot be null");
        return this;
    }

    public boolean outline() {
        return outline;
    }

    public ShapeComponent outline(boolean outline) {
        this.outline = outline;
        return this;
    }

    public Color outlineColor() {
        return outlineColor;
    }

    public ShapeComponent outlineColor(Color outlineColor) {
        this.outlineColor = Objects.requireNonNullElse(outlineColor, Color.WHITE);
        return this;
    }

    public float outlineThickness() {
        return outlineThickness;
    }

    public ShapeComponent outlineThickness(float outlineThickness) {
        this.outlineThickness = outlineThickness;
        return this;
    }

    public String outlineStyle() {
        return outlineStyle;
    }

    public ShapeComponent outlineStyle(String outlineStyle) {
        this.outlineStyle = outlineStyle != null ? outlineStyle : "solid";
        return this;
    }

    public float rotation() {
        return rotation;
    }

    public ShapeComponent rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public static final class Builder {
        private final String id;
        private String shapeType = "rounded_rect";
        private UiAnchorPoint anchor = UiAnchorPoint.CENTER;
        private UiAnchorPoint origin = UiAnchorPoint.CENTER;
        private float x = 0.0f;
        private float y = 0.0f;
        private float width = 50.0f;
        private float height = 50.0f;
        private float scale = 1.0f;
        private float borderRound = 6.0f;
        private Color color = Color.fromRGB(50, 100, 200);
        private Color backgroundColor;
        private UiGradient gradient;
        private boolean outline = false;
        private Color outlineColor = Color.WHITE;
        private float outlineThickness = 2.0f;
        private String outlineStyle = "solid";
        private float rotation = 0.0f;

        public Builder(String id) {
            this.id = id;
        }

        public Builder shapeType(String shapeType) {
            this.shapeType = shapeType;
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

        public Builder borderRound(float borderRound) {
            this.borderRound = borderRound;
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

        public Builder outline(boolean outline) {
            this.outline = outline;
            return this;
        }

        public Builder outlineColor(Color outlineColor) {
            this.outlineColor = outlineColor;
            return this;
        }

        public Builder outlineThickness(float outlineThickness) {
            this.outlineThickness = outlineThickness;
            return this;
        }

        public Builder outlineStyle(String outlineStyle) {
            this.outlineStyle = outlineStyle;
            return this;
        }

        public Builder rotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        public ShapeComponent build() {
            ShapeComponent comp = new ShapeComponent(id);
            comp.shapeType(shapeType);
            comp.anchor(anchor);
            comp.origin(origin);
            comp.offset(x, y);
            comp.size(width, height);
            comp.scale(scale);
            comp.borderRound(borderRound);
            comp.color(color);
            comp.backgroundColor(backgroundColor);
            comp.gradient(gradient);
            comp.outline(outline);
            comp.outlineColor(outlineColor);
            comp.outlineThickness(outlineThickness);
            comp.outlineStyle(outlineStyle);
            comp.rotation(rotation);
            return comp;
        }
    }
}
