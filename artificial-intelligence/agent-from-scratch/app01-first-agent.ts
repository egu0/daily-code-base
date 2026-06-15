import { Agent, run } from "@openai/agents";

const agent = new Agent({
  name: "History tutor",
  instructions: "You answer history questions clearly and concisely.",
  model: "gpt-5.4",
  modelSettings: {
    reasoning: {
      effort: "medium",
    },
  },
});

const result = await run(agent, "When did the Sovet fall?");
console.log(result.finalOutput);
