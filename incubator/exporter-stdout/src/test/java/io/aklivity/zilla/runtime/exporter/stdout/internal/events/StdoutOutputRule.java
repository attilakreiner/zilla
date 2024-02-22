package io.aklivity.zilla.runtime.exporter.stdout.internal.events;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public final class StdoutOutputRule implements TestRule
{
    public final static PrintStream out;

    private final static ByteArrayOutputStream bos;

    static
    {
        bos = new ByteArrayOutputStream();
        out = new PrintStream(bos);
    }

    private Pattern expected;

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
                assertThat(bos.toString(StandardCharsets.UTF_8), matchesPattern(expected));
            }
        };
    }

    public void expect(
        Pattern expected)
    {
        this.expected = expected;
    }
}
