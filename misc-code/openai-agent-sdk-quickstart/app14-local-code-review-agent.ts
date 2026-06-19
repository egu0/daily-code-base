/*
任务：本地代码审查 Agent

实现一个可以审查当前目录 TypeScript 文件的 agent。

要求：
- 提供读取文件列表的工具
- 提供读取单个文件内容的工具
- 让 agent 输出 issues、risk、suggestedPatch
- 控制单次读取内容，避免把整个项目一次性塞给模型

练习点：
- repo 上下文读取
- 结构化审查结果
- 工具返回内容裁剪
- 从玩具 agent 过渡到真实开发辅助 agent
*/

import { Agent, run, tool } from "@openai/agents";
import { createReadStream } from "node:fs";
import { readdir, stat } from "node:fs/promises";
import { join, relative, resolve } from "node:path";
import { createInterface } from "node:readline";
import { z } from "zod";

const SOURCE_EXTENSIONS = new Set([".ts", ".tsx"]);
const SKIP_DIRS = new Set([
  ".git",
  "node_modules",
  "dist",
  "build",
  "coverage",
  ".next",
]);
const MAX_FILES = 200;
const MAX_LINES = 120;

const listFilesOfCurrentDirectory = tool({
  name: "list_files_of_current_directory",
  description: "List all ts files of current directory.",
  parameters: z.object({}),
  async execute({}) {
    const root = resolve(".");
    const pending = [root];
    const files: string[] = [];

    while (pending.length > 0 && files.length < MAX_FILES) {
      const current = pending.pop()!;
      const entries = await readdir(current, { withFileTypes: true });

      for (const entry of entries) {
        if (files.length >= MAX_FILES) {
          break;
        }

        const fullPath = join(current, entry.name);

        if (entry.isDirectory()) {
          if (!SKIP_DIRS.has(entry.name)) {
            pending.push(fullPath);
          }
          continue;
        }

        if (!entry.isFile()) {
          continue;
        }

        const extension = entry.name.slice(entry.name.lastIndexOf("."));
        if (SOURCE_EXTENSIONS.has(extension)) {
          files.push(relative(root, fullPath));
        }
      }
    }

    return {
      files,
      truncated: files.length >= MAX_FILES,
      maxFiles: MAX_FILES,
    };
  },
});

const readFileByLines = tool({
  name: "read_file_by_lines",
  description: "Read file content by range (startLine, endLine).",
  parameters: z.object({
    filepath: z.string(),
    startLine: z.number(),
    endLine: z.number(),
  }),
  async execute({ filepath, startLine, endLine }) {
    const fullpath = resolveInsideRoot(filepath);

    const start = Math.max(1, Math.floor(startLine));
    const end = Math.min(
      Math.max(start, Math.floor(endLine)),
      start + MAX_LINES - 1,
    );
    const stream = createReadStream(resolve(fullpath), { encoding: "utf8" });
    const lines = createInterface({
      input: stream,
      crlfDelay: Infinity,
    });
    const selected: string[] = [];
    let lineNumber = 0;

    try {
      for await (const line of lines) {
        lineNumber += 1;

        if (lineNumber < start) {
          continue;
        }

        if (lineNumber > end) {
          break;
        }

        selected.push(`${lineNumber}: ${line}`);
      }
    } finally {
      lines.close();
      stream.destroy();
    }

    return {
      filepath,
      startLine: start,
      endLine: end,
      maxLines: MAX_LINES,
      content: selected.join("\n"),
    };
  },
});

const ROOT = resolve(".");

function resolveInsideRoot(filepath: string) {
  const fullPath = resolve(ROOT, filepath);
  const relativePath = relative(ROOT, fullPath);

  if (
    relativePath.startsWith("..") ||
    relativePath === "" ||
    fullPath === ROOT
  ) {
    throw new Error("File path must be inside current directory.");
  }
  const extension = filepath.slice(filepath.lastIndexOf("."));
  if (!SOURCE_EXTENSIONS.has(extension)) {
    throw new Error("File extension must be .ts or .tsx.");
  }

  return fullPath;
}

const getFileOverview = tool({
  name: "get_file_overview",
  description: "Get file overview, including total lines, size, readable.",
  parameters: z.object({
    filepath: z.string(),
  }),
  async execute({ filepath }) {
    const fullPath = resolveInsideRoot(filepath);

    try {
      const fileStat = await stat(fullPath);

      if (!fileStat.isFile()) {
        return {
          filepath: fullPath,
          totalLines: 0,
          size: fileStat.size,
          readable: false,
        };
      }

      const stream = createReadStream(fullPath, { encoding: "utf8" });
      let totalLines = 0;
      let hasBytes = false;
      let endsWithNewline = false;

      for await (const chunk of stream) {
        hasBytes = true;
        endsWithNewline = chunk.endsWith("\n");
        totalLines += chunk.split("\n").length - 1;
      }

      if (hasBytes && !endsWithNewline) {
        totalLines += 1;
      }

      return {
        filepath: fullPath,
        totalLines,
        size: fileStat.size,
        readable: true,
      };
    } catch (error) {
      return {
        filepath: fullPath,
        totalLines: 0,
        size: 0,
        readable: false,
        error: error instanceof Error ? error.message : String(error),
      };
    }
  },
});

const reviewOutput = z.object({
  issues: z.array(
    z.object({
      file: z.string(),
      line: z.number().optional(),
      severity: z.enum(["low", "medium", "high"]),
      message: z.string(),
    }),
  ),
  risk: z.string(),
  suggestedPatch: z.string(),
});

const agent = new Agent({
  name: "Typescript files reviewer",
  instructions:
    "审查当前工作目录下的 TypeScript 文件。先调用 list_files_of_current_directory，再按需调用 read_file_by_lines 等工具。最终输出 issues、risk、suggestedPatch",
  tools: [listFilesOfCurrentDirectory, readFileByLines, getFileOverview],
  outputType: reviewOutput,
});

const result = await run(agent, "审查当前目录下的 ts 文件");

console.log(result.finalOutput);
