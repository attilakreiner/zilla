/*
 * Copyright 2021-2023 Aklivity Inc.
 *
 * Aklivity licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.aklivity.zilla.runtime.binding.kafka.internal;

import java.nio.ByteBuffer;
import java.util.function.LongSupplier;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import io.aklivity.zilla.runtime.binding.kafka.internal.types.event.KafkaEventFW;
import io.aklivity.zilla.runtime.binding.kafka.internal.types.event.Result;
import io.aklivity.zilla.runtime.engine.EngineContext;
import io.aklivity.zilla.runtime.engine.binding.function.MessageConsumer;

public class KafkaEventContext
{
    private static final int EVENT_BUFFER_CAPACITY = 1024;
    private static final int ERROR_NONE = 0;

    private final KafkaEventFW.Builder kafkaEventRW = new KafkaEventFW.Builder();
    private final MutableDirectBuffer eventBuffer = new UnsafeBuffer(ByteBuffer.allocate(EVENT_BUFFER_CAPACITY));
    private final int kafkaTypeId;
    private final MessageConsumer logger;
    private final LongSupplier timestamp;

    public KafkaEventContext(
        EngineContext context)
    {
        this.kafkaTypeId = context.supplyTypeId(KafkaBinding.NAME);
        this.logger = context.logger();
        this.timestamp = context.timestamp();
    }

    public void authorization(
        int errorCode,
        long traceId,
        long routedId)
    {
        Result result = errorCode == ERROR_NONE ? Result.SUCCESS : Result.FAILURE;
        KafkaEventFW event = kafkaEventRW
            .wrap(eventBuffer, 0, eventBuffer.capacity())
            .authorization(e -> e
                .timestamp(timestamp.getAsLong())
                .traceId(traceId)
                .namespacedId(routedId)
                .result(r -> r.set(result))
            )
            .build();
        System.out.println(event); // TODO: Ati
        logger.accept(kafkaTypeId, event.buffer(), event.offset(), event.limit());
    }

    public void apiVersionRejected(
        long traceId)
    {
        KafkaEventFW event = kafkaEventRW
            .wrap(eventBuffer, 0, eventBuffer.capacity())
            .apiVersionRejected(e -> e
                .timestamp(timestamp.getAsLong())
                .traceId(traceId)
            )
            .build();
        System.out.println(event); // TODO: Ati
        logger.accept(kafkaTypeId, event.buffer(), event.offset(), event.limit());
    }
}
