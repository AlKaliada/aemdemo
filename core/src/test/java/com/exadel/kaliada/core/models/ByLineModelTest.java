package com.exadel.kaliada.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ByLineModelTest {

    private final AemContext aemContext = new AemContext();
    private ByLineModel byLineModel;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(ByLineModel.class);
        aemContext.load().json("/byline.json", "/component");
    }

    @Test
    void getName() {
        aemContext.currentResource("/component/byline");
        byLineModel = aemContext.request().adaptTo(ByLineModel.class);
        assertEquals("Plane", byLineModel.getName());
    }

    @Test
    void getOccupations() {
        aemContext.currentResource("/component/byline");
        byLineModel = aemContext.request().adaptTo(ByLineModel.class);
        assertEquals(List.of("Dream", "Fly"), byLineModel.getOccupations());
    }

    @Test
    void isEmpty() {
    }
}