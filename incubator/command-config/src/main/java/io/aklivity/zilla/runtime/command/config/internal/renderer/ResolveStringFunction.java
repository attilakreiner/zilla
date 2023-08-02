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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class ResolveStringFunction implements Function
{
    private final JsonNode jsonNode;
    private final ObjectMapper yamlWriter;

    public ResolveStringFunction(
        JsonNode jsonNode)
    {
        this.jsonNode = jsonNode;
        YAMLFactory yamlFactory = new YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlWriter = new ObjectMapper(yamlFactory);
    }

    @Override
    public Object execute(
        Map<String, Object> args,
        PebbleTemplate self,
        EvaluationContext context,
        int lineNumber)
    {
        String expression = (String)args.get("expression");
        return resolveExpression(expression);
    }

    @Override
    public List<String> getArgumentNames()
    {
        return List.of("expression");
    }

    private String resolveExpression(
        String expression)
    {
        String result;
        JsonNode node = jsonNode.at(expression);
        switch (node.getNodeType())
        {
        case ARRAY:
        case OBJECT:
            result = toYaml(node);
            break;
        default:
            result = node.asText();
            break;
        }
        return result;
    }

    private String toYaml(
        JsonNode node)
    {
        String result = "";
        try
        {
            result = yamlWriter.writeValueAsString(node);
            if (result.endsWith("\n"))
            {
                result = result.substring(0, result.length() - 1);
            }
        }
        catch (JsonProcessingException ex)
        {
            ex.printStackTrace();
            rethrowUnchecked(ex);
        }
        return result;
    }
}
