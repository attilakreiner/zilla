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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class TemplateRendererTest
{
    @Test
    public void shouldRenderTemplate() throws Exception
    {
        // GIVEN
        String expectedResult =
            "---\n" +
            "name: test\n" +
            "quoteBraces: ${{env.NAME}}\n" +
            "string: Hello Zilla!\n" +
            "integer: 42\n" +
            "double: 0.77\n" +
            "boolean: true\n" +
            "firstName: Aklivity\n" +
            "answer: 42\n";
        Path inputPath = Path.of(getClass().getResource("input.yaml").toURI());
        TemplateRenderer renderer = new TemplateRenderer(inputPath);
        Writer writer = new StringWriter();

        // WHEN
        renderer.render(writer, "template.yaml");

        // THEN
        assertThat(writer.toString(), equalTo(expectedResult));
    }
}
