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
package io.aklivity.zilla.runtime.command.dump.internal.airline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.Container;

public final class DumpRule implements TestRule
{
    private static final Path ENGINE_PATH = Path.of("target/zilla-itests");
    private static final Path PCAP_PATH = ENGINE_PATH.resolve("actual.pcap");
    private static final Path TXT_PATH = ENGINE_PATH.resolve("actual.txt");
    private static final DumpCommandRunner DUMP = new DumpCommandRunner();
    private static final TsharkRunner TSHARK = new TsharkRunner();

    private Path expected;

    @Override
    public Statement apply(
        Statement base,
        Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                base.evaluate();
                DUMP.createPcap(PCAP_PATH);
                Container.ExecResult result = TSHARK.createTxt(PCAP_PATH);
                Files.writeString(TXT_PATH, result.getStdout());
                assertThat(result.getExitCode(), equalTo(0));
                assert expected != null;
                assertThat(result.getStdout(), equalTo(Files.readString(expected)));
            }
        };
    }

    public void expect(
        String file) throws Exception
    {
        this.expected = resourceToPath(file);
    }

    private static Path resourceToPath(
        String name) throws Exception
    {
        URL resource = DumpRule.class.getResource(name);
        assert resource != null;
        return Path.of(resource.toURI());
    }
}
