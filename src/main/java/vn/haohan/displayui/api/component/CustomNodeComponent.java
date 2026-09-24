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

import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.node.UiNode;

import java.util.Objects;

/**
 * Universal component that encapsulates any low-level {@link UiNode} (such as 3D entities, mobs,
 * lines, or custom geometry) within the Container and Layer hierarchy.
 * Automatically resolves positions via Anchor/Origin and receives anti-flicker depth allocation.
 */
public class CustomNodeComponent extends AbstractComponent {

    @FunctionalInterface
    public interface NodeFactory {
        UiNode create(float x, float y, float width, float height, float depth, float scale);
    }

    private NodeFactory factory;

    public CustomNodeComponent(String id) {
        super(id);
    }

    public CustomNodeComponent(String id, NodeFactory factory) {
        super(id);
        this.factory = Objects.requireNonNull(factory, "factory cannot be null");
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public NodeFactory factory() {
        return factory;
    }

    public CustomNodeComponent factory(NodeFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory cannot be null");
        return this;
    }

    public UiNode instantiate(float x, float y, float width, float height, float depth, float scale) {
        if (factory == null) return null;
        return factory.create(x, y, width, height, depth, scale);
    }

    public static final class Builder {
        private final String id;
        private NodeFactory factory;
        private UiAnchorPoint anchor = UiAnchorPoint.CENTER;
        private UiAnchorPoint origin = UiAnchorPoint.CENTER;
        private float x = 0.0f;
        private float y = 0.0f;
        private float width = 20.0f;
        private float height = 20.0f;
        private float scale = 1.0f;

        public Builder(String id) {
            this.id = id;
        }

        public Builder factory(NodeFactory factory) {
            this.factory = factory;
            return this;
        }

        public Builder node(UiNode staticNode) {
            this.factory = (x, y, w, h, d, s) -> staticNode;
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

        public CustomNodeComponent build() {
            CustomNodeComponent comp = new CustomNodeComponent(id, factory);
            comp.anchor(anchor);
            comp.origin(origin);
            comp.offset(x, y);
            comp.size(width, height);
            comp.scale(scale);
            return comp;
        }
    }
}
