# Agentic AI - Andrew Ng

- [course](https://www.deeplearning.ai/courses/agentic-ai)
- [slides & code](https://github.com/nhatnam2609/agentic_ai_andrew)

Modules in this course:

- Introduction to Agentic Workflows
- Reflection Design Pattern
- Tool Use
- Practical Tips for Building Agentic AI
- Patterns for Highly Autonomous Agents

quickstart:

```bash
export TAVILY_API_KEY=''
export OPENAI_API_KEY=''
export ANTHROPIC_API_KEY=''

uv run --with jupyter jupyter lab
```

## ugl_2 of Module 3

start backend:

```bash
cd m3/ugl_2/email_server
uv run uvicorn email_service:app --timeout-keep-alive 1200
```

## Module 1 Labs

Research Agent service for the Agentic Workflow course. [agents.py](https://github.com/https-deeplearning-ai/agentic-ai-public/blob/main/src/agents.py)

## Module 2 Labs

1. Visualization Agent with Reflection - Chart Improvement. [lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m2/ungraded/ugl_1)
2. SQL Agent with Reflection - Query Improvement. [lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m2/ungraded/ugl_2)
3. Reflection in a Research Agent. [lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m2/graded)

## Module 3 Labs

1. Turning functions into tools. [lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m3/ugl_1)
2. Email assistant workflow. [lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m3/ugl_2)
3. Tool Use in Reflective Research Agents. [lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m3/gl_1)

## Module 4 - 关于构建代理工作流的实用建议

### End To End Evaluations

### Error Analysis

1. Examine **traces** to better understand each step in the workflow.
2. Counting up the errors in a spreadsheet.

### Component Level Evaluations

### Improving Components Performance

### Lab - Adding a component-level eval to the research workflow

[lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m4/ugl_1)

### Latency and Cost Optimization

### Summary

Building 和 Analyse 交叉进行

```text
Build                              Analyse

build end-to-end system.           examine outputs; traces
improve individual component.      build evals; compute metrics
                                   error analysis
                                   component-level evals
```

Andrew Ng 分享构建代理工作流的经验：

1. start by quickly building an end-to-end system, maybe even a quick and dirty implementation, then start to **examine the final outputs** of the end-to-end system, or also **read through traces** to get a sense of where it's doing well, where it's doing poorly. Based on even just looking at traces, sometimes this will give me a gut sense of which individual components I might want to improve. And so I might go tune some individual components or keep tuning the overall end-to-end system.
2. As my system starts to mature a little bit more, then beyond just manually examining a few outputs and reading through traces, I might start to **build evals** and have a small data set, maybe just 10-20 examples, to compute metrics, at least on end-to-end performance. And this then further helps me have a more refined perspective on how to improve the end-to-end system or how to improve individual components.
3. As it matures _even further_, my analysis then becomes maybe even more disciplined, where I start to do **error analysis** and look through the components and try to count up how frequently individual components led to subpar outputs. And this more rigorous analysis then lets me be even more focused in deciding what components to work on next or inspire ideas for improving the overall end-to-end system.
4. And then eventually, when it's _even more mature_ to drive more efficient improvements at the **component level**, that's when I might also build component-level evals. And so the workflow of building an agentic system often goes back and forth.
5. It's not a linear process. We sometimes tune the end-to-end system, then do some error analysis, then improve a component for a bit, then tune the component-level evals. And I tend to bounce back and forth between these two types of techniques.

简单概括：

- 快速搭建起来端到端系统。简单测试，查看结果和中间追踪，根据直觉做系统微调
- 更进一步，构建评估，10-20 个样本，查看端到端的性能表现，根据直觉做系统微调
- 更进一步，进行错误分析，记录单个组件的表现，根据直觉做系统微调
- 更进一步，做组件级别的评估，根据评估结果和直觉做系统微调

## Module 5

### Planning Design Pattern

让 LLM 在开始工作前制定一个计划，然后根据计划逐步解决问题

一个示例：
![916](assets/Pasted%20image%2020260615161407.png)

解决的问题：工作步骤不用硬编码，可以适配很多任务
出现的问题：系统的控制性差；需要维护大量 Tools

### planning and executing LLM plans

关于控制 LLM 生成计划的输出格式：

- text。原生方式
- json。根据指定的json path生成json块
- code。生成可执行的代码

---

**formatting plan as json**

![925](assets/Pasted%20image%2020260615162844.png)

**planning with code execution**

![928](assets/Pasted%20image%2020260615162735.png)

结论：**优先让 LLM 在 planning 步骤生成代码解决问题**

![916](assets/Pasted%20image%2020260615160712.png)

### Lab 1 - Customer Service Pipeline

[lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m5/ugl_1)

- formatting plan as JSON
- formatting plan as CODE

### Multi-Agent Workflow

### Lab 2 - Market Research Team

[lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m5/ugl_2)

### Lab 3 - Research Workflows

[lab](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course/m5/gl_1)
