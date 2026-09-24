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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active {@link UiDebugSession} instances for players across the server,
 * enabling centralized debugging from console or in-game commands.
 */
public class UiDebugManager {

    private final Map<UUID, UiDebugSession> activeSessions = new ConcurrentHashMap<>();

    public UiDebugManager() {
    }

    /**
     * Registers an active UI debug session for a player.
     */
    public void registerSession(UUID playerId, UiDebugSession session) {
        Objects.requireNonNull(playerId, "playerId cannot be null");
        Objects.requireNonNull(session, "session cannot be null");
        activeSessions.put(playerId, session);
    }

    /**
     * Unregisters the debug session for a player.
     */
    public void unregisterSession(UUID playerId) {
        if (playerId != null) {
            activeSessions.remove(playerId);
        }
    }

    /**
     * Retrieves the active debug session for a player if one is registered.
     */
    public Optional<UiDebugSession> getSession(UUID playerId) {
        if (playerId == null) return Optional.empty();
        return Optional.ofNullable(activeSessions.get(playerId));
    }

    /**
     * Returns all player UUIDs currently associated with an active debug session.
     */
    public Collection<UUID> getActiveSessionPlayerIds() {
        return activeSessions.keySet();
    }

    /**
     * Clears all active debug sessions.
     */
    public void clear() {
        activeSessions.clear();
    }
}
