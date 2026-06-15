/*
任务：Obsidian 笔记检索 Agent

实现一个可以搜索默认 Obsidian vault 的 agent。

默认 vault：
/Users/jordanguo/Library/Mobile Documents/iCloud~md~obsidian/Documents/obsidian-repo-001

要求：
- 搜索 .md 文件
- 根据关键词找出相关笔记
- 总结匹配内容
- 返回相关文件路径

练习点：
- 本地文件读取工具
- 控制工具返回内容大小
- 把外部知识交给 agent 使用
*/

import { tool, Agent, run } from "@openai/agents";
import { z } from "zod";
import { readdir, readFile } from "node:fs/promises";
import path from "node:path";

const VAULT_PATH =
  "/Users/jordanguo/Library/Mobile Documents/iCloud~md~obsidian/Documents/obsidian-repo-001";
const MAX_RESULTS = 5;
const MAX_SNIPPET_LENGTH = 300;

function extractTitle(filePath: string, content: string) {
  const heading = content.match(/^#\s+(.+)$/m);
  if (heading) {
    return heading[1];
  }

  return path.basename(filePath, ".md");
}

async function searchNotes(query: string) {
  const normalizedQuery = query.trim().toLowerCase();
  if (!normalizedQuery) {
    return [];
  }

  const entries = await readdir(VAULT_PATH, {
    recursive: true,
    withFileTypes: true,
  });

  const mdFiles = entries.filter((entry) => {
    return !entry.isDirectory() && entry.name.toLowerCase().endsWith(".md");
  });

  const results: Array<{ path: string; title: string; snippet: string }> = [];
  const lowerQuery = query.toLowerCase();

  for (const entry of mdFiles) {
    const fullFilePath = path.join(entry.parentPath, entry.name);

    let content: string;
    try {
      //Obsidian vault 里可能有 iCloud 未下载文件、权限问题、临时文件。现在一个 readFile 失败会让整个工具失败。可以包一层：
      content = await readFile(fullFilePath, "utf-8");
    } catch {
      continue;
    }

    const lowerContent = content.toLowerCase();

    if (
      entry.name.toLowerCase().includes(lowerQuery) ||
      lowerContent.includes(lowerQuery)
    ) {
      const index = lowerContent.indexOf(lowerQuery);

      const start = Math.max(0, index - 80);
      const end = Math.min(content.length, index + MAX_SNIPPET_LENGTH);
      const snippet = content.substring(start, end);

      results.push({
        path: fullFilePath,
        title: extractTitle(fullFilePath, content),
        snippet,
      });
    }

    if (results.length >= MAX_RESULTS) {
      break;
    }
  }

  return results;
}

const searchObsidianNotes = tool({
  name: "search_notes",
  description: "Search Obsidian notes by keyword.",
  parameters: z.object({
    query: z.string(),
  }),
  async execute({ query }) {
    const results = await searchNotes(query);

    if (results.length === 0) {
      return "Found 0 matching notes.";
    }

    return results
      .map(
        (item, index) => `${index + 1}. ${item.path}
Title: ${item.title}
Snippet:
${item.snippet}`,
      )
      .join("\n\n");
  },
});

const agent = new Agent({
  name: "Obsidian Note Lookup Agent",
  instructions:
    "You answer questions about the user's Obsidian notes. Use search_notes to find relevant notes before answering. Include related file paths in your answer. If no notes match, say that no matching notes were found. Base your answer only on the search_notes results. Do not invent file paths or note contents.",
  tools: [searchObsidianNotes],
  model: "gpt-5.4",
  modelSettings: {
    reasoning: {
      effort: "medium",
    },
  },
});

const result = await run(
  agent,
  "Search my notes for OpenAI and summarize the relevant notes.",
);

console.log(result.finalOutput);
