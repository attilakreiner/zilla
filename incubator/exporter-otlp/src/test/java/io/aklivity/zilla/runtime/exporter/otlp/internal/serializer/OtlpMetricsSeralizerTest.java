/*
 * Copyright 2021-2023 Aklivity Inc
 *
 * Licensed under the Aklivity Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 *   https://www.aklivity.io/aklivity-community-license/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.aklivity.zilla.runtime.exporter.otlp.internal.serializer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import io.aklivity.zilla.runtime.engine.config.AttributeConfig;
import io.aklivity.zilla.runtime.exporter.otlp.internal.duplicated.CounterGaugeRecord;
import io.aklivity.zilla.runtime.exporter.otlp.internal.duplicated.HistogramRecord;
import io.aklivity.zilla.runtime.exporter.otlp.internal.duplicated.MetricRecord;
import io.aklivity.zilla.runtime.exporter.otlp.internal.duplicated.MetricsProcessor;

public class OtlpMetricsSeralizerTest
{
    @Test
    public void shouldWorkInGenericCase()
    {
        // GIVEN
        String expectedJson =
            "{" +
            "\"resourceMetrics\":[" +
                    "{" +
                        "\"resource\":{" +
                            "\"attributes\":[" +
                                "{" +
                                    "\"key\":\"service.namespace\"," +
                                    "\"value\":{" +
                                        "\"stringValue\":\"example\"" +
                                    "}" +
                                "}" +
                            "]" +
                        "}," +
                        "\"scopeMetrics\":[" +
                            "{" +
                                "\"scope\":{" +
                                    "\"name\":\"OtlpMetricsSerializer\"," +
                                    "\"version\":\"1.0.0\"" +
                                "}," +
                                "\"metrics\":[" +
                                    "{" +
                                        "\"name\":\"counter1\"," +
                                        "\"unit\":\"\"," +
                                        "\"description\":\"description for counter1\"," +
                                        "\"sum\":{" +
                                            "\"dataPoints\":[" +
                                                "{" +
                                                    "\"asInt\":42," +
                                                    "\"timeUnixNano\":1234567890," +
                                                    "\"attributes\":[" +
                                                        "{" +
                                                            "\"key\":\"namespace\"," +
                                                            "\"value\":{" +
                                                                "\"stringValue\":\"ns1\"" +
                                                            "}" +
                                                        "}," +
                                                        "{" +
                                                            "\"key\":\"binding\"," +
                                                            "\"value\":{" +
                                                                "\"stringValue\":\"binding1\"" +
                                                            "}" +
                                                        "}" +
                                                    "]" +
                                                "}" +
                                            "]," +
                                            "\"aggregationTemporality\":2," +
                                            "\"isMonotonic\":true" +
                                        "}" +
                                    "}," +
                                    "{" +
                                        "\"name\":\"gauge1\"," +
                                        "\"unit\":\"nanoseconds\"," +
                                        "\"description\":\"description for gauge1\"," +
                                        "\"gauge\":{" +
                                            "\"dataPoints\":[" +
                                                "{" +
                                                    "\"asInt\":77," +
                                                    "\"timeUnixNano\":1234567890," +
                                                    "\"attributes\":[" +
                                                        "{" +
                                                            "\"key\":\"namespace\"," +
                                                            "\"value\":{" +
                                                                "\"stringValue\":\"ns1\"" +
                                                            "}" +
                                                        "}," +
                                                        "{" +
                                                            "\"key\":\"binding\"," +
                                                            "\"value\":{" +
                                                                "\"stringValue\":\"binding1\"" +
                                                            "}" +
                                                        "}" +
                                                    "]" +
                                                "}" +
                                            "]" +
                                        "}" +
                                    "}," +
                                    "{" +
                                        "\"name\":\"histogram1\"," +
                                        "\"description\":\"description for histogram1\"," +
                                        "\"unit\":\"bytes\"," +
                                        "\"histogram\":{" +
                                            "\"aggregationTemporality\":2," +
                                            "\"dataPoints\":[" +
                                                "{" +
                                                    "\"timeUnixNano\":1234567890," +
                                                    "\"attributes\":[" +
                                                        "{" +
                                                            "\"key\":\"namespace\"," +
                                                            "\"value\":{" +
                                                                "\"stringValue\":\"ns1\"" +
                                                            "}" +
                                                        "}," +
                                                        "{" +
                                                            "\"key\":\"binding\"," +
                                                            "\"value\":{" +
                                                                "\"stringValue\":\"binding1\"" +
                                                            "}" +
                                                        "}" +
                                                    "]," +
                                                    "\"min\":1," +
                                                    "\"max\":1000," +
                                                    "\"sum\":2327," +
                                                    "\"count\":59," +
                                                    "\"explicitBounds\":[" +
                                                        "1," +
                                                        "10," +
                                                        "100" +
                                                    "]," +
                                                    "\"bucketCounts\":[" +
                                                        "7," +
                                                        "42," +
                                                        "9," +
                                                        "1" +
                                                    "]" +
                                                "}" +
                                            "]" +
                                        "}" +
                                    "}" +
                                "]" +
                            "}" +
                        "]" +
                    "}" +
                "]" +
            "}";

        CounterGaugeRecord counterRecord = mock(CounterGaugeRecord.class);
        when(counterRecord.namespaceName()).thenReturn("ns1");
        when(counterRecord.bindingName()).thenReturn("binding1");
        when(counterRecord.metricName()).thenReturn("counter1");
        when(counterRecord.value()).thenReturn(42L);

        CounterGaugeRecord gaugeRecord = mock(CounterGaugeRecord.class);
        when(gaugeRecord.namespaceName()).thenReturn("ns1");
        when(gaugeRecord.bindingName()).thenReturn("binding1");
        when(gaugeRecord.metricName()).thenReturn("gauge1");
        when(gaugeRecord.value()).thenReturn(77L);

        HistogramRecord histogramRecord = mock(HistogramRecord.class);
        when(histogramRecord.namespaceName()).thenReturn("ns1");
        when(histogramRecord.bindingName()).thenReturn("binding1");
        when(histogramRecord.metricName()).thenReturn("histogram1");
        when(histogramRecord.buckets()).thenReturn(4);
        when(histogramRecord.bucketLimits()).thenReturn(new long[]{1, 10, 100, 1000});
        when(histogramRecord.bucketValues()).thenReturn(new long[]{7, 42, 9, 1});
        when(histogramRecord.stats()).thenReturn(new long[]{1L, 1000L, 2327L, 59L, 39L}); // min, max, sum, cnt, avg

        List<MetricRecord> metricRecords = List.of(counterRecord, gaugeRecord, histogramRecord);
        MetricsProcessor metricsProcessor = mock(MetricsProcessor.class);
        when(metricsProcessor.getRecords()).thenReturn(metricRecords);

        List<AttributeConfig> attributes = List.of(
            new AttributeConfig("service.namespace", "example")
        );

        OtlpMetricsDescriptor descriptor = mock(OtlpMetricsDescriptor.class);
        when(descriptor.nameByBinding("counter1", "binding1")).thenReturn("counter1");
        when(descriptor.kind("counter1")).thenReturn("sum");
        when(descriptor.description("counter1")).thenReturn("description for counter1");
        when(descriptor.unit("counter1")).thenReturn("");

        when(descriptor.nameByBinding("gauge1", "binding1")).thenReturn("gauge1");
        when(descriptor.kind("gauge1")).thenReturn("gauge");
        when(descriptor.description("gauge1")).thenReturn("description for gauge1");
        when(descriptor.unit("gauge1")).thenReturn("nanoseconds");

        when(descriptor.nameByBinding("histogram1", "binding1")).thenReturn("histogram1");
        when(descriptor.kind("histogram1")).thenReturn("histogram");
        when(descriptor.description("histogram1")).thenReturn("description for histogram1");
        when(descriptor.unit("histogram1")).thenReturn("bytes");

        OtlpMetricsSerializer serializer = new OtlpMetricsSerializer(metricsProcessor, attributes,
            descriptor::kind, descriptor::nameByBinding, descriptor::description, descriptor::unit);
        serializer.timeStamp(1234567890);

        // WHEN
        String json = serializer.serializeAll();

        // THEN
        assertThat(json, equalTo(expectedJson));
    }

    @Test
    public void shouldReturnEmpty()
    {
        // GIVEN
        String expectedJson =
            "{" +
                "\"resourceMetrics\":[" +
                    "{" +
                        "\"resource\":{" +
                            "\"attributes\":[]" +
                        "}," +
                        "\"scopeMetrics\":[" +
                            "{" +
                                "\"scope\":{" +
                                    "\"name\":\"OtlpMetricsSerializer\"," +
                                    "\"version\":\"1.0.0\"" +
                                "}," +
                                "\"metrics\":[]" +
                            "}" +
                        "]" +
                    "}" +
                "]" +
            "}";
        List<AttributeConfig> attributes = List.of();
        MetricsProcessor metricsProcessor = mock(MetricsProcessor.class);
        when(metricsProcessor.getRecords()).thenReturn(List.of());
        OtlpMetricsDescriptor descriptor = mock(OtlpMetricsDescriptor.class);
        OtlpMetricsSerializer serializer = new OtlpMetricsSerializer(metricsProcessor, attributes,
            descriptor::kind, descriptor::nameByBinding, descriptor::description, descriptor::unit);
        serializer.timeStamp(1234567890);

        // WHEN
        String json = serializer.serializeAll();

        // THEN
        assertThat(json, equalTo(expectedJson));
    }
}
