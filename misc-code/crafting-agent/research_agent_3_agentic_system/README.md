# Research Workflow with Multi-Agent

Steps:

1. prepare three agents:
   1. research agent: search web, wiki and arXiv.
   2. writer agent: draft research summaries.
   3. editor agent: reflect and revise the drafts.
2. planning with planner agent: generate a plan list, each step only rely on an agent.
3. executing with executor agent: execute task (a single plan) one by one.

## Research Agent

```text
You are a research assistant with access to the following tools:
- arxiv_tool: for finding academic papers
- tavily_tool: for general web search
- wikipedia_tool: for encyclopedic knowledge

Task:
{task}

Today is {datetime.now().strftime('%Y-%m-%d')}.
```

## Writer Agent

```python
messages = [
    {"role": "system", "content": "You are a writing agent specialized in generating well-structured academic or technical content."},
    {"role": "user", "content": task}
]
```

## Editor Agent

```python
messages = [
    {"role": "system", "content": "You are an editor agent. Your job is to reflect on, critique, or improve existing drafts."},
    {"role": "user", "content": task}
]
```

## Planner Agent

```text
You are a planning agent responsible for organizing a research workflow with multiple intelligent agents.

🧠 Available agents:
- A research agent who can search the web, Wikipedia, and arXiv.
- A writer agent who can draft research summaries.
- An editor agent who can reflect and revise the drafts.

🎯 Your job is to write a clear, step-by-step research plan **as a valid Python list**, where each step is a string, e.g. '["first", "second"]'.
Each step should be atomic, executable, and must rely only on the capabilities of the above agents.

🚫 DO NOT include irrelevant tasks like "create CSV", "set up a repo", "install packages", etc.
✅ DO include real research-related tasks (e.g., search, summarize, draft, revise).
✅ DO assume tool use is available.
✅ DO NOT include explanation text — return ONLY the Python list.
✅ The final step should be to generate a Markdown document containing the complete research report.

Topic: "{topic}"
```

## Executor Agent

```python
def executor_agent(plan_steps: list[str], model: str = "deepseek:deepseek-v4-flash"):
    history = []

    for i, step in enumerate(plan_steps):

        # 根据计划信息得到需要使用的 agent 和 task 描述
        agent_decision_prompt = f"""
You are an execution manager for a multi-agent research team.

Given the following instruction, identify which agent should perform it and extract the clean task.

Return only a valid JSON object with two keys:
- "agent": one of ["research_agent", "editor_agent", "writer_agent"]
- "task": a string with the instruction that the agent should follow

Only respond with a valid JSON object. Do not include explanations or markdown formatting.

Instruction: "{step}"
"""
        response = client.chat.completions.create(
            model=model,
            messages=[{"role": "user", "content": agent_decision_prompt}],
            temperature=0,
        )

        raw_content = response.choices[0].message.content
        cleaned_json = clean_json_block(raw_content)
        agent_info = json.loads(cleaned_json)

        agent_name = agent_info["agent"]
        task = agent_info["task"]

        # 调用对应 agent，并添加上下文
        context = "\n".join([
            f"Step {j+1} executed by {a}:\n{r}"
            for j, (s, a, r) in enumerate(history)
        ])
        enriched_task = f"""You are {agent_name}.

Here is the context of what has been done so far:
{context}

Your next task is:
{task}
"""
        print(f"\n🛠️ Executing with agent: `{agent_name}` on task: {task}")

        if agent_name in agent_registry:
            output = agent_registry[agent_name](enriched_task)
            history.append((step, agent_name, output))
        else:
            output = f"⚠️ Unknown agent: {agent_name}"
            history.append((step, agent_name, output))

        print(f"✅ Output:\n{output}")

    return history
```
