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
import vn.haohan.displayui.api.interaction.event.UiControlChangeEvent;
import vn.haohan.displayui.api.component.event.ComponentChangeEvent;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Boolean toggle checkbox component with checked/unchecked styling and toggle events.
 */
public class CheckboxComponent extends AbstractComponent {

    private boolean checked = false;
    private Color checkColor = Color.fromRGB(240, 240, 255);
    private Color activeBoxColor = Color.fromRGB(40, 180, 100);
    private Color inactiveBoxColor = Color.fromRGB(35, 45, 60);
    private float hitSlop = 0.0f;

    public CheckboxComponent(String id) {
        super(id);
        this.width = 14.0f;
        this.height = 14.0f;
        this.borderRound = 3.0f;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public boolean checked() {
        return checked;
    }

    public CheckboxComponent checked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public Color checkColor() {
        return checkColor;
    }

    public CheckboxComponent checkColor(Color checkColor) {
        this.checkColor = Objects.requireNonNull(checkColor, "checkColor cannot be null");
        return this;
    }

    public Color activeBoxColor() {
        return activeBoxColor;
    }

    public CheckboxComponent activeBoxColor(Color activeBoxColor) {
        this.activeBoxColor = Objects.requireNonNull(activeBoxColor, "activeBoxColor cannot be null");
        return this;
    }

    public Color inactiveBoxColor() {
        return inactiveBoxColor;
    }

    public CheckboxComponent inactiveBoxColor(Color inactiveBoxColor) {
        this.inactiveBoxColor = Objects.requireNonNull(inactiveBoxColor, "inactiveBoxColor cannot be null");
        return this;
    }

    public float hitSlop() {
        return hitSlop;
    }

    public CheckboxComponent hitSlop(float hitSlop) {
        if (!Float.isFinite(hitSlop) || hitSlop < 0.0f) {
            throw new IllegalArgumentException("hitSlop must be non-negative and finite");
        }
        this.hitSlop = hitSlop;
        return this;
    }

    private final List<Consumer<ComponentChangeEvent>> toggleHandlers = new ArrayList<>();

    public CheckboxComponent onToggle(Consumer<ComponentChangeEvent> handler) {
        toggleHandlers.add(Objects.requireNonNull(handler, "toggle handler cannot be null"));
        return this;
    }

    public List<Consumer<ComponentChangeEvent>> toggleHandlers() {
        return List.copyOf(toggleHandlers);
    }

    public void triggerToggle(ComponentChangeEvent event) {
        this.checked = !this.checked;
        for (Consumer<ComponentChangeEvent> handler : toggleHandlers) {
            handler.accept(event);
        }
    }

    public void triggerToggle(UiControlChangeEvent event) {
        ComponentChangeEvent compEvent = event != null
                ? new ComponentChangeEvent(this, event.getPlayer(), event.getOldValue(), event.getValue())
                : ComponentChangeEvent.of(this, null, this.checked ? 1.0 : 0.0, this.checked ? 0.0 : 1.0);
        triggerToggle(compEvent);
    }

    public Color currentBoxColor() {
        return checked ? activeBoxColor : inactiveBoxColor;
    }

    public static final class Builder {
        private final String id;
        private UiAnchorPoint anchor = UiAnchorPoint.CENTER;
        private UiAnchorPoint origin = UiAnchorPoint.CENTER;
        private float x = 0.0f;
        private float y = 0.0f;
        private float width = 14.0f;
        private float height = 14.0f;
        private float scale = 1.0f;
        private float borderRound = 3.0f;
        private boolean checked = false;
        private Color checkColor = Color.fromRGB(240, 240, 255);
        private Color activeBoxColor = Color.fromRGB(40, 180, 100);
        private Color inactiveBoxColor = Color.fromRGB(35, 45, 60);
        private float hitSlop = 0.0f;
        private final List<Consumer<ComponentChangeEvent>> toggleHandlers = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
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

        public Builder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        public Builder checkColor(Color color) {
            this.checkColor = color;
            return this;
        }

        public Builder activeBoxColor(Color color) {
            this.activeBoxColor = color;
            return this;
        }

        public Builder inactiveBoxColor(Color color) {
            this.inactiveBoxColor = color;
            return this;
        }

        public Builder hitSlop(float hitSlop) {
            this.hitSlop = hitSlop;
            return this;
        }

        public Builder onToggle(Consumer<ComponentChangeEvent> handler) {
            this.toggleHandlers.add(handler);
            return this;
        }

        public CheckboxComponent build() {
            CheckboxComponent cb = new CheckboxComponent(id);
            cb.anchor(anchor);
            cb.origin(origin);
            cb.offset(x, y);
            cb.size(width, height);
            cb.scale(scale);
            cb.borderRound(borderRound);
            cb.checked(checked);
            cb.checkColor(checkColor);
            cb.activeBoxColor(activeBoxColor);
            cb.inactiveBoxColor(inactiveBoxColor);
            cb.hitSlop(hitSlop);
            for (Consumer<ComponentChangeEvent> handler : toggleHandlers) {
                cb.onToggle(handler);
            }
            return cb;
        }
    }
}
