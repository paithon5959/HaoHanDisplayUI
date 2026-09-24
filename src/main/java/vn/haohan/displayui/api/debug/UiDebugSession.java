/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 *
 * HaoHanDisplayUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HaoHanDisplayUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HaoHanDisplayUI. If not, see <https://www.gnu.org/licenses/>.
 */
package vn.haohan.displayui.api.debug;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.layer.LayerManager;

/**
 * Represents an active, debuggable Display UI instance.
 * Allows inspector tools, hierarchy tree view, and live visibility toggles
 * to interact with the underlying {@link LayerManager}.
 */
public interface UiDebugSession {

    /**
     * The title or description of this UI session (e.g. "Demo UI", "Robot Dashboard").
     */
    default String name() {
        return "Display UI";
    }

    /**
     * Retrieves the current active {@link LayerManager} containing the UI hierarchy.
     */
    LayerManager getCurrentLayerManager();

    /**
     * Retrieves the {@link UiDebugState} attached to this UI.
     */
    UiDebugState getDebugState();

    /**
     * Re-renders and updates the display entities to reflect changes in debug state.
     */
    void forceUpdate();

    /**
     * Handles button click interception when Click-to-Inspect mode or synthetic hitboxes are active.
     *
     * @param player the clicking player
     * @param buttonId the clicked element ID
     * @return true if intercepted as a debug inspection action; false to continue normal click flow
     */
    default boolean handleInspectClick(Player player, String buttonId) {
        return handleInspectClick(player, buttonId, UiDebugInspector.DEFAULT_COMMAND_PREFIX);
    }

    /**
     * Handles button click interception with a custom command prefix for generated chat links.
     */
    default boolean handleInspectClick(Player player, String buttonId, String commandPrefix) {
        UiDebugState state = getDebugState();
        if (state == null || buttonId == null) return false;

        if (buttonId.startsWith("__inspect_") || state.isClickInspectEnabled()) {
            String targetId = buttonId;
            if (targetId.startsWith("__inspect_comp_")) {
                targetId = targetId.substring("__inspect_comp_".length());
            } else if (targetId.startsWith("__inspect_cont_")) {
                targetId = targetId.substring("__inspect_cont_".length());
            } else if (targetId.startsWith("__inspect_layer_")) {
                targetId = targetId.substring("__inspect_layer_".length());
            }

            if (player != null && player.isOnline()) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.9f, 1.8f);
                boolean found = UiDebugInspector.inspectAndSend(
                        player, getCurrentLayerManager(), targetId, null, state, commandPrefix);
                if (!found) {
                    UiDebugInspector.inspectAndSend(
                            player, getCurrentLayerManager(), buttonId, null, state, commandPrefix);
                }
            }
            return true;
        }
        return false;
    }
}
