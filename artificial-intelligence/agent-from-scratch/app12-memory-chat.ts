/*
任务：带记忆的学习助手

实现一个多轮 CLI 聊天 agent，用来学习 OpenAI Agents SDK。

要求：
- 支持连续多轮提问
- 记住前面讨论过的概念
- 能回答“刚才我们讲到哪了”这类问题
- 可以先用 MemorySession

练习点：
- sessions
- 多轮上下文
- 把一次性 run 改造成持续对话
*/

import readline from "node:readline/promises";
import { stdin as input, stdout as output } from "node:process";
import { Agent, MemorySession, run } from "@openai/agents";

const agent = new Agent({
  name: "Learning assistant",
  instructions:
    "You are a learning assistant for OpenAI Agents SDK. Explain concepts clearly, remember prior discussion, and when asked where we left off, summarize the recent topic.",
});

const session = new MemorySession();

const rl = readline.createInterface({
  input,
  output,
});

while (true) {
  const input = await rl.question("> ");

  if (input === "exit" || input === "quit") break;

  const result = await run(agent, input, { session });
  console.log(result.finalOutput);
}

rl.close();

// https://openai.github.io/openai-agents-js/guides/sessions/

// tests:
// > 什么是 handoff？
// > 那它和 tool 有什么区别？
// > 刚才我们讲到哪了？
