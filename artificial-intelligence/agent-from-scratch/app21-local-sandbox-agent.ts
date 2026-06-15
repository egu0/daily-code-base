import { run } from "@openai/agents";
import {
  Capabilities,
  Manifest,
  SandboxAgent,
  localDir,
  skills,
} from "@openai/agents/sandbox";

import {
  UnixLocalSandboxClient,
  localDirLazySkillSource,
} from "@openai/agents/sandbox/local";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const exampleDir = dirname(fileURLToPath(import.meta.url));
const hostRepoDir = join(exampleDir, "repo");
const hostSkillsDir = join(exampleDir, "skills");

const manifest = new Manifest({
  entries: {
    repo: localDir({ src: hostRepoDir }),
  },
});

const agent = new SandboxAgent({
  name: "Sandbox engineer",
  model: "gpt-5.4",
  instructions:
    "Read `repo/task.md` before editing files. Load the `$invoice-total-fixer` skill before changing code. Stay grounded in the repository, preserve existing behavior, and mention the exact verification command you ran. If you edit files with apply_patch, paths are relative to the sandbox workspace root.",
  defaultManifest: manifest,
  capabilities: [
    ...Capabilities.default(),
    skills({
      lazyFrom: localDirLazySkillSource({
        src: hostSkillsDir,
      }),
    }),
  ],
});

// 此示例在 repo/ 下暂存本地仓库，延迟加载本地技能，
// 并允许运行器为本次运行创建 Unix 本地沙箱会话。
// 代理定义拥有清单和功能，而运行配置仅为本次运行选择沙箱客户端。

const result = await run(
  agent,
  "Open `repo/task.md`, fix the issue, run the targeted test, and summarize the change.",
  {
    sandbox: {
      client: new UnixLocalSandboxClient(),
    },
  },
);

console.log(result.finalOutput);
