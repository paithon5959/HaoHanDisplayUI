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
package vn.haohan.displayui.api.container;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import vn.haohan.displayui.api.animation.Easings;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.*;
import java.util.function.Consumer;

/**
 * An interactive expandable/collapsible container that inherits from {@link Container}.
 *
 * <p>A {@code DropdownContainer} renders an interactive clickable header bar with a title and
 * expand/collapse indicator (e.g. {@code ▶} / {@code ▼}). It hosts nested child {@link Container}s
 * that are hidden when collapsed.
 *
 * <p>When clicked or toggled open, it plays an animation (e.g. {@link DropdownAnimationType#SLIDE_AND_FADE})
 * that pushes out the nested child containers and dynamically expands the overall container height.
 */
public class DropdownContainer extends Container {

    private float headerHeight = 18.0f;
    private Component headerTitle;
    private Color headerBackgroundColor = Color.fromARGB(220, 32, 38, 50);
    private Color headerExpandedBackgroundColor = Color.fromARGB(240, 42, 52, 70);
    private float headerBorderRound = 3.0f;
    private float headerTitleFontSize = 5.0f;
    private String indicatorCollapsed = "▶";
    private String indicatorExpanded = "▼";
    private TextColor indicatorColor = NamedTextColor.GOLD;

    private boolean expanded = false;
    private DropdownAnimationType animationType = DropdownAnimationType.SLIDE_AND_FADE;
    private int animationDurationTicks = 8;
    private Easings animationEasing = Easings.OutCubic;
    private float slideDistance = 12.0f;
    private UiAnimation customExpandAnimation;

    private boolean autoLayout = true;
    private float itemSpacing = 3.0f;
    private float contentPadding = 4.0f;
    private Color contentBackgroundColor = Color.fromARGB(140, 20, 24, 32);
    private float contentBorderRound = 3.0f;

    private boolean clampToParent = true;
    private Float maxHeight = null;

    private final Container contentContainer;
    private final List<Consumer<Boolean>> toggleListeners = new ArrayList<>();

    public DropdownContainer(String id) {
        super(id);
        this.contentContainer = new Container(id + "_content");
        this.contentContainer.anchor(UiAnchorPoint.TOP_LEFT);
        this.contentContainer.origin(UiAnchorPoint.TOP_LEFT);
        this.contentContainer.visible(false);
        super.addContainer(contentContainer);
    }

    @Override
    public void setParentContainer(Container parent) {
        super.setParentContainer(parent);
        refreshLayout();
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    // --- Header Configuration ---

    public float headerHeight() {
        return headerHeight;
    }

    public DropdownContainer headerHeight(float headerHeight) {
        if (!Float.isFinite(headerHeight) || headerHeight <= 0.0f) {
            throw new IllegalArgumentException("headerHeight must be positive and finite");
        }
        this.headerHeight = headerHeight;
        refreshLayout();
        return this;
    }

    public Component headerTitle() {
        return headerTitle != null ? headerTitle : Component.text(id());
    }

    public DropdownContainer headerTitle(Component headerTitle) {
        this.headerTitle = headerTitle;
        return this;
    }

    public DropdownContainer headerTitle(String title) {
        this.headerTitle = title != null ? Component.text(title, NamedTextColor.WHITE, TextDecoration.BOLD) : null;
        return this;
    }

    public Color headerBackgroundColor() {
        return headerBackgroundColor;
    }

    public DropdownContainer headerBackgroundColor(Color headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
        return this;
    }

    public Color headerExpandedBackgroundColor() {
        return headerExpandedBackgroundColor != null ? headerExpandedBackgroundColor : headerBackgroundColor;
    }

    public DropdownContainer headerExpandedBackgroundColor(Color color) {
        this.headerExpandedBackgroundColor = color;
        return this;
    }

    public float headerBorderRound() {
        return headerBorderRound;
    }

    public DropdownContainer headerBorderRound(float radius) {
        this.headerBorderRound = Math.max(0.0f, radius);
        return this;
    }

    public float headerTitleFontSize() {
        return headerTitleFontSize;
    }

    public DropdownContainer headerTitleFontSize(float fontSize) {
        this.headerTitleFontSize = fontSize;
        return this;
    }

    public String indicatorCollapsed() {
        return indicatorCollapsed;
    }

    public DropdownContainer indicatorCollapsed(String indicator) {
        this.indicatorCollapsed = Objects.requireNonNull(indicator, "indicatorCollapsed cannot be null");
        return this;
    }

    public String indicatorExpanded() {
        return indicatorExpanded;
    }

    public DropdownContainer indicatorExpanded(String indicator) {
        this.indicatorExpanded = Objects.requireNonNull(indicator, "indicatorExpanded cannot be null");
        return this;
    }

    public TextColor indicatorColor() {
        return indicatorColor;
    }

    public DropdownContainer indicatorColor(TextColor color) {
        this.indicatorColor = color;
        return this;
    }

    /**
     * The ID of the clickable button spanning the header area.
     */
    public String toggleButtonId() {
        return id() + "_toggle";
    }

    // --- Expanded State & Height Computation ---

    public boolean isExpanded() {
        return expanded;
    }

    public DropdownContainer expanded(boolean expanded) {
        if (this.expanded != expanded) {
            if (expanded) {
                expand();
            } else {
                collapse();
            }
        }
        return this;
    }

    /**
     * Toggles the dropdown open or closed with animation.
     */
    public void toggle() {
        if (expanded) {
            collapse();
        } else {
            expand();
        }
    }

    /**
     * Expands the dropdown, pushing out nested child containers with animation.
     */
    public void expand() {
        this.expanded = true;
        this.contentContainer.visible(true);
        refreshLayout();

        // Apply expand animation cascading to all nested child items
        UiAnimation anim = (animationType == DropdownAnimationType.CUSTOM && customExpandAnimation != null)
                ? customExpandAnimation
                : animationType.createExpandAnimation(animationDurationTicks, animationEasing, slideDistance);

        this.contentContainer.animate(anim);

        notifyToggle(true);
        update();
    }

    /**
     * Collapses the dropdown, hiding child containers.
     */
    public void collapse() {
        this.expanded = false;
        this.contentContainer.visible(false);
        this.contentContainer.stopAnimation();

        notifyToggle(false);
        update();
    }

    @Override
    public float height() {
        if (!expanded) {
            return headerHeight;
        }
        float contentH = calculateContentHeight();
        float totalH = headerHeight + itemSpacing + contentH + (contentBackgroundColor != null ? contentPadding * 2.0f : 0.0f);
        if (maxHeight != null && maxHeight > 0.0f) {
            totalH = Math.min(totalH, maxHeight);
        }
        if (clampToParent && parentContainer().isPresent()) {
            float parentH = parentContainer().get().height();
            if (parentH > 0.0f) {
                float availableH = Math.max(headerHeight, parentH - y());
                totalH = Math.min(totalH, availableH);
            }
        }
        return totalH;
    }

    /**
     * Effective height computed identically to {@link #height()}.
     */
    public float effectiveHeight() {
        return height();
    }

    public boolean isClampToParent() {
        return clampToParent;
    }

    public DropdownContainer clampToParent(boolean clampToParent) {
        this.clampToParent = clampToParent;
        refreshLayout();
        return this;
    }

    public Optional<Float> maxHeight() {
        return Optional.ofNullable(maxHeight);
    }

    public DropdownContainer maxHeight(Float maxHeight) {
        if (maxHeight != null && (!Float.isFinite(maxHeight) || maxHeight < 0.0f)) {
            throw new IllegalArgumentException("maxHeight must be positive and finite");
        }
        this.maxHeight = maxHeight;
        refreshLayout();
        return this;
    }

    // --- Dropdown Items Management ---

    /**
     * Adds a child container nested inside this dropdown's content area.
     */
    public DropdownContainer addDropdownItem(Container item) {
        Objects.requireNonNull(item, "dropdown item container cannot be null");
        contentContainer.addContainer(item);
        refreshLayout();
        return this;
    }

    /**
     * Overrides {@link #addContainer(Container)} to route child containers into the nested content area,
     * unless the child is the internal {@code contentContainer}.
     */
    @Override
    public Container addContainer(Container child) {
        if (child == contentContainer) {
            return super.addContainer(child);
        }
        return addDropdownItem(child);
    }

    public Collection<Container> dropdownItems() {
        return contentContainer.childContainers();
    }

    public Optional<Container> getDropdownItem(String itemId) {
        return contentContainer.getContainer(itemId);
    }

    public Optional<Container> removeDropdownItem(String itemId) {
        Optional<Container> removed = contentContainer.removeContainer(itemId);
        refreshLayout();
        return removed;
    }

    public void clearDropdownItems() {
        contentContainer.clearContainers();
        refreshLayout();
    }

    public Container contentContainer() {
        return contentContainer;
    }

    // --- Layout & Styling ---

    public boolean isAutoLayout() {
        return autoLayout;
    }

    public DropdownContainer autoLayout(boolean autoLayout) {
        this.autoLayout = autoLayout;
        refreshLayout();
        return this;
    }

    public float itemSpacing() {
        return itemSpacing;
    }

    public DropdownContainer itemSpacing(float itemSpacing) {
        this.itemSpacing = Math.max(0.0f, itemSpacing);
        refreshLayout();
        return this;
    }

    public float contentPadding() {
        return contentPadding;
    }

    public DropdownContainer contentPadding(float contentPadding) {
        this.contentPadding = Math.max(0.0f, contentPadding);
        refreshLayout();
        return this;
    }

    public Color contentBackgroundColor() {
        return contentBackgroundColor;
    }

    public DropdownContainer contentBackgroundColor(Color color) {
        this.contentBackgroundColor = color;
        return this;
    }

    public float contentBorderRound() {
        return contentBorderRound;
    }

    public DropdownContainer contentBorderRound(float radius) {
        this.contentBorderRound = Math.max(0.0f, radius);
        return this;
    }

    // --- Animation Settings ---

    public DropdownAnimationType animationType() {
        return animationType;
    }

    public DropdownContainer animationType(DropdownAnimationType animationType) {
        this.animationType = Objects.requireNonNull(animationType, "animationType cannot be null");
        return this;
    }

    public int animationDurationTicks() {
        return animationDurationTicks;
    }

    public DropdownContainer animationDurationTicks(int ticks) {
        this.animationDurationTicks = Math.max(1, ticks);
        return this;
    }

    public Easings animationEasing() {
        return animationEasing;
    }

    public DropdownContainer animationEasing(Easings easing) {
        this.animationEasing = Objects.requireNonNull(easing, "easing cannot be null");
        return this;
    }

    public float slideDistance() {
        return slideDistance;
    }

    public DropdownContainer slideDistance(float distance) {
        this.slideDistance = distance;
        return this;
    }

    public UiAnimation customExpandAnimation() {
        return customExpandAnimation;
    }

    public DropdownContainer customExpandAnimation(UiAnimation customExpandAnimation) {
        this.customExpandAnimation = customExpandAnimation;
        return this;
    }

    // --- Listeners ---

    public DropdownContainer onToggle(Consumer<Boolean> listener) {
        toggleListeners.add(Objects.requireNonNull(listener, "listener cannot be null"));
        return this;
    }

    private void notifyToggle(boolean isNowExpanded) {
        for (Consumer<Boolean> listener : toggleListeners) {
            listener.accept(isNowExpanded);
        }
    }

    // --- Internal Layout Recalculation ---

    public void refreshLayout() {
        if (contentContainer == null) return;

        float containerW = width();
        float contentW = Math.max(1.0f, containerW);

        // Respect parent width limits if clamped
        if (clampToParent && parentContainer().isPresent()) {
            float parentW = parentContainer().get().width();
            if (parentW > 0.0f) {
                contentW = Math.min(contentW, Math.max(1.0f, parentW - x()));
            }
        }

        contentContainer.offset(0.0f, headerHeight + itemSpacing);
        contentContainer.borderRound(contentBorderRound);
        contentContainer.backgroundColor(contentBackgroundColor);

        float maxAllowedContentH = Float.MAX_VALUE;
        float paddingAllowance = (contentBackgroundColor != null ? contentPadding * 2.0f : 0.0f);
        if (maxHeight != null && maxHeight > 0.0f) {
            maxAllowedContentH = Math.max(0.0f, maxHeight - headerHeight - itemSpacing - paddingAllowance);
        }
        if (clampToParent && parentContainer().isPresent()) {
            float parentH = parentContainer().get().height();
            if (parentH > 0.0f) {
                float availableTotalH = Math.max(headerHeight, parentH - y());
                float allowedFromParent = Math.max(0.0f, availableTotalH - headerHeight - itemSpacing - paddingAllowance);
                maxAllowedContentH = Math.min(maxAllowedContentH, allowedFromParent);
            }
        }

        if (!autoLayout) {
            float totalH = calculateContentHeight();
            if (maxAllowedContentH < Float.MAX_VALUE) {
                totalH = Math.min(totalH, maxAllowedContentH);
            }
            contentContainer.size(contentW, totalH);
            return;
        }

        float currentY = contentBackgroundColor != null ? contentPadding : 0.0f;
        float innerW = contentBackgroundColor != null ? Math.max(1.0f, contentW - contentPadding * 2.0f) : contentW;

        for (Container child : contentContainer.childContainers()) {
            if (maxAllowedContentH < Float.MAX_VALUE && currentY >= maxAllowedContentH) {
                child.visible(false);
                continue;
            }

            child.anchor(UiAnchorPoint.TOP_LEFT);
            child.origin(UiAnchorPoint.TOP_LEFT);
            child.offset(contentBackgroundColor != null ? contentPadding : 0.0f, currentY);
            if (child.width() <= 0.0f || child.width() > innerW) {
                child.size(innerW, child.height());
            }

            if (maxAllowedContentH < Float.MAX_VALUE && (currentY + child.height()) > maxAllowedContentH) {
                float clampedH = Math.max(1.0f, maxAllowedContentH - currentY);
                child.size(child.width(), clampedH);
            }

            child.visible(expanded && this.isVisible());
            currentY += child.height() + itemSpacing;
        }

        if (!contentContainer.childContainers().isEmpty()) {
            currentY -= itemSpacing; // remove trailing spacing
        }
        if (contentBackgroundColor != null) {
            currentY += contentPadding;
        }

        float finalContentH = Math.max(1.0f, currentY);
        if (maxAllowedContentH < Float.MAX_VALUE) {
            finalContentH = Math.min(finalContentH, Math.max(1.0f, maxAllowedContentH + paddingAllowance));
        }

        contentContainer.size(contentW, finalContentH);
    }

    public float calculateContentHeight() {
        if (contentContainer == null) return 0.0f;
        if (!autoLayout) {
            float maxBottom = 0.0f;
            for (Container child : contentContainer.childContainers()) {
                maxBottom = Math.max(maxBottom, child.y() + child.height());
            }
            return maxBottom;
        }

        float total = 0.0f;
        int count = 0;
        for (Container child : contentContainer.childContainers()) {
            total += child.height();
            count++;
        }
        if (count > 1) {
            total += (count - 1) * itemSpacing;
        }
        return total;
    }

    // --- Fluent Builder ---

    public static class Builder extends Container.Builder {
        private float headerHeight = 18.0f;
        private Component headerTitle;
        private Color headerBackgroundColor = Color.fromARGB(220, 32, 38, 50);
        private Color headerExpandedBackgroundColor = Color.fromARGB(240, 42, 52, 70);
        private float headerBorderRound = 3.0f;
        private float headerTitleFontSize = 5.0f;
        private String indicatorCollapsed = "▶";
        private String indicatorExpanded = "▼";
        private TextColor indicatorColor = NamedTextColor.GOLD;

        private boolean expanded = false;
        private DropdownAnimationType animationType = DropdownAnimationType.SLIDE_AND_FADE;
        private int animationDurationTicks = 8;
        private Easings animationEasing = Easings.OutCubic;
        private float slideDistance = 12.0f;
        private UiAnimation customExpandAnimation;

        private boolean autoLayout = true;
        private float itemSpacing = 3.0f;
        private float contentPadding = 4.0f;
        private Color contentBackgroundColor = Color.fromARGB(140, 20, 24, 32);
        private float contentBorderRound = 3.0f;

        private boolean clampToParent = true;
        private Float maxHeight = null;

        public Builder(String id) {
            super(id);
            this.anchor = UiAnchorPoint.TOP_LEFT;
            this.origin = UiAnchorPoint.TOP_LEFT;
            this.width = 174.0f;
        }

        @Override
        public Builder anchor(UiAnchorPoint anchor) { super.anchor(anchor); return this; }
        @Override
        public Builder origin(UiAnchorPoint origin) { super.origin(origin); return this; }
        @Override
        public Builder offset(float x, float y) { super.offset(x, y); return this; }
        @Override
        public Builder size(float width, float height) { super.size(width, height); return this; }
        public Builder width(float width) { this.width = width; return this; }
        @Override
        public Builder scale(float scale) { super.scale(scale); return this; }
        @Override
        public Builder opacity(float opacity) { super.opacity(opacity); return this; }
        @Override
        public Builder borderRound(float borderRound) { super.borderRound(borderRound); return this; }
        @Override
        public Builder backgroundColor(Color backgroundColor) { super.backgroundColor(backgroundColor); return this; }
        @Override
        public Builder gradient(vn.haohan.displayui.api.gradient.UiGradient gradient) { super.gradient(gradient); return this; }
        @Override
        public Builder visible(boolean visible) { super.visible(visible); return this; }

        public Builder headerHeight(float headerHeight) { this.headerHeight = headerHeight; return this; }
        public Builder headerTitle(Component headerTitle) { this.headerTitle = headerTitle; return this; }
        public Builder headerTitle(String headerTitle) {
            this.headerTitle = headerTitle != null ? Component.text(headerTitle, NamedTextColor.WHITE, TextDecoration.BOLD) : null;
            return this;
        }
        public Builder headerBackgroundColor(Color color) { this.headerBackgroundColor = color; return this; }
        public Builder headerExpandedBackgroundColor(Color color) { this.headerExpandedBackgroundColor = color; return this; }
        public Builder headerBorderRound(float radius) { this.headerBorderRound = radius; return this; }
        public Builder headerTitleFontSize(float fontSize) { this.headerTitleFontSize = fontSize; return this; }
        public Builder indicatorCollapsed(String indicator) { this.indicatorCollapsed = indicator; return this; }
        public Builder indicatorExpanded(String indicator) { this.indicatorExpanded = indicator; return this; }
        public Builder indicatorColor(TextColor color) { this.indicatorColor = color; return this; }

        public Builder expanded(boolean expanded) { this.expanded = expanded; return this; }
        public Builder animationType(DropdownAnimationType animationType) { this.animationType = animationType; return this; }
        public Builder animationDurationTicks(int ticks) { this.animationDurationTicks = ticks; return this; }
        public Builder animationEasing(Easings easing) { this.animationEasing = easing; return this; }
        public Builder slideDistance(float distance) { this.slideDistance = distance; return this; }
        public Builder customExpandAnimation(UiAnimation animation) { this.customExpandAnimation = animation; return this; }

        public Builder autoLayout(boolean autoLayout) { this.autoLayout = autoLayout; return this; }
        public Builder itemSpacing(float itemSpacing) { this.itemSpacing = itemSpacing; return this; }
        public Builder contentPadding(float contentPadding) { this.contentPadding = contentPadding; return this; }
        public Builder contentBackgroundColor(Color color) { this.contentBackgroundColor = color; return this; }
        public Builder contentBorderRound(float radius) { this.contentBorderRound = radius; return this; }

        public Builder clampToParent(boolean clampToParent) { this.clampToParent = clampToParent; return this; }
        public Builder maxHeight(Float maxHeight) { this.maxHeight = maxHeight; return this; }

        @Override
        public DropdownContainer build() {
            DropdownContainer dropdown = new DropdownContainer(id);
            dropdown.anchor(anchor);
            dropdown.origin(origin);
            dropdown.offset(x, y);
            dropdown.size(width, headerHeight); // Base size
            dropdown.scale(scale);
            dropdown.opacity(opacity);
            dropdown.borderRound(borderRound);
            dropdown.backgroundColor(backgroundColor);
            dropdown.gradient(gradient);
            dropdown.visible(visible);

            dropdown.clampToParent(clampToParent);
            if (maxHeight != null) dropdown.maxHeight(maxHeight);

            dropdown.headerHeight(headerHeight);
            if (headerTitle != null) dropdown.headerTitle(headerTitle);
            dropdown.headerBackgroundColor(headerBackgroundColor);
            dropdown.headerExpandedBackgroundColor(headerExpandedBackgroundColor);
            dropdown.headerBorderRound(headerBorderRound);
            dropdown.headerTitleFontSize(headerTitleFontSize);
            dropdown.indicatorCollapsed(indicatorCollapsed);
            dropdown.indicatorExpanded(indicatorExpanded);
            dropdown.indicatorColor(indicatorColor);

            dropdown.animationType(animationType);
            dropdown.animationDurationTicks(animationDurationTicks);
            dropdown.animationEasing(animationEasing);
            dropdown.slideDistance(slideDistance);
            dropdown.customExpandAnimation(customExpandAnimation);

            dropdown.autoLayout(autoLayout);
            dropdown.itemSpacing(itemSpacing);
            dropdown.contentPadding(contentPadding);
            dropdown.contentBackgroundColor(contentBackgroundColor);
            dropdown.contentBorderRound(contentBorderRound);

            if (expanded) {
                dropdown.expand();
            } else {
                dropdown.collapse();
            }

            return dropdown;
        }
    }
}
