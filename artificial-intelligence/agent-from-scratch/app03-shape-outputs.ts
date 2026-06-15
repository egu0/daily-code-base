import { Agent, run } from "@openai/agents";
import { z } from "zod";

const calendarEvent = z.object({
  name: z.string(),
  date: z.string(),
  participants: z.array(z.string()),
});

const agent = new Agent({
  name: "Calendar extractor",
  instructions: "Extract calendar events from text.",
  outputType: calendarEvent, // 指定输出类型
});

const result = await run(agent, "Dinner with Priya and Sam on Friday.");

console.log(result.finalOutput);
// {
//   name: "Dinner with Priya and Sam",
//   date: "Friday",
//   participants: [ "Priya", "Sam" ],
// }
