package io.aklivity.zilla.runtime.engine.internal.layout.metrics;

import static io.aklivity.zilla.runtime.engine.internal.layouts.Layout.Mode.CREATE_READ_WRITE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import org.junit.Test;

import io.aklivity.zilla.runtime.engine.internal.layouts.metrics.GaugesLayout;

public class GaugesLayoutTest
{
    @Test
    public void shouldWorkInGenericCase() throws Exception
    {
        String fileName = "target/zilla-itests/gauges0";
        Path path = Paths.get(fileName);
        GaugesLayout gaugesLayout = new GaugesLayout.Builder()
                .path(path)
                .capacity(8192)
                .mode(CREATE_READ_WRITE)
                .build();

        LongConsumer writer1 = gaugesLayout.supplyWriter(11L, 42L);
        LongConsumer writer2 = gaugesLayout.supplyWriter(22L, 77L);
        LongConsumer writer3 = gaugesLayout.supplyWriter(33L, 88L);

        LongSupplier reader1 = gaugesLayout.supplyReader(11L, 42L);
        LongSupplier reader2 = gaugesLayout.supplyReader(22L, 77L);
        LongSupplier reader3 = gaugesLayout.supplyReader(33L, 88L);

        assertThat(reader1.getAsLong(), equalTo(0L)); // should be 0L initially
        writer1.accept(1L);
        assertThat(reader1.getAsLong(), equalTo(1L));
        writer1.accept(1L);
        writer2.accept(100L);
        writer3.accept(77L);
        assertThat(reader1.getAsLong(), equalTo(1L));
        assertThat(reader2.getAsLong(), equalTo(100L));
        assertThat(reader3.getAsLong(), equalTo(77L));
        writer2.accept(10L);
        writer3.accept(1L);
        writer2.accept(20L);
        writer3.accept(1L);
        writer3.accept(3L);
        assertThat(reader2.getAsLong(), equalTo(20L));
        assertThat(reader3.getAsLong(), equalTo(3L));

        gaugesLayout.close();
        assertTrue(Files.exists(path));
        Files.delete(path);
    }

    @Test
    public void shouldThrowExceptionIfBufferIsTooSmall() throws Exception
    {
        String fileName = "target/zilla-itests/gauges1";
        Path path = Paths.get(fileName);
        GaugesLayout gaugesLayout = new GaugesLayout.Builder()
                .path(path)
                .capacity(71) // we'd need 72 bytes here for the 3 records
                .mode(CREATE_READ_WRITE)
                .build();

        gaugesLayout.supplyWriter(11L, 42L);
        gaugesLayout.supplyWriter(22L, 77L);
        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            gaugesLayout.supplyWriter(33L, 88L);
        });

        gaugesLayout.close();
        assertTrue(Files.exists(path));
        Files.delete(path);
    }

    @Test
    public void shouldGetIds()
    {
        String fileName = "target/zilla-itests/gauges2";
        Path path = Paths.get(fileName);
        GaugesLayout gaugesLayout = new GaugesLayout.Builder()
                .path(path)
                .capacity(8192)
                .mode(CREATE_READ_WRITE)
                .build();

        gaugesLayout.supplyWriter(11L, 42L);
        gaugesLayout.supplyWriter(22L, 77L);
        gaugesLayout.supplyWriter(33L, 88L);
        long[][] expectedIds = new long[][]{{11L, 42L}, {22L, 77L}, {33L, 88L}};

        assertThat(gaugesLayout.getIds(), equalTo(expectedIds));
    }
}
