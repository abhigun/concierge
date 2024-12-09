package com.grookage.concierge.core.engine;

import com.grookage.concierge.core.engine.processors.RejectConfigProcessor;
import com.grookage.concierge.core.utils.ContextUtils;
import com.grookage.concierge.models.ResourceHelper;
import com.grookage.concierge.models.config.ConfigDetails;
import com.grookage.concierge.models.config.ConfigKey;
import com.grookage.concierge.models.config.ConfigState;
import com.grookage.concierge.models.exception.ConciergeException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

class RejectConfigProcessorTest extends AbstractProcessorTest {
    @Override
    public ConciergeProcessor createConciergeProcessor() {
        return new RejectConfigProcessor(this::getConciergeRepository);
    }

    @Test
    @SneakyThrows
    void testRejectConfigNotInCreatedState() {
        final var conciergeContext = new ConciergeContext();
        final var configKey = ResourceHelper.getResource("configKey.json",
                ConfigKey.class);
        conciergeContext.addContext(ConfigKey.class.getSimpleName(), configKey);
        ContextUtils.addSchemaUpdaterContext(conciergeContext, getConfigUpdater());
        Mockito.when(getConciergeRepository().getStoredRecord(configKey))
                .thenReturn(Optional.empty());
        final var processor = getConciergeProcessor();
        Assertions.assertThrows(ConciergeException.class, () -> processor.process(conciergeContext));
        final var configDetails = ResourceHelper.getResource("configDetails.json", ConfigDetails.class);
        configDetails.setConfigState(ConfigState.ROLLED);
        Mockito.when(getConciergeRepository().getStoredRecord(configKey))
                .thenReturn(Optional.of(configDetails));
        Assertions.assertThrows(ConciergeException.class, () -> processor.process(conciergeContext));
        Mockito.verify(getConciergeRepository(), Mockito.times(0)).update(configDetails);
    }

    @Test
    @SneakyThrows
    void testRejectConfiguration() {
        final var conciergeContext = new ConciergeContext();
        final var configKey = ResourceHelper.getResource("configKey.json",
                ConfigKey.class);
        conciergeContext.addContext(ConfigKey.class.getSimpleName(), configKey);
        ContextUtils.addSchemaUpdaterContext(conciergeContext, getConfigUpdater());
        final var configDetails = ResourceHelper.getResource("configDetails.json", ConfigDetails.class);
        Mockito.when(getConciergeRepository().getStoredRecord(configKey))
                .thenReturn(Optional.of(configDetails));
        getConciergeProcessor().process(conciergeContext);
        Mockito.verify(getConciergeRepository(), Mockito.times(1)).update(configDetails);
    }
}