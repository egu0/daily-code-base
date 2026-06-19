/*
任务：本地 Todo 工具 Agent

实现一个可以管理待办事项的 agent。

要求：
- 提供 add_todo 工具
- 提供 list_todos 工具
- 提供 complete_todo 工具
- 数据可以先保存在内存中，后续再改为 JSON 文件

练习点：
- function tools
- tool parameters
- Zod 参数校验
- 工具执行结果如何影响 agent 回复
*/

import { run, Agent, tool } from "@openai/agents";
import { z } from "zod";

type Todo = {
  id: number;
  text: string;
  completed: boolean;
};

const todos: Todo[] = [];
let nextId = 1;

const addTodo = tool({
  name: "add_todo",
  description: "Add a new item to the local todo list.",
  parameters: z.object({ text: z.string() }),
  async execute({ text }) {
    const todo = {
      id: nextId++,
      text,
      completed: false,
    };
    todos.push(todo);
    return `Added todo #${todo.id}: ${todo.text}`;
  },
});

const listTodos = tool({
  name: "list_todos",
  description: "List all items in the local todo list.",
  parameters: z.object(),
  async execute() {
    if (todos.length === 0) {
      return "No todos yet.";
    }
    return todos
      .map(
        (item) => `${item.id}. [${item.completed ? "x" : " "}] ${item.text}\n`,
      )
      .join("\n");
  },
});

const completeTodo = tool({
  name: "complete_todo",
  description: "Mark a todo item as completed by ID.",
  parameters: z.object({ id: z.number() }),
  async execute({ id }) {
    const todo = todos.find((item) => item.id === id);
    if (todo) {
      todo.completed = true;
      return `Completed todo #${todo.id}: ${todo.text}`;
    } else {
      return `Todo #${id} was not found.`;
    }
  },
});

const agent = new Agent({
  name: "Todo Manager",
  instructions:
    "You manage a local todo list. Use the available tools to add, list, and complete todos. Do not invent todo IDs; call list_todos if you need to inspect current todos.",
  tools: [addTodo, listTodos, completeTodo],
  model: "gpt-5.4",
  modelSettings: {
    reasoning: {
      effort: "medium",
    },
  },
});

await run(agent, "Add a todo: read the Agent SDK docs.");
await run(agent, "Add another todo: implement app09.");
const result = await run(agent, "Show my todos.");
console.log(result.finalOutput);

const completed = await run(agent, "Mark todo 1 complete.");
console.log(completed.finalOutput);

const final = await run(agent, "Show my todos.");
console.log(final.finalOutput);
