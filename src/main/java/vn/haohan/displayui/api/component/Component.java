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
import vn.haohan.displayui.api.layout.UiRect;

import java.util.Optional;

/**
 * Base contract for all renderable and interactive UI elements in the HaoHanDisplayUI 2.0 hierarchy.
 *
 * <p>Every component is positioned relative to its parent {@link Container} using:
 * <ul>
 *   <li>{@link #anchor()}: Reference spawn point on the parent container (9 directions)</li>
 *   <li>{@link #origin()}: Pivot touch point on the component itself (9 directions)</li>
 *   <li>{@link #x()}, {@link #y()}: Local pixel offsets relative to the anchor</li>
 * </ul>
 *
 * <p>Components support independent animation, container animation inheritance,
 * local scaling, corner rounding ({@code borderRound}), and color/gradient styling.</p>
 */
public interface Component {

    /**
     * Unique identifier of this component within its container.
     */
    String id();

    /**
     * Anchor point on the parent container.
     */
    UiAnchorPoint anchor();

    /**
     * Sets the anchor point on the parent container.
     */
    Component anchor(UiAnchorPoint anchor);

    /**
     * Origin pivot point on this component.
     */
    UiAnchorPoint origin();

    /**
     * Sets the origin pivot point on this component.
     */
    Component origin(UiAnchorPoint origin);

    /**
     * Local horizontal offset in pixels.
     */
    float x();

    /**
     * Local vertical offset in pixels.
     */
    float y();

    /**
     * Sets the local pixel offset.
     */
    Component offset(float x, float y);

    /**
     * Logical pixel width of the component.
     */
    float width();

    /**
     * Logical pixel height of the component.
     */
    float height();

    /**
     * Sets the component dimensions.
     */
    Component size(float width, float height);

    /**
     * Local scale factor of this component (default 1.0).
     */
    float scale();

    /**
     * Sets the local scale factor of this component.
     */
    Component scale(float scale);

    /**
     * Corner radius in pixels for rounded rect visuals (0 for sharp corners).
     */
    float borderRound();

    /**
     * Sets the corner radius in pixels.
     */
    Component borderRound(float radius);

    /**
     * Primary color or foreground tint of this component.
     */
    Color color();

    /**
     * Sets the primary color or foreground tint.
     */
    Component color(Color color);

    /**
     * Background color of this component, if any.
     */
    Color backgroundColor();

    /**
     * Sets the solid background color.
     */
    Component backgroundColor(Color color);

    /**
     * Background gradient of this component, if any.
     */
    UiGradient gradient();

    /**
     * Sets the background gradient.
     */
    Component gradient(UiGradient gradient);

    /**
     * Component opacity factor between 0.0 (transparent) and 1.0 (opaque).
     */
    float opacity();

    /**
     * Sets the opacity factor.
     */
    Component opacity(float opacity);

    /**
     * Whether this component is visible and active.
     */
    boolean isVisible();

    /**
     * Sets whether this component is visible.
     */
    Component visible(boolean visible);

    /**
     * Returns the parent container owning this component, if attached.
     */
    Optional<Container> parent();

    /**
     * Attaches this component to a parent container.
     */
    void setParent(Container parent);

    /**
     * Starts an independent animation on this component.
     */
    void animate(UiAnimation animation);

    /**
     * Stops the independent animation running on this component.
     */
    void stopAnimation();

    /**
     * Returns whether an independent animation is actively running on this component.
     */
    boolean isAnimating();

    /**
     * Returns the active independent animation, if running.
     */
    Optional<UiAnimation> activeAnimation();

    /**
     * Calculates the resolved top-left coordinate [left, top] in parent container coordinate space.
     *
     * @param parentWidth  the width of the parent container
     * @param parentHeight the height of the parent container
     * @return float array containing [left, top]
     */
    default float[] computePosition(float parentWidth, float parentHeight) {
        return anchor().resolve(parentWidth, parentHeight, x(), y(), width(), height(), origin());
    }

    /**
     * Computes the bounding rectangle of this component in parent container space.
     *
     * @param parentWidth  the width of the parent container
     * @param parentHeight the height of the parent container
     * @return {@link UiRect} representing bounds
     */
    default UiRect computeBounds(float parentWidth, float parentHeight) {
        float[] pos = computePosition(parentWidth, parentHeight);
        return new UiRect(pos[0], pos[1], width() * effectiveScale(), height() * effectiveScale());
    }

    /**
     * Calculates the effective scale factor, multiplying parent container scale (if attached)
     * by this component's local scale.
     */
    float effectiveScale();

    /**
     * Calculates the effective opacity factor, multiplying parent container opacity
     * by this component's local opacity.
     */
    float effectiveOpacity();
}
