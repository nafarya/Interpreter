package io.github.nafarya.interpreter;

import io.github.nafarya.interpreter.parser.LangLexer;
import io.github.nafarya.interpreter.parser.LangParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class KefetatorTest {

    @Before
    public void setUp() throws Exception {



    }

    private BufferedReader getProgramStreamReader(String program) {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(program.getBytes(Charset.defaultCharset())), StandardCharsets.UTF_8));
    }

    private String testProgram(String program) throws IOException {
        BufferedReader reader = getProgramStreamReader(program);
        ANTLRInputStream input = new ANTLRInputStream(reader);
        LangLexer lexer = new LangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LangParser parser = new LangParser(tokens);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        Kefetator kef = new Kefetator(printStream);
        kef.evalProg(parser.prog());
        return outputStream.toString();
    }

    @Test
    public void testAPlusB() throws IOException {
        String output = testProgram(
                "func main() {" +
                        "  a = 5;" +
                        "  b = 6;" +
                        "  print((a + b));" +
                        "  return 0;" +
                        "}");
        assertEquals("11\n", output);
    }

    @Test
    public void testAMinusB() throws IOException {
        String output = testProgram(
                "func main() {" +
                        "  a = 5;" +
                        "  b = 6;" +
                        "  print((a - b));" +
                        "  return 0;" +
                        "}");
        assertEquals("-1\n", output);
    }

    @Test
    public void testAMulB() throws IOException {
        String output = testProgram(
                "func main() {" +
                        "  a = 5;" +
                        "  b = 7;" +
                        "  print((a * b));" +
                        "  return 0;" +
                        "}");
        assertEquals("35\n", output);
    }

    @Test
    public void testADivB() throws IOException {
        String output = testProgram(
                "func main() {" +
                        "  a = 32;" +
                        "  b = 8;" +
                        "  print((a / b));" +
                        "  return 0;" +
                        "}");
        assertEquals("4\n", output);
    }


    @Test
    public void testAPowN() throws IOException {
        String output = testProgram(
                "func pow(x n) {\n" +
                        "  sum = x;\n" +
                        "  for i = 2; (i - n - 1); i = i + 1 {\n" +
                        "      sum = sum * x;\n" +
                        "  }\n" +
                        "  return sum;\n" +
                        "}\n" +
                        "\n" +
                        "func main() {\n" +
                        "  n = 5;\n" +
                        "  print(pow(5 2));\n" +
                        "  return 0;\n" +
                        "}");
        assertEquals("25\n", output);
    }


    @Test
    public void testFactorial() throws IOException {
        String output = testProgram(
                "func f(n) { " +
                        "  if (n) {" +
                        "    return n * f( (n - 1) );" +
                        "  } else {" +
                        "    return 1;" +
                        "  }" +
                        "}" +

                "func main() {" +
                        "  print(f(5));" +
                        "  return 0;" +
                        "}");
        assertEquals("120\n", output);
    }

    @Test
    public void testFibonacci() throws IOException {
        String output = testProgram(
                "func is1(n) {" +
                        "    if ((n - 1)) {" +
                        "        return 0;" +
                        "    }" +
                        "    return 1;" +
                        "}" +
                        "" +
                        "func is0(n) {" +
                        "    if (n) {" +
                        "        return 0;" +
                        "    }" +
                        "    return 1;" +
                        "}" +
                        "\n" +
                        "func fib(n) {\n" +
                        "    if (is0(n)) {\n" +
                        "        return 1;\n" +
                        "    }\n" +
                        "    if (is1(n)) {\n" +
                        "        return 1;\n" +
                        "    }\n" +
                        "    return fib((n-2)) + fib((n - 1));\n" +
                        "}\n" +
                        "\n" +
                        "func main() {\n" +
                        "    n = 25;\n" +
                        "    m = fib(n);\n" +
                        "    print(m);\n" +
                        "    return 0;\n" +
                        "}");
        assertEquals("121393\n", output);
    }

    @Test
    public void testConditions1() throws IOException {
        String output = testProgram(
                "func condtest(a) {\n" +
                        "  if (1) {\n" +
                        "    a = 5;\n" +
                        "    if (0) {\n" +
                        "      a = 7;\n" +
                        "    } else {\n" +
                        "      a = 6;\n" +
                        "    }\n" +
                        "  }\n" +
                        "  return a;\n" +
                        "}\n" +
                        "\n" +
                        "func testCond() {\n" +
                        "  c = condtest(2);\n" +
                        "  return c;\n" +
                        "}\n" +
                        "func main() {\n" +
                        "    print( testCond() );" +
                        "    return 0;\n" +
                        "}");
        assertEquals("6\n", output);
    }

    @Test
    public void testAscendingCycle() throws IOException {
        String output = testProgram(
                "func sq(n) {\n" +
                        "  return n * n;\n" +
                        "}\n" +

                "func main() {" +
                        "n = 5;\n" +
                        "  sum = 0;\n" +
                        "  for i = 1; (i - n - 1); i = i + 1 {\n" +
                        "    sum = sum + sq(i);\n" +
                        "  }\n" +
                        "  print(sum);" +
                        "  return 0;" +
                        "}");
        assertEquals("55\n", output);
    }

    @Test
    public void testDescendingCycle() throws IOException {
        String output = testProgram(
                "func sq(n) {\n" +
                        "  return n * n;\n" +
                        "}\n" +

                        "func main() {" +
                        "n = 5;\n" +
                        "  sum = 0;\n" +
                        "  for i = n; i; i = i - 1 {\n" +
                        "    sum = sum + sq(i);\n" +
                        "  }\n" +
                        "  print(sum);" +
                        "  return 0;" +
                        "}");
        assertEquals("55\n", output);
    }

    @Test
    public void testArithProgression() throws IOException {
        String output = testProgram(
                "func arithmeticProgression(a1 d n) {\n" +
                        "  return (a1 + (n - 1) * d);\n" +
                        "}\n" +
                        "\n" +
                        "\n" +
                        "func main() {\n" +
                        "  n = 5;\n" +
                        "  print(arithmeticProgression(1 2 5));\n" +
                        "  return 0;\n" +
                        "}");
        assertEquals("9\n", output);
    }

    @Test
    public void testGeomProgression() throws IOException {
        String output = testProgram(
                "func pow(x n) {\n" +
                        "  sum = x;\n" +
                        "  for i = 2; (i - n - 1); i = i + 1 {\n" +
                        "      sum = sum * x;\n" +
                        "  }\n" +
                        "  return sum;\n" +
                        "}\n" +
                        "\n" +
                        "func geometricProgression(a1 q n) {\n" +
                        "  n = n - 1;\n" +
                        "  return (a1 * (pow(q n)));\n" +
                        "}\n" +
                        "\n" +
                        "\n" +
                        "func main() {\n" +
                        "  n = 5;\n" +
                        "  print(geometricProgression(2 2 2));\n" +
                        "  return 0;\n" +
                        "}");
        assertEquals("4\n", output);
    }



}