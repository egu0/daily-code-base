/*
任务：多专家作业分流 Agent

扩展现有 specialist agents 示例，做一个作业问题分流器。

要求：
- 创建 mathTutor
- 创建 historyTutor
- 创建 codeTutor
- 创建 writingTutor
- 创建 triageAgent
- triageAgent 根据用户问题 handoff 给合适专家

练习点：
- handoffs
- specialist agents
- 对比 handoff 和普通工具调用
*/

import { Agent, run } from "@openai/agents";

const historyTutor = new Agent({
  name: "History tutor",
  instructions:
    // "Answer history questions clearly and concisely.",
    "You are a history tutor. Answer history questions accurately and clearly. Explain key events, people, causes, consequences, and historical context. If dates or facts are uncertain, say so instead of guessing.",
});

const mathTutor = new Agent({
  name: "Math tutor",
  instructions:
    // "Explain math step by step and include worked examples.",
    "You are a math tutor. Explain math problems step by step. Show the reasoning, define formulas when needed, and include a worked example. Do not just give the final answer.",
});
const codeTutor = new Agent({
  name: "Code tutor",
  instructions:
    // "Explain coding questions clearly and concisely, generate code when needed, typescript is preferred.",
    "You are a coding tutor. Explain programming concepts clearly, help debug code, and provide concise examples when useful. Prefer TypeScript unless the user asks for another language. Explain the important parts of any code you provide.",
});
const writingTutor = new Agent({
  name: "Writing tutor",
  //   instructions: "Write in the style of a pirate captain from the Caribbean.",
  instructions:
    "You are a writing tutor. Help students improve writing by giving clear feedback on structure, clarity, grammar, tone, and word choice. Provide examples and revisions when useful.",
});

const triageAgent = Agent.create({
  name: "Homework triage", // 作业分流器（作业分诊员）
  instructions:
    // "Route each homework question to the right specialist.",
    "You are a homework triage agent. Read the user's question and hand it off to the most appropriate specialist: History tutor for history, Math tutor for math, Code tutor for programming, and Writing tutor for writing. If a question overlaps categories, choose the tutor that best matches the main task.",
  handoffs: [historyTutor, mathTutor, codeTutor, writingTutor],
});

const result = await run(
  triageAgent,
  //   "Who was the first president of the United States?",
  //   "how to get current time by code? e.g. 19:30:12",
  "Write a small essay (no more than 100 words) to introduce Kubernetes.",
);

console.log(result.finalOutput);
console.log(result.lastAgent?.name);
