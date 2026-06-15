## Graded Lab: Reflection in a Research Agent

In this graded lab, you’ll implement a simple **agentic workflow** designed to simulate reflective thinking in a writing task. This is one building block of a more complex research agent that will be constructed throughout the course.

### Objective

Build a three-step workflow where an LLM writes an essay draft, critiques it, and rewrites it. 

* **Step 1 – Drafting:** Call the LLM to generate an initial draft of an essay based on a simple prompt.
* **Step 2 – Reflection:** Reflect on the draft using a reasoning step. (Optionally, this can be done with a different model.)
* **Step 3 – Revision:** Apply the feedback from the reflection to generate a revised version of the essay.
