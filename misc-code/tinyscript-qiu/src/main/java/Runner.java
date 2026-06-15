import parser.Parser;
import translator.Translator;

public class Runner {
    public static void main(String[] args)
            throws Exception {
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
        // astNode.print(0);

        var translator = new Translator();
        var program = translator.translate(astNode);
        System.out.println(program);
    }
}
