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
package io.aklivity.zilla.runtime.exporter.stdout.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.agrona.LangUtil;

import io.aklivity.zilla.runtime.engine.EngineConfiguration;
import io.aklivity.zilla.runtime.engine.EngineContext;
import io.aklivity.zilla.runtime.engine.exporter.ExporterHandler;
import io.aklivity.zilla.runtime.exporter.stdout.internal.config.StdoutExporterConfig;
import io.aklivity.zilla.runtime.exporter.stdout.internal.labels.LabelManager;
import io.aklivity.zilla.runtime.exporter.stdout.internal.layouts.EventsLayout;
import io.aklivity.zilla.runtime.exporter.stdout.internal.printer.PrintableEventsStream;
import io.aklivity.zilla.runtime.exporter.stdout.internal.spy.RingBufferSpy.SpyPosition;

public class StdoutExporterHandler implements ExporterHandler
{
    private static final Pattern EVENTS_PATTERN = Pattern.compile("events(\\d+)");

    private final EngineContext context;
    private final Path directory;
    private final LabelManager labels;
    private final PrintStream out;

    private PrintableEventsStream[] printables;

    public StdoutExporterHandler(
        EngineConfiguration config,
        EngineContext context,
        StdoutExporterConfig exporter,
        PrintStream out)
    {
        this.context = context;
        this.directory = config.directory();
        this.labels = new LabelManager(directory);
        this.out = out;
    }

    @Override
    public void start()
    {
        try (Stream<Path> files = Files.walk(directory, 3))
        {
            this.printables = files.filter(this::isEventsFile)
                 .map(this::newPrintable)
                 .toArray(PrintableEventsStream[]::new);
        }
        catch (IOException ex)
        {
            LangUtil.rethrowUnchecked(ex);
        }

    }

    @Override
    public int export()
    {
        int workCount = 0;
        if (printables != null)
        {
            for (int i = 0; i < printables.length; i++)
            {
                workCount += printables[i].process();
            }
        }
        return workCount;
    }

    @Override
    public void stop()
    {
    }

    private boolean isEventsFile(
        Path path)
    {
        final int depth = path.getNameCount() - directory.getNameCount();
        if (depth != 1 || !Files.isRegularFile(path))
        {
            return false;
        }

        final Matcher matcher = EVENTS_PATTERN.matcher(path.getName(path.getNameCount() - 1).toString());
        return matcher.matches();
    }

    private PrintableEventsStream newPrintable(
        Path path)
    {
        EventsLayout layout = new EventsLayout.Builder()
            .path(path)
            .readonly(true)
            .spyAt(SpyPosition.ZERO)
            .build();
        return new PrintableEventsStream(labels, layout, out);
    }
}
