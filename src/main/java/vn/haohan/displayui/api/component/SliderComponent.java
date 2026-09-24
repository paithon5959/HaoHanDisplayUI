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
import vn.haohan.displayui.api.interaction.event.UiControlChangeEvent;
import vn.haohan.displayui.api.component.event.ComponentChangeEvent;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.layout.UiRect;
import vn.haohan.displayui.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Interactive slider component supporting continuous or stepped values,
 * customizable track, fill, and thumb colors, and value change callbacks.
 */
public class SliderComponent extends AbstractComponent {

    private double minimum = 0.0;
    private double maximum = 1.0;
    private double value = 0.5;
    private double step = 0.0;
    private float hitSlop = 0.0f;

    private Color trackColor = Color.fromRGB(20, 20, 25);
    private Color fillColor = Color.fromRGB(50, 180, 255);
    private Color thumbColor = Color.fromRGB(240, 240, 255);

    public SliderComponent(String id) {
        super(id);
        this.width = 120.0f;
        this.height = 14.0f;
        this.borderRound = 4.0f;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public double minimum() {
        return minimum;
    }

    public double maximum() {
        return maximum;
    }

    public double value() {
        return value;
    }

    public SliderComponent range(double minimum, double maximum, double value) {
        if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum >= maximum) {
            throw new IllegalArgumentException("minimum must be finite and lower than maximum");
        }
        if (!Double.isFinite(value)) throw new IllegalArgumentException("value must be finite");
        this.minimum = minimum;
        this.maximum = maximum;
        this.value = clampAndSnap(value);
        return this;
    }

    public SliderComponent value(double value) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("value must be finite");
        this.value = clampAndSnap(value);
        return this;
    }

    public double step() {
        return step;
    }

    public SliderComponent step(double step) {
        if (!Double.isFinite(step) || step < 0.0) {
            throw new IllegalArgumentException("step must be non-negative and finite");
        }
        this.step = step;
        this.value = clampAndSnap(this.value);
        return this;
    }

    public float hitSlop() {
        return hitSlop;
    }

    public SliderComponent hitSlop(float hitSlop) {
        if (!Float.isFinite(hitSlop) || hitSlop < 0.0f) {
            throw new IllegalArgumentException("hitSlop must be non-negative and finite");
        }
        this.hitSlop = hitSlop;
        return this;
    }

    public Color trackColor() {
        return trackColor;
    }

    public SliderComponent trackColor(Color trackColor) {
        this.trackColor = Objects.requireNonNull(trackColor, "trackColor cannot be null");
        return this;
    }

    public Color fillColor() {
        return fillColor;
    }

    public SliderComponent fillColor(Color fillColor) {
        this.fillColor = Objects.requireNonNull(fillColor, "fillColor cannot be null");
        return this;
    }

    public Color thumbColor() {
        return thumbColor;
    }

    public SliderComponent thumbColor(Color thumbColor) {
        this.thumbColor = Objects.requireNonNull(thumbColor, "thumbColor cannot be null");
        return this;
    }

    private final List<Consumer<ComponentChangeEvent>> changeHandlers = new ArrayList<>();

    public SliderComponent onValueChange(Consumer<ComponentChangeEvent> handler) {
        changeHandlers.add(Objects.requireNonNull(handler, "change handler cannot be null"));
        return this;
    }

    public List<Consumer<ComponentChangeEvent>> changeHandlers() {
        return List.copyOf(changeHandlers);
    }

    public void triggerChange(ComponentChangeEvent event) {
        for (Consumer<ComponentChangeEvent> handler : changeHandlers) {
            handler.accept(event);
        }
    }

    public void triggerChange(UiControlChangeEvent event) {
        ComponentChangeEvent compEvent = event != null
                ? new ComponentChangeEvent(this, event.getPlayer(), event.getOldValue(), event.getValue())
                : ComponentChangeEvent.of(this, null, 0.0, this.value);
        triggerChange(compEvent);
    }

    public double normalizedProgress() {
        if (maximum <= minimum) return 0.0;
        return Math.clamp((value - minimum) / (maximum - minimum), 0.0, 1.0);
    }

    private double clampAndSnap(double val) {
        double clamped = Math.clamp(val, minimum, maximum);
        if (step <= 0.0) return clamped;
        double steps = Math.round((clamped - minimum) / step);
        return Math.clamp(minimum + (steps * step), minimum, maximum);
    }

    public static final class Builder {
        private final String id;
        private UiAnchorPoint anchor = UiAnchorPoint.CENTER;
        private UiAnchorPoint origin = UiAnchorPoint.CENTER;
        private float x = 0.0f;
        private float y = 0.0f;
        private float width = 120.0f;
        private float height = 14.0f;
        private float scale = 1.0f;
        private float borderRound = 4.0f;
        private double minimum = 0.0;
        private double maximum = 1.0;
        private double value = 0.5;
        private double step = 0.0;
        private float hitSlop = 0.0f;
        private Color trackColor = Color.fromRGB(20, 20, 25);
        private Color fillColor = Color.fromRGB(50, 180, 255);
        private Color thumbColor = Color.fromRGB(240, 240, 255);
        private final List<Consumer<ComponentChangeEvent>> changeHandlers = new ArrayList<>();

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

        public Builder range(double min, double max, double val) {
            this.minimum = min;
            this.maximum = max;
            this.value = val;
            return this;
        }

        public Builder step(double step) {
            this.step = step;
            return this;
        }

        public Builder hitSlop(float hitSlop) {
            this.hitSlop = hitSlop;
            return this;
        }

        public Builder trackColor(Color color) {
            this.trackColor = color;
            return this;
        }

        public Builder fillColor(Color color) {
            this.fillColor = color;
            return this;
        }

        public Builder thumbColor(Color color) {
            this.thumbColor = color;
            return this;
        }

        public Builder onValueChange(Consumer<ComponentChangeEvent> handler) {
            this.changeHandlers.add(handler);
            return this;
        }

        public SliderComponent build() {
            SliderComponent slider = new SliderComponent(id);
            slider.anchor(anchor);
            slider.origin(origin);
            slider.offset(x, y);
            slider.size(width, height);
            slider.scale(scale);
            slider.borderRound(borderRound);
            slider.range(minimum, maximum, value);
            slider.step(step);
            slider.hitSlop(hitSlop);
            slider.trackColor(trackColor);
            slider.fillColor(fillColor);
            slider.thumbColor(thumbColor);
            for (Consumer<ComponentChangeEvent> handler : changeHandlers) {
                slider.onValueChange(handler);
            }
            return slider;
        }
    }
}
