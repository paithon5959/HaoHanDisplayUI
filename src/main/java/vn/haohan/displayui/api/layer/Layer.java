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
package vn.haohan.displayui.api.layer;

import org.bukkit.Color;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.gradient.UiGradient;

import java.util.*;

/**
 * Represents an isolated visual layer in the UI stack.
 *
 * <p>Each layer is allocated a guaranteed distinct base world-depth slot,
 * preventing any Z-fighting and visual flickering between layers and between
 * containers within the same layer.</p>
 */
public class Layer {

    /** Default depth separation between consecutive layers in world blocks. */
    public static final float LAYER_DEPTH_STEP = 0.020f;
    /** Sub-depth separation between elements within the same layer. */
    public static final float ELEMENT_DEPTH_STEP = 0.0005f;

    private final String id;
    private int zIndex;
    private float baseDepth;
    private boolean visible = true;
    private float x = -96.0f;
    private float y = -64.0f;
    private float width = 192.0f;
    private float height = 128.0f;
    private boolean doubleSided = false;
    private boolean hasExplicitBounds = false;
    private Color backgroundColor;
    private UiGradient gradient;
    private float borderRound = 0.0f;
    private UiAnimation activeAnimation;

    private final Map<String, Container> rootContainers = new LinkedHashMap<>();

    public Layer(String id, int zIndex) {
        this.id = Objects.requireNonNull(id, "Layer id cannot be null");
        this.zIndex = zIndex;
        this.baseDepth = zIndex * LAYER_DEPTH_STEP;
    }

    public String id() {
        return id;
    }

    public int zIndex() {
        return zIndex;
    }

    public Layer zIndex(int zIndex) {
        this.zIndex = zIndex;
        this.baseDepth = zIndex * LAYER_DEPTH_STEP;
        return this;
    }

    public float baseDepth() {
        return baseDepth;
    }

    public Layer baseDepth(float baseDepth) {
        this.baseDepth = baseDepth;
        return this;
    }

    /**
     * Allocates a non-colliding depth for an element inside this layer, guaranteeing zero Z-fighting.
     *
     * @param elementIndex the sequential index of the element
     * @return unique world-Z depth
     */
    public float allocateDepth(int elementIndex) {
        return baseDepth + (Math.max(0, elementIndex) * ELEMENT_DEPTH_STEP);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public boolean hasExplicitBounds() {
        return hasExplicitBounds;
    }

    public Layer bounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hasExplicitBounds = true;
        return this;
    }

    public boolean doubleSided() {
        return doubleSided;
    }

    public Layer doubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public Layer visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public Color backgroundColor() {
        return backgroundColor;
    }

    public Layer backgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public UiGradient gradient() {
        return gradient;
    }

    public Layer gradient(UiGradient gradient) {
        this.gradient = gradient;
        return this;
    }

    public float borderRound() {
        return borderRound;
    }

    public Layer borderRound(float radius) {
        this.borderRound = radius;
        return this;
    }

    public Layer addContainer(Container container) {
        Objects.requireNonNull(container, "container cannot be null");
        container.setLayer(this);
        rootContainers.put(container.id(), container);
        return this;
    }

    public Optional<Container> removeContainer(String containerId) {
        Container removed = rootContainers.remove(containerId);
        if (removed != null) {
            removed.setLayer(null);
        }
        return Optional.ofNullable(removed);
    }

    public Optional<Container> getContainer(String containerId) {
        return Optional.ofNullable(rootContainers.get(containerId));
    }

    public Collection<Container> containers() {
        return Collections.unmodifiableCollection(rootContainers.values());
    }

    public void clearContainers() {
        for (Container c : rootContainers.values()) {
            c.setLayer(null);
        }
        rootContainers.clear();
    }

    public void animate(UiAnimation animation) {
        this.activeAnimation = Objects.requireNonNull(animation, "animation cannot be null");
    }

    public void stopAnimation() {
        this.activeAnimation = null;
    }

    public boolean isAnimating() {
        return activeAnimation != null;
    }

    public Optional<UiAnimation> activeAnimation() {
        return Optional.ofNullable(activeAnimation);
    }
}
