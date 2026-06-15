package vm;

import gen.*;
import gen.operand.Register;
import lexer.LexicalException;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.util.ParseException;
import translator.Translator;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VMTests {

    @Test
    public void calcExpr() throws LexicalException, ParseException, GeneratorException {
        var source = "func main() int { var a = 2*3+4 \n return \n }";
        var astNode = Parser.parse(source);
        var translator = new Translator();
        var taProgram = translator.translate(astNode);
        var gen = new OpCodeGen();
        var program = gen.gen(taProgram);

        var statics = program.getStaticArea(taProgram);
        Integer entry = program.getEntry();
        ArrayList<Integer> opcodes = program.toByteCodes();

        System.out.println(program);
        System.out.println("---");

        var vm = new VirtualMachine(statics, opcodes, entry);

        // CALL main
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        System.out.println("RA:" + vm.getRegisters()[Register.RA.getAddr()]);
        assertEquals(18, vm.getSpMemory(0));

        // p1 = 2 * 3
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(2, vm.getRegisters()[Register.S0.getAddr()]);
        assertEquals(3, vm.getRegisters()[Register.S1.getAddr()]);
        assertEquals(6, vm.getRegisters()[Register.LO.getAddr()]);
        assertEquals(6, vm.getRegisters()[Register.S2.getAddr()]);
        assertEquals(6, vm.getSpMemory(-2));

        // p2 = p1 + 4
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(6, vm.getRegisters()[Register.S0.getAddr()]);
        assertEquals(4, vm.getRegisters()[Register.S1.getAddr()]);
        assertEquals(10, vm.getRegisters()[Register.S2.getAddr()]);
        assertEquals(10, vm.getSpMemory(-3));

        // a = p2
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(10, vm.getSpMemory(-1));
        assertEquals(10, vm.getRegisters()[Register.S0.getAddr()]);

        // RETURN null
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();

        System.out.println("SP:" + vm.getRegisters()[Register.SP.getAddr()]);
    }

    @Test
    public void recursiveFunction() throws FileNotFoundException, ParseException, LexicalException,
            UnsupportedEncodingException, GeneratorException {
        var source = """
                func fact(int n)  int {
                    if(n == 0) {
                        return 1
                    }
                    return fact(n-1) * n
                }
                func main() void {
                    return fact(2)
                }
                """;
        var astNode = Parser.parse(source);
        var translator = new Translator();
        var taProgram = translator.translate(astNode);
        var gen = new OpCodeGen();
        var program = gen.gen(taProgram);
        System.out.println(program);
        System.out.println("---");

        var statics = program.getStaticArea(taProgram);
        var entry = program.getEntry();
        var opcodes = program.toByteCodes();
        // System.out.println(taProgram.getStaticSymbolTable());
        var vm = new VirtualMachine(statics, opcodes, entry);

        // CALL main
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        System.out.println("RA:" + vm.getRegisters()[Register.RA.getAddr()]);
        assertEquals(39, vm.getSpMemory(0));

        // PARAM 10 0
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(2, vm.getSpMemory(-3));

        // SP -2
        vm.runOneStep();
        vm.runOneStep();
        System.out.println("RA:" + vm.getRegisters()[Register.RA.getAddr()]);

        // #FUNC_BEGIN
        vm.runOneStep();
        assertEquals(33, vm.getSpMemory(0));

        // #p1 = n == 0
        assertEquals(2, vm.getSpMemory(-1));
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(false, vm.getSpMemory(-2) == 0);

        // #IF p1 ELSE L1
        vm.runOneStep();
        vm.runOneStep();

        // #p3 = n - 1
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(1, vm.getSpMemory(-3));

        // #PARAM p3 0
        // #SP-5
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(1, vm.getSpMemory(-1));
        System.out.println(vm.getSpMemory(-2));

        vm.runOneStep();
        vm.runOneStep();

        // #p1 = n == 0
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(false, vm.getSpMemory(-2) == 0);

        // #IF p1 ELSE L1
        vm.runOneStep();

        // #p3 = n - 1
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();

        // #PARAM p3 0
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();

        // CALL
        vm.runOneStep();
        vm.runOneStep();

        // #p1 = n == 0
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        assertEquals(true, vm.getSpMemory(-2) == 0);

        // #IF p1 ELSE L1
        vm.runOneStep();

        // RETURN 1
        vm.runOneStep();
        vm.runOneStep();

        vm.runOneStep();
        vm.runOneStep();

        // #p4 = p2 * n 计算递归值
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        // #RETURN p4
        vm.runOneStep();
        vm.runOneStep();
        //RETURN
        vm.runOneStep();
        vm.runOneStep();

        //#p4 = p2 * n
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();
        vm.runOneStep();

        assertEquals(2, vm.getSpMemory(-5));

        vm.runOneStep();
        vm.runOneStep();
        // RETURN MAIN
        vm.runOneStep();

        // SP 2
        vm.runOneStep();

        // #RETURN p1 : from main
        vm.runOneStep();
        assertEquals(2, vm.getSpMemory(-1));

        while (vm.runOneStep())
            ;
        assertEquals(2, vm.getSpMemory(0));

    }

    @Test
    public void recursiveFunction1() throws FileNotFoundException, ParseException, LexicalException,
            UnsupportedEncodingException, GeneratorException {
        var source = """
                func fact(int n)  int {
                    if(n == 0) {
                        return 1
                    }
                    return fact(n-1) * n
                }
                func main() void {
                    return fact(5)
                }
                """;
        var astNode = Parser.parse(source);

        var translator = new Translator();
        var taProgram = translator.translate(astNode);

        var gen = new OpCodeGen();
        var program = gen.gen(taProgram);
        var statics = program.getStaticArea(taProgram);
        var entry = program.getEntry();
        var opcodes = program.toByteCodes();
        System.out.println(taProgram.getStaticSymbolTable());

        var vm = new VirtualMachine(statics, opcodes, entry);
        vm.run();

        assertEquals(120, vm.getSpMemory(0));

    }
}
