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
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.interaction.UiButtonAction;
import vn.haohan.displayui.api.interaction.event.UiButtonClickEvent;
import vn.haohan.displayui.api.component.event.ComponentClickEvent;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Clickable interactive button component with visual styling, border rounding,
 * outline options, and action dispatching.
 */
public class ButtonComponent extends AbstractComponent {

    private Component label = Component.empty();
    private UiButtonAction action = UiButtonAction.none();
    private float hitSlop = 0.0f;
    private boolean outline = false;
    private Color outlineColor = Color.fromRGB(80, 140, 240);
    private float outlineThickness = 1.0f;

    public ButtonComponent(String id) {
        super(id);
        this.width = 80.0f;
        this.height = 20.0f;
        this.borderRound = 6.0f;
        this.backgroundColor = Color.fromRGB(35, 45, 65);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public Component label() {
        return label;
    }

    public ButtonComponent label(Component label) {
        this.label = Objects.requireNonNullElse(label, Component.empty());
        return this;
    }

    public ButtonComponent label(String plainText) {
        this.label = plainText != null ? Component.text(plainText) : Component.empty();
        return this;
    }

    public UiButtonAction action() {
        return action;
    }

    public ButtonComponent action(UiButtonAction action) {
        this.action = Objects.requireNonNullElse(action, UiButtonAction.none());
        return this;
    }

    public float hitSlop() {
        return hitSlop;
    }

    public ButtonComponent hitSlop(float hitSlop) {
        if (!Float.isFinite(hitSlop) || hitSlop < 0.0f) {
            throw new IllegalArgumentException("hitSlop must be non-negative and finite");
        }
        this.hitSlop = hitSlop;
        return this;
    }

    public boolean outline() {
        return outline;
    }

    public ButtonComponent outline(boolean outline) {
        this.outline = outline;
        return this;
    }

    public Color outlineColor() {
        return outlineColor;
    }

    public ButtonComponent outlineColor(Color outlineColor) {
        this.outlineColor = Objects.requireNonNullElse(outlineColor, Color.WHITE);
        return this;
    }

    public float outlineThickness() {
        return outlineThickness;
    }

    public ButtonComponent outlineThickness(float outlineThickness) {
        this.outlineThickness = outlineThickness;
        return this;
    }

    private final List<Consumer<ComponentClickEvent>> clickHandlers = new ArrayList<>();

    public ButtonComponent onClick(Consumer<ComponentClickEvent> handler) {
        clickHandlers.add(Objects.requireNonNull(handler, "click handler cannot be null"));
        return this;
    }

    public List<Consumer<ComponentClickEvent>> clickHandlers() {
        return List.copyOf(clickHandlers);
    }

    public void triggerClick(ComponentClickEvent event) {
        for (Consumer<ComponentClickEvent> handler : clickHandlers) {
            handler.accept(event);
        }
    }

    public void triggerClick(UiButtonClickEvent event) {
        ComponentClickEvent compEvent = event != null
                ? new ComponentClickEvent(this, event.getPlayer(), event.getLocalX(), event.getLocalY())
                : ComponentClickEvent.of(this, null);
        triggerClick(compEvent);
    }

    public static final class Builder {
        private final String id;
        private Component label = Component.empty();
        private UiAnchorPoint anchor = UiAnchorPoint.CENTER;
        private UiAnchorPoint origin = UiAnchorPoint.CENTER;
        private float x = 0.0f;
        private float y = 0.0f;
        private float width = 80.0f;
        private float height = 20.0f;
        private float scale = 1.0f;
        private float borderRound = 6.0f;
        private Color color = Color.WHITE;
        private Color backgroundColor = Color.fromRGB(35, 45, 65);
        private UiGradient gradient;
        private boolean outline = false;
        private Color outlineColor = Color.fromRGB(80, 140, 240);
        private float outlineThickness = 1.0f;
        private float hitSlop = 0.0f;
        private UiButtonAction action = UiButtonAction.none();
        private final List<Consumer<ComponentClickEvent>> clickHandlers = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        public Builder label(String plainText) {
            this.label = plainText != null ? Component.text(plainText) : Component.empty();
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

        public Builder hitSlop(float hitSlop) {
            this.hitSlop = hitSlop;
            return this;
        }

        public Builder action(UiButtonAction action) {
            this.action = action;
            return this;
        }

        public Builder onClick(Consumer<ComponentClickEvent> handler) {
            this.clickHandlers.add(handler);
            return this;
        }

        public ButtonComponent build() {
            ButtonComponent btn = new ButtonComponent(id);
            btn.label(label);
            btn.anchor(anchor);
            btn.origin(origin);
            btn.offset(x, y);
            btn.size(width, height);
            btn.scale(scale);
            btn.borderRound(borderRound);
            btn.color(color);
            btn.backgroundColor(backgroundColor);
            btn.gradient(gradient);
            btn.outline(outline);
            btn.outlineColor(outlineColor);
            btn.outlineThickness(outlineThickness);
            btn.hitSlop(hitSlop);
            btn.action(action);
            for (Consumer<ComponentClickEvent> handler : clickHandlers) {
                btn.onClick(handler);
            }
            return btn;
        }
    }
}
