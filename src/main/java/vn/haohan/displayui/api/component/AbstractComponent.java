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
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class providing common state, positioning, scaling, and animation
 * logic for all {@link Component} implementations.
 */
public abstract class AbstractComponent implements Component {

    protected final String id;
    protected UiAnchorPoint anchor = UiAnchorPoint.CENTER;
    protected UiAnchorPoint origin = UiAnchorPoint.CENTER;
    protected float x = 0.0f;
    protected float y = 0.0f;
    protected float width = 10.0f;
    protected float height = 10.0f;
    protected float scale = 1.0f;
    protected float borderRound = 0.0f;
    protected Color color;
    protected Color backgroundColor;
    protected UiGradient gradient;
    protected float opacity = 1.0f;
    protected boolean visible = true;

    protected Container parent;
    protected UiAnimation activeAnimation;

    protected AbstractComponent(String id) {
        this.id = Objects.requireNonNull(id, "Component id cannot be null");
        if (id.isBlank()) throw new IllegalArgumentException("Component id cannot be empty");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public UiAnchorPoint anchor() {
        return anchor;
    }

    @Override
    public AbstractComponent anchor(UiAnchorPoint anchor) {
        this.anchor = Objects.requireNonNull(anchor, "anchor cannot be null");
        return this;
    }

    @Override
    public UiAnchorPoint origin() {
        return origin;
    }

    @Override
    public AbstractComponent origin(UiAnchorPoint origin) {
        this.origin = Objects.requireNonNull(origin, "origin cannot be null");
        return this;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public AbstractComponent offset(float x, float y) {
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            throw new IllegalArgumentException("Offsets must be finite");
        }
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    @Override
    public AbstractComponent size(float width, float height) {
        if (!Float.isFinite(width) || !Float.isFinite(height) || width < 0.0f || height < 0.0f) {
            throw new IllegalArgumentException("Dimensions must be non-negative and finite");
        }
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public float scale() {
        return scale;
    }

    @Override
    public AbstractComponent scale(float scale) {
        if (!Float.isFinite(scale) || scale < 0.0f) {
            throw new IllegalArgumentException("Scale must be non-negative and finite");
        }
        this.scale = scale;
        return this;
    }

    @Override
    public float borderRound() {
        return borderRound;
    }

    @Override
    public AbstractComponent borderRound(float radius) {
        if (!Float.isFinite(radius) || radius < 0.0f) {
            throw new IllegalArgumentException("borderRound must be non-negative and finite");
        }
        this.borderRound = radius;
        return this;
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public AbstractComponent color(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public Color backgroundColor() {
        return backgroundColor;
    }

    @Override
    public AbstractComponent backgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    @Override
    public UiGradient gradient() {
        return gradient;
    }

    @Override
    public AbstractComponent gradient(UiGradient gradient) {
        this.gradient = gradient;
        return this;
    }

    @Override
    public float opacity() {
        return opacity;
    }

    @Override
    public AbstractComponent opacity(float opacity) {
        if (!Float.isFinite(opacity)) throw new IllegalArgumentException("opacity must be finite");
        this.opacity = Math.clamp(opacity, 0.0f, 1.0f);
        return this;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public AbstractComponent visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public Optional<Container> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public void animate(UiAnimation animation) {
        this.activeAnimation = Objects.requireNonNull(animation, "animation cannot be null");
    }

    @Override
    public void stopAnimation() {
        this.activeAnimation = null;
    }

    @Override
    public boolean isAnimating() {
        return activeAnimation != null;
    }

    @Override
    public Optional<UiAnimation> activeAnimation() {
        return Optional.ofNullable(activeAnimation);
    }

    @Override
    public float effectiveScale() {
        float parentScale = parent != null ? parent.effectiveScale() : 1.0f;
        return parentScale * this.scale;
    }

    @Override
    public float effectiveOpacity() {
        float parentOpacity = parent != null ? parent.effectiveOpacity() : 1.0f;
        return parentOpacity * this.opacity;
    }
}
