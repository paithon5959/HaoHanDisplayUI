package vn.haohan.displayui.api;

import org.bukkit.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UiHandleTest {

    @Test
    @DisplayName("UiHandle.replace(LayerManager) compiles and calls replace(UiDocument)")
    void testReplaceLayerManagerDelegatesToReplaceDocument() {
        UiHandle handle = mock(UiHandle.class, CALLS_REAL_METHODS);

        LayerManager lm = new LayerManager();
        lm.createLayer("layer1", 0);

        handle.replace(lm);

        ArgumentCaptor<UiDocument> captor = ArgumentCaptor.forClass(UiDocument.class);
        verify(handle).replace(captor.capture());
        assertNotNull(captor.getValue());
        verify(handle, never()).animateNodes(any());
    }

    @Test
    @DisplayName("UiHandle.replace(LayerManager) triggers animateNodes when animations exist")
    void testReplaceLayerManagerAnimatesWhenPresent() {
        UiHandle handle = mock(UiHandle.class, CALLS_REAL_METHODS);

        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("layer1", 0);
        layer.backgroundColor(Color.BLACK);
        layer.animate(UiAnimation.fadeIn(5));

        handle.replace(lm);

        verify(handle).replace(any(UiDocument.class));
        verify(handle).animateNodes(anyList());
    }

    @Test
    @DisplayName("UiHandle.replace(Container) delegates through replace(LayerManager)")
    void testReplaceContainerDelegates() {
        UiHandle handle = mock(UiHandle.class, CALLS_REAL_METHODS);

        Container container = Container.builder("test_box").size(100, 100).build();
        handle.replace(container);

        verify(handle).replace(any(LayerManager.class));
        verify(handle).replace(any(UiDocument.class));
    }
}
