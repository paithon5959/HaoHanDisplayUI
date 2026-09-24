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
package vn.haohan.displayui.api;

import vn.haohan.displayui.api.icon.UiIconRegistry;
import vn.haohan.displayui.api.view.UiAudience;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface DisplayUiService {
    UiIconRegistry icons();
    vn.haohan.displayui.api.debug.UiDebugManager debug();
    UiHandle create(String ownerKey, Location origin, UiDocument document);
    UiHandle create(String ownerKey, Location origin, UiDocument document,
                    UiOptions options, UiAudience audience);

    default UiHandle create(String ownerKey, Location origin, vn.haohan.displayui.api.layer.LayerManager layerManager) {
        UiHandle handle = create(ownerKey, origin, vn.haohan.displayui.api.bridge.UiDocumentBridge.compile(layerManager));
        vn.haohan.displayui.api.bridge.UiDocumentBridge.bindInteractions(handle, layerManager);
        return handle;
    }

    default UiHandle create(String ownerKey, Location origin, vn.haohan.displayui.api.layer.LayerManager layerManager,
                            UiOptions options, UiAudience audience) {
        UiHandle handle = create(ownerKey, origin, vn.haohan.displayui.api.bridge.UiDocumentBridge.compile(layerManager), options, audience);
        vn.haohan.displayui.api.bridge.UiDocumentBridge.bindInteractions(handle, layerManager);
        return handle;
    }

    default UiHandle create(String ownerKey, Location origin, vn.haohan.displayui.api.container.Container container) {
        vn.haohan.displayui.api.layer.LayerManager manager = new vn.haohan.displayui.api.layer.LayerManager();
        manager.createLayer("root_layer", 0).addContainer(container);
        return create(ownerKey, origin, manager);
    }
    Optional<UiHandle> find(UUID id);
    Collection<UiHandle> active();
    int removeOwnedBy(String ownerKey);
}
