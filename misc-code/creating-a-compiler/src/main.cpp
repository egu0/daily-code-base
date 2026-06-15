#include <iostream>
#include <fstream>
#include <sstream>
#include <optional>
#include <vector>

#include "generation.hpp"

int main(int argc, char* argv[])
{
    if (argc != 2)
    {
        std::cerr << "Incorrect usage. Correct usage is..." << std::endl;
        std::cerr << "hydro <input.hy>" << std::endl;
        return EXIT_FAILURE;
    }

    //读取文件内容
    std::string contents;
    {
        std::stringstream contents_stream;
        std::fstream input(argv[1], std::ios::in);
        // std::fstream input("/home//Workspace/cpp_project/compiler-pixeled/test.hy", std::ios::in);
        contents_stream << input.rdbuf();
        contents = contents_stream.str();
    }

    // 解析代码字符串为 Token 列表
    Tokenizer tokenzier(std::move(contents));
    std::vector<Token> tokens = tokenzier.tokenize();

    // 根据生成式解析 tokens 生成 AST
    Parser parser(std::move(tokens));
    std::optional<NodeProg> prog = parser.parse_prog();
    if (!prog.has_value())
    {
        std::cerr << "Invalid Program" << std::endl;
        exit(EXIT_FAILURE);
    }

    // 生成 ASM 汇编
    Generator generator(prog.value());
    {
        std::fstream file("out.asm", std::ios::out);
        file << generator.gen_prog();
    }

    system("nasm -felf64 out.asm");
    system("ld -o out out.o");

    return EXIT_SUCCESS;
}