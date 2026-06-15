/*
任务：安全命令规划 Agent

实现一个只生成 shell 命令计划、不直接执行命令的 agent。

要求：
- 根据用户目标生成命令步骤
- 输出每一步的 purpose 和 command
- 使用 guardrail 阻止危险命令建议
- 危险命令示例：rm -rf、git reset --hard、磁盘格式化、权限破坏类命令

练习点：
- guardrails
- 结构化命令计划
- 在高风险场景中约束 agent 行为

参考：
- https://developers.openai.com/api/docs/guides/agents/guardrails-approvals
*/

import { Agent, OutputGuardrailTripwireTriggered, run } from "@openai/agents";
import { z } from "zod";

const guardrailAgent = new Agent({
  name: "Shell command safety check",
  instructions:
    "Detect whether a shell command plan contains dangerous commands. Flag commands that could cause irreversible data loss, destroy Git history, format disks, damage permissions, modify system-critical paths, or bypass safety controls. Examples include rm -rf, git reset --hard, git clean -fd, mkfs, dd to a disk device, chmod -R 777 /, and chown -R on broad system paths.",
  outputType: z.object({
    isDangerous: z.boolean(),
    reasoning: z.string(),
  }),
});

const commandPlanOutput = z.object({
  steps: z.array(
    z.object({
      purpose: z.string(),
      command: z.string(),
    }),
  ),
});

const agent = new Agent({
  name: "Safe shell planner",
  instructions:
    "Generate a shell command plan for the user's goal. Do not execute commands or claim that you executed them. Return concise steps only. Each step must include a purpose and a command.",
  outputType: commandPlanOutput,
  outputGuardrails: [
    {
      name: "Dangerous shell command guardrail",
      async execute({ agentOutput, context }) {
        const result = await run(guardrailAgent, JSON.stringify(agentOutput), {
          context,
        });
        return {
          outputInfo: result.finalOutput,
          tripwireTriggered: result.finalOutput?.isDangerous === true,
        };
      },
    },
  ],
});

try {
  const safeResult = await run(agent, "Plan commands to inspect this project.");
  console.log(JSON.stringify(safeResult.finalOutput, null, 2));

  await run(agent, "Plan commands to permanently delete the project folder.");
} catch (error) {
  if (error instanceof OutputGuardrailTripwireTriggered) {
    console.log("Guardrail blocked a dangerous command plan.");
  } else {
    throw error;
  }
}
