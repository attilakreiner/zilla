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

import java.util.List;
import java.util.Map;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class IndentFilter implements Filter
{
    public static final String SPACES = "spaces";

    @Override
    public List<String> getArgumentNames()
    {
        return List.of(SPACES);
    }

    @Override
    public Object apply(
        Object input,
        Map<String, Object> args,
        PebbleTemplate self,
        EvaluationContext context,
        int lineNumber)
    {
        int spaces = 0;
        Object spacesArg = args.get(SPACES);
        if (spacesArg instanceof Number)
        {
            spaces = ((Number)spacesArg).intValue();
        }
        String result = "";
        if (input instanceof String)
        {
            String[] lines = ((String)input).split("\n");
            StringBuilder indented = new StringBuilder();
            for (String line : lines)
            {
                indented.append(" ".repeat(spaces));
                indented.append(line).append("\n");
            }
            result = indented.toString();
            if (result.endsWith("\n"))
            {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }
}
