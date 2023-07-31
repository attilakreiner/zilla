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

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class ResolveStringFunction implements Function
{
    private final DocumentContext jsonPathContext;

    public ResolveStringFunction(
        DocumentContext jsonPathContext)
    {
        this.jsonPathContext = jsonPathContext;
    }

    @Override
    public Object execute(
        Map<String, Object> args,
        PebbleTemplate self,
        EvaluationContext context,
        int lineNumber)
    {
        String expression = (String)args.get("expression");
        return resolveJsonPath(expression);
    }

    @Override
    public List<String> getArgumentNames()
    {
        return List.of("expression");
    }

    private String resolveJsonPath(
        String expression)
    {
        String result = "";
        try
        {
            Object object = jsonPathContext.read(expression);
            result = String.valueOf(object);
        }
        catch (PathNotFoundException ex)
        {
            ex.printStackTrace();
            rethrowUnchecked(ex);
        }
        return result;
    }
}
