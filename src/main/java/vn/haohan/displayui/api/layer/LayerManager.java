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

import java.util.*;

/**
 * Manages the ordered stack of UI layers, ensuring visual clarity,
 * isolated component ownership, and zero Z-fighting depth separation.
 */
public class LayerManager {

    private final Map<String, Layer> layers = new LinkedHashMap<>();

    public LayerManager() {
    }

    /**
     * Creates and registers a new layer with the specified Z-index.
     */
    public Layer createLayer(String id, int zIndex) {
        Objects.requireNonNull(id, "Layer id cannot be null");
        Layer layer = new Layer(id, zIndex);
        layers.put(id, layer);
        return layer;
    }

    public Optional<Layer> getLayer(String id) {
        return Optional.ofNullable(layers.get(id));
    }

    public boolean hasLayer(String id) {
        return layers.containsKey(id);
    }

    public Optional<Layer> removeLayer(String id) {
        return Optional.ofNullable(layers.remove(id));
    }

    /**
     * Returns all layers sorted in ascending order of their Z-index (back to front).
     */
    public List<Layer> layers() {
        List<Layer> list = new ArrayList<>(layers.values());
        list.sort(Comparator.comparingInt(Layer::zIndex));
        return Collections.unmodifiableList(list);
    }

    public int layerCount() {
        return layers.size();
    }

    public void clear() {
        layers.clear();
    }
}
