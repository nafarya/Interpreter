package io.github.nafarya.interpreter;

import io.github.nafarya.interpreter.parser.LangParser;
import io.github.nafarya.interpreter.util.ForLoopContext;
import io.github.nafarya.interpreter.util.FunctionContext;
import io.github.nafarya.interpreter.util.IfClauseContext;
import io.github.nafarya.interpreter.util.VariableContext;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.IntStream;

public class Kefetator {

    private Map<String, LangParser.FunctionContext> functions;
    private List<VariableContext> contextStack;
    private Scanner scanner;
    private PrintStream outputStream;

    public Kefetator(PrintStream outputStream) {
        functions = new HashMap<>();
        contextStack = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(System.in);
        scanner = new Scanner(reader);
        this.outputStream = outputStream;
    }

    public int evalProg(LangParser.ProgContext ctx) {
        for (LangParser.FunctionContext fctx : ctx.function()) {
            final String functionName = fctx.NAME().getText();
            functions.put(functionName, fctx);
        }
        return evalFunction("main", Arrays.asList());
    }

    private int evalFunction(String name, List<Integer> evaluatedArgs) {
//        System.out.println("CALL " + name + " [" +
//                evaluatedArgs.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]");
        if ("readInt".equals(name)) {
            if (evaluatedArgs.size() > 0) {
                throw new RuntimeException("readInt() must have no arguments");
            }
            return scanner.nextInt();
        }
        LangParser.FunctionContext ctx = functions.get(name);
        if (ctx == null) {
            throw new RuntimeException("Function '" + name + "' is not defined");
        }
        VariableContext vc = new FunctionContext();
        for (int i = 0; i < evaluatedArgs.size(); i++) {
            final String argName = ctx.funcDeclArgs().NAME().get(i).getText();
            vc.getContext().put(argName, evaluatedArgs.get(i));
        }
        pushContext(vc);
        Integer returnValue = evalStatements(ctx.funcBody().statement());
        popContext();
        if (returnValue != null) {
            return returnValue;
        }
        throw new RuntimeException("Function '" + name + "' has no return statement");
    }

    private Integer evalStatements(List<LangParser.StatementContext> statements) {
        for (LangParser.StatementContext st : statements) {
            if (st.print() != null) {
                outputStream.println(evalAtom(st.print().atom()));
            } else if (st.assignment() != null) {
                evalAssignment(st.assignment().assignmentBody());
            } else if (st.ret() != null) {
                return evalExpr(st.ret().expr());
            } else if (st.ifclause() != null) {
                VariableContext vc = new IfClauseContext();
                pushContext(vc);
                Integer ifReturns = evalIfClause(st.ifclause());
                popContext();
                if (ifReturns != null) {
                    return ifReturns;
                }
            } else if (st.forloop() != null) {
                VariableContext vc = new ForLoopContext();
                pushContext(vc);
                Integer forReturns = evalForLoop(st.forloop());
                if (forReturns != null) {
                    return forReturns;
                }
            }
        }
        return null;
    }

    private Integer evalForLoop(LangParser.ForloopContext ctx) {
        if (ctx.forPreaction() != null) {
            evalAssignment(ctx.forPreaction().assignmentBody());
        }
        for (;;) {
            int cond = 1;
            if (ctx.forPredicate() != null) {
                cond = evalAtom(ctx.forPredicate().atom());
            }
            if (cond == 0) {
                break;
            }
            Integer forReturns = evalStatements(ctx.forbody().statement());
            if (forReturns != null) {
                return forReturns;
            }
            if (ctx.forPostaction() != null) {
                evalAssignment(ctx.forPostaction().assignmentBody());
            }
        }
        return null;
    }

    private Integer evalIfClause(LangParser.IfclauseContext ctx) {
        int predicate = evalExpr(ctx.ifPredicate().expr());
        if (predicate != 0) {
            return evalStatements(ctx.leftBranch().ifBranch().statement());
        } else {
            if (ctx.rightBranch().ifBranch() != null) {
                return evalStatements(ctx.rightBranch().ifBranch().statement());
            }
        }
        return null;
    }

    private void evalAssignment(LangParser.AssignmentBodyContext ctx) {
        final int value;
        if (ctx.expr() != null) {
            value = evalExpr(ctx.expr());
        } else if (ctx.funccall() != null) {
            List<Integer> args = evalFunctionArgs(ctx.funccall().funcargs());
            value = evalFunction(ctx.funccall().NAME().getText(), args);
        } else {
            throw new RuntimeException("Bad state");
        }
        putVariableInCurrentContext(ctx.NAME().getText(), value);
    }

    // expr = mulExpr + term*
    private int evalExpr(LangParser.ExprContext ctx) {
        int result = evalMulExpr(ctx.mulExpr());
        for (LangParser.TermContext term : ctx.term()) {
            int snd = evalMulExpr(term.mulExpr());
            if (term.ADD() != null) {
                result += snd;
            } else if (term.SUB() != null) {
                result -= snd;
            }
        }
        return result;
    }

    // mulexpr = atom + factor*
    private int evalMulExpr(LangParser.MulExprContext ctx) {
        int result = evalAtom(ctx.atom());

        for (LangParser.FactorContext factor : ctx.factor()) {
            int snd = evalAtom(factor.atom());
            if (factor.MUL() != null) {
                result*= snd;
            } else if (factor.DIV() != null) {
                result /= snd;
            }
        }

        return result;
    }

    private List<Integer> evalFunctionArgs(LangParser.FuncargsContext ctx) {
        List<Integer> args = new ArrayList<>();
        for (LangParser.AtomContext arg : ctx.atom()) {
            args.add(evalAtom(arg));
        }
        return args;
    }

    private int evalAtom(LangParser.AtomContext ctx) {
        if (ctx.NUM() != null) {
            return Integer.parseInt(ctx.NUM().getText());
        } else if (ctx.NAME() != null) {
            return lookupVariable(ctx.NAME().getText());
        } else if (ctx.funccall() != null) {
            List<Integer> args = evalFunctionArgs(ctx.funccall().funcargs());
            return evalFunction(ctx.funccall().NAME().getText(), args);
        } else if (ctx.expr() != null) {
            return evalExpr(ctx.expr());
        }
        throw new RuntimeException("wtf");
    }

    private int lookupVariable(String name) {
        int functionContextsSeen = 0;
        for (int i = contextStack.size() - 1; functionContextsSeen < 1 && i >= 0; i--) {
            Integer value = contextStack.get(i).getContext().get(name);
            if (value != null) {
                return value;
            }
            if (contextStack.get(i) instanceof FunctionContext) {
                functionContextsSeen++;
            }
        }
        throw new RuntimeException("Variable not in scope: " + name); //TODO: throw and handle exception
    }

    private void pushContext(VariableContext vc) {
        contextStack.add(vc);
    }

    private void popContext() {
        contextStack.remove(contextStack.size() - 1);
    }

    private static IntStream revRange(int from, int to) {
        return IntStream.range(from, to).map(i -> to - i + from - 1);
    }

    private void putVariableInCurrentContext(String name, int value) {
        int functionContextsSeen = 0;
        int i, index = contextStack.size() - 1;
        for (i = contextStack.size() - 1; i >= 0; i--) {
            if (contextStack.get(i) instanceof FunctionContext) {
                functionContextsSeen++;
                if (functionContextsSeen == 2) {
                    break;
                }
            }
            if (contextStack.get(i).getContext().containsKey(name)) {
                index = i;
            }
        }
        contextStack.get(index).getContext().put(name, value);
    }

}
