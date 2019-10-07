package io.github.nafarya.interpreter;

import io.github.nafarya.interpreter.parser.LangLexer;
import io.github.nafarya.interpreter.parser.LangParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws IOException {

        Process makeExec = Runtime.getRuntime().exec("chmod +x concatFiles.sh");
        Process runExec  = Runtime.getRuntime().exec("./concatFiles.sh");
        try {
            makeExec.waitFor();
            runExec.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("progKefExecFile.kef"), StandardCharsets.UTF_8));

        ANTLRInputStream input = new ANTLRInputStream(in);
        LangLexer lexer = new LangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LangParser parser = new LangParser(tokens);

        Kefetator kef = new Kefetator(System.out);
        kef.evalProg(parser.prog());

//        ParseTreeWalker walker = new ParseTreeWalker();
//        EvalLangVisitor visitor = new EvalLangVisitor();
//        walker.walk(visitor, parser.prog());


    }
}
