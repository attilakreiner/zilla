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
package io.aklivity.zilla.runtime.command.config.internal.renderer;

import static org.agrona.LangUtil.rethrowUnchecked;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class TemplateRenderer
{
    private final PebbleEngine pebble;

    public TemplateRenderer(
        Path input)
    {
        DocumentContext jsonPath = initJsonPath(input);
        ConfigCommandExtension extension = new ConfigCommandExtension(jsonPath);
        this.pebble = initPebble(extension);
    }

    public void render(
        Writer writer,
        String resource)
    {
        try
        {
            URL url = getClass().getResource(resource);
            String template = Files.readString(Path.of(url.toURI()));
            PebbleTemplate pebbleTemplate = pebble.getLiteralTemplate(template);
            pebbleTemplate.evaluate(writer);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            rethrowUnchecked(ex);
        }
    }

    private DocumentContext initJsonPath(
        Path input)
    {
        DocumentContext jsonPathContext = null;
        try
        {
            String yaml = Files.readString(input);
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object object = yamlReader.readValue(yaml, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            String json = jsonWriter.writeValueAsString(object);
            jsonPathContext = JsonPath.parse(json);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            rethrowUnchecked(ex);
        }
        return jsonPathContext;
    }

    private PebbleEngine initPebble(
        ConfigCommandExtension extension)
    {
        return new PebbleEngine.Builder()
            .extension(extension)
            .newLineTrimming(false)
            .build();
    }
}
