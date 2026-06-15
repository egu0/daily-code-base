/*
任务：Manager + 子 Agent 工具

实现一个 manager agent，并把其他 agent 作为工具交给它使用。

要求：
- researcherAgent 负责整理事实
- flashcardAgent 负责生成学习卡片
- managerAgent 负责拆解任务并调用子 agent 工具
- 最终输出一组可复习的学习卡片

练习点：
- agent.asTool
- manager-style orchestration
- 对比 manager 控制流和 handoff 控制流
*/

import { Agent, run } from "@openai/agents";

const researcherAgent = new Agent({
  name: "整理事实",
  instructions: "整理事实，应至少包括时间、原因、关键事件和结果",
});

const researchTool = researcherAgent.asTool({
  toolName: "research_tool",
  toolDescription: "整理事实，应至少包括时间、原因、关键事件和结果",
});

const flashcardAgent = new Agent({
  name: "生成学习卡片",
  instructions:
    "Create concise study flashcards from the provided facts. Output 5 cards in Markdown. Each card must have Question, Answer, and Key point.",
});

const flashcardTool = flashcardAgent.asTool({
  toolName: "flashcard_tool",
  toolDescription:
    "Create concise study flashcards from the provided facts. Output 5 cards in Markdown. Each card must have Question, Answer, and Key point.",
});

const managerAgent = new Agent({
  name: "Manager",
  instructions:
    "You are a manager agent. For every user topic, first call research_tool to gather key facts. Then call flashcard_tool using the research result. Return only the final flashcards, not the intermediate research.",
  tools: [researchTool, flashcardTool],
});

const result = await run(managerAgent, "简单介绍一下第三次工业革命");
console.log(result.finalOutput);
