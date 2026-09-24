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
package vn.haohan.displayui.demo;

import org.bukkit.entity.Player;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.interaction.UiControlChange;

/**
 * Contract for demo presentation pages built purely on the Container and Component architecture.
 */
public interface DemoPage {

    /** The display title shown in the demo dialog header. */
    String title();

    /**
     * Populates the page content container with components and child containers.
     *
     * @param container the content container for this demo page
     * @param context the demo execution context
     */
    void build(Container container, DemoContext context);

    default boolean onClick(DemoContext context, String buttonId, Player player) {
        return false;
    }

    default void onControlChange(DemoContext context, UiControlChange change) {}

    default void onShow(DemoContext context) {}

    default void onTick(DemoContext context) {}
}
