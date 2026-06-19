const state = {
  sessionId: null,
  controller: null,
  assistantMessages: new Map(),
  tools: [],
  toolItems: new Map(),
  currentReasoning: null,
  autoScrollEnabled: true,
};

const AUTO_SCROLL_THRESHOLD = 36;

const els = {
  sessionLabel: document.querySelector("#sessionLabel"),
  messages: document.querySelector("#messages"),
  promptForm: document.querySelector("#promptForm"),
  promptInput: document.querySelector("#promptInput"),
  sendButton: document.querySelector("#sendButton"),
  newSessionButton: document.querySelector("#newSessionButton"),
  stopButton: document.querySelector("#stopButton"),
  statusLabel: document.querySelector("#statusLabel"),
  tools: document.querySelector("#tools"),
  contextFill: document.querySelector("#contextFill"),
  contextStats: document.querySelector("#contextStats"),
};

if (window.marked) {
  marked.setOptions({
    breaks: true,
    gfm: true,
  });
}

function renderMarkdown(text) {
  if (!window.marked || !window.DOMPurify) {
    return escapeHtml(text).replaceAll("\n", "<br>");
  }
  return DOMPurify.sanitize(marked.parse(text));
}

function isMessagesNearBottom() {
  const distanceFromBottom =
    els.messages.scrollHeight - els.messages.scrollTop - els.messages.clientHeight;
  return distanceFromBottom <= AUTO_SCROLL_THRESHOLD;
}

function scrollMessagesToBottom() {
  if (!state.autoScrollEnabled) return;

  requestAnimationFrame(() => {
    if (!state.autoScrollEnabled) return;
    els.messages.scrollTop = els.messages.scrollHeight;
    requestAnimationFrame(() => {
      if (!state.autoScrollEnabled) return;
      els.messages.scrollTop = els.messages.scrollHeight;
    });
  });
}

function scheduleMarkdownRender(content) {
  if (content.renderFrame) return;
  content.renderFrame = requestAnimationFrame(() => {
    content.innerHTML = renderMarkdown(content.markdownText || "");
    content.renderFrame = null;
    scrollMessagesToBottom();
  });
}

function appendMessage(role, text, messageId = null) {
  const row = document.createElement("div");
  row.className = `message-row ${role}`;
  row.innerHTML = `
    <div class="message">
      <div class="content"></div>
    </div>
  `;
  const content = row.querySelector(".content");
  if (role === "assistant") {
    content.classList.add("markdown-body");
    content.markdownText = text;
    content.innerHTML = renderMarkdown(text);
  } else {
    content.textContent = text;
  }
  els.messages.appendChild(row);
  scrollMessagesToBottom();
  if (messageId) {
    state.assistantMessages.set(messageId, content);
  }
  return row;
}

function clearMessages() {
  els.messages.innerHTML = "";
  state.assistantMessages.clear();
  state.toolItems.clear();
  state.currentReasoning = null;
}

function appendAssistantChunk(messageId, text) {
  let content = state.assistantMessages.get(messageId);
  if (!content) {
    content = appendMessage("assistant", "", messageId).querySelector(
      ".content",
    );
  }
  content.markdownText = (content.markdownText || "") + text;
  scheduleMarkdownRender(content);
}

function appendTraceDetails(kind, title, meta = "") {
  const row = document.createElement("div");
  row.className = `message-row trace ${kind}`;
  row.innerHTML = `
    <details class="trace-details">
      <summary>
        <span class="trace-title"></span>
        <span class="trace-meta"></span>
      </summary>
      <div class="trace-body"></div>
    </details>
  `;
  row.querySelector(".trace-title").textContent = title;
  row.querySelector(".trace-meta").textContent = meta;
  els.messages.appendChild(row);
  scrollMessagesToBottom();
  return {
    row,
    details: row.querySelector(".trace-details"),
    meta: row.querySelector(".trace-meta"),
    body: row.querySelector(".trace-body"),
  };
}

function enabledTools() {
  return Array.from(
    els.tools.querySelectorAll("input[type='checkbox']:checked"),
  ).map((input) => input.value);
}

function renderTools(tools, enabledTools) {
  const enabledSet = new Set(enabledTools !== undefined ? enabledTools : tools.map((t) => t.name));
  els.tools.innerHTML = "";
  tools.forEach((tool) => {
    const label = document.createElement("label");
    label.className = "tool-row";
    label.innerHTML = `
      <input type="checkbox" value="${escapeHtml(tool.name)}" ${enabledSet.has(tool.name) ? "checked" : ""} />
      <span>
        <strong>${escapeHtml(tool.name)}</strong>
        <span>${escapeHtml(tool.description)}</span>
      </span>
    `;
    els.tools.appendChild(label);
  });
}

async function syncToolSelection() {
  if (!state.sessionId) return;
  try {
    await fetch(`/api/sessions/${state.sessionId}/tools`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ enabledTools: enabledTools() }),
    });
  } catch {
    // Best-effort sync; silently ignore network errors
  }
}

function setRunning(running) {
  els.sendButton.disabled = running;
  els.promptInput.disabled = running;
  els.stopButton.disabled = !running;
  if (running) {
    setStatus("Streaming", "running");
  }
}

function setStatus(text, tone = "") {
  els.statusLabel.textContent = text;
  els.statusLabel.className = `status-pill ${tone}`.trim();
}

function ensureToolItem(toolCallId, name = "tool", args = {}) {
  let item = state.toolItems.get(toolCallId);
  if (item) return item;

  const trace = appendTraceDetails("tool", `Tool call: ${name}`, "pending");
  trace.body.innerHTML = `
    <div class="trace-section">Arguments</div>
    <pre class="args"></pre>
    <div class="trace-section result-label" hidden>Result</div>
    <pre class="result"></pre>
  `;
  trace.body.querySelector(".args").textContent = JSON.stringify(args, null, 2);
  item = {
    row: trace.row,
    status: trace.meta,
    result: trace.body.querySelector(".result"),
    resultLabel: trace.body.querySelector(".result-label"),
  };
  state.toolItems.set(toolCallId, item);
  return item;
}

function updateTool(data) {
  const item = ensureToolItem(data.toolCallId, data.name, data.args);
  if (data.status) {
    item.status.textContent = data.status;
    item.row.dataset.status = data.status;
    if (data.status === "in_progress") {
      setStatus("Tool running", "running");
    }
  }
  if (data.result !== undefined) {
    item.result.textContent = data.result;
    item.resultLabel.hidden = false;
  }
}

function parseToolArgs(rawArgs) {
  if (!rawArgs) return {};
  try {
    return JSON.parse(rawArgs);
  } catch {
    return {};
  }
}

function appendToolCallsFromMessage(message) {
  (message.tool_calls || []).forEach((toolCall) => {
    updateTool({
      toolCallId: toolCall.id,
      name: toolCall.function?.name || "tool",
      args: parseToolArgs(toolCall.function?.arguments),
      status: "completed",
    });
  });
}

function appendToolResultFromMessage(message) {
  updateTool({
    toolCallId: message.tool_call_id,
    status: "completed",
    result: message.content || "",
  });
}

function renderHistory(messages) {
  clearMessages();
  state.autoScrollEnabled = true;

  messages.forEach((message) => {
    if (message.role === "system") return;
    if (message.role === "user") {
      appendMessage("user", message.content || "");
    } else if (message.role === "assistant") {
      if (message.reasoning_content) {
        appendTraceDetails("reasoning", "Reasoning", "saved").body.textContent =
          message.reasoning_content;
      }
      if (message.content) {
        appendMessage("assistant", message.content);
      }
      appendToolCallsFromMessage(message);
    } else if (message.role === "tool") {
      appendToolResultFromMessage(message);
    }
  });

  scrollMessagesToBottom();
}

function appendReasoning(text) {
  if (!state.currentReasoning) {
    state.currentReasoning = appendTraceDetails(
      "reasoning",
      "Reasoning",
      "stream",
    );
  }
  state.currentReasoning.body.textContent += text;
  scrollMessagesToBottom();
}

function formatTokenCount(value) {
  if (value === null || value === undefined) return "未知";
  return Number(value).toLocaleString();
}

function updateUsage(data) {
  const used = data.used;
  const size = data.size;
  const hasUsage = used !== null && used !== undefined;
  const hasWindow = size !== null && size !== undefined;

  if (hasUsage && hasWindow && size > 0) {
    const percent = Math.min(100, Math.round((used / size) * 100));
    els.contextFill.style.width = `${percent}%`;
  } else {
    els.contextFill.style.width = "0%";
  }

  els.contextStats.textContent = `${formatTokenCount(used)} / ${formatTokenCount(size)} tokens · ${data.messageCount || 0} messages`;
}

function handleEvent(item) {
  const { event, data } = item;
  if (event === "agent_message_chunk") {
    setStatus("Streaming", "running");
    appendAssistantChunk(data.messageId, data.text);
  } else if (event === "reasoning_chunk") {
    setStatus("Reasoning", "running");
    appendReasoning(data.text);
  } else if (event === "tool_call" || event === "tool_call_update") {
    updateTool(data);
  } else if (event === "usage_update") {
    updateUsage(data);
  } else if (event === "error") {
    setStatus("Error", "error");
    appendMessage("assistant", `Error: ${data.message}`);
  } else if (event === "turn_done") {
    setStatus(data.stopReason === "cancelled" ? "Cancelled" : "Idle");
    setRunning(false);
  }
}

async function readNdjson(response) {
  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let buffer = "";

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split("\n");
    buffer = lines.pop();
    for (const line of lines) {
      if (!line.trim()) continue;
      handleEvent(JSON.parse(line));
    }
  }

  if (buffer.trim()) {
    handleEvent(JSON.parse(buffer));
  }
}

async function sendPrompt(prompt) {
  state.autoScrollEnabled = true;
  appendMessage("user", prompt);
  state.toolItems.clear();
  state.currentReasoning = null;
  state.controller = new AbortController();
  setRunning(true);

  try {
    const response = await fetch(
      `/api/sessions/${state.sessionId}/prompt/stream`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt, enabledTools: enabledTools() }),
        signal: state.controller.signal,
      },
    );
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || response.statusText);
    }
    await readNdjson(response);
  } catch (error) {
    if (error.name !== "AbortError") {
      setStatus("Error", "error");
      appendMessage("assistant", `Error: ${error.message}`);
    }
  } finally {
    setRunning(false);
    state.controller = null;
    if (
      !els.statusLabel.classList.contains("error") &&
      els.statusLabel.textContent !== "Cancelled"
    ) {
      setStatus("Idle");
    }
  }
}

async function stopTurn() {
  if (state.controller) {
    state.controller.abort();
  }
  setStatus("Cancelling", "running");
  await fetch(`/api/sessions/${state.sessionId}/cancel`, { method: "POST" });
  setRunning(false);
  setStatus("Cancelled");
}

function sessionUrl(sessionId) {
  const url = new URL(window.location.href);
  url.pathname = "/";
  url.search = "";
  url.searchParams.set("sid", sessionId);
  return url.toString();
}

function currentSessionIdFromUrl() {
  const querySessionId = new URLSearchParams(window.location.search).get("sid");
  if (querySessionId) return querySessionId;
  const pathMatch = window.location.pathname.match(/^\/sid=([^/]+)$/);
  return pathMatch ? decodeURIComponent(pathMatch[1]) : null;
}

function setSession(session) {
  state.sessionId = session.sessionId;
  els.sessionLabel.textContent = state.sessionId;
  renderHistory(session.messages || []);
  updateUsage(session.usage || {});
  if (state.tools.length > 0) {
    renderTools(state.tools, session.enabledTools);
  }
}

async function createSession() {
  const response = await fetch("/api/sessions", { method: "POST" });
  if (!response.ok) {
    throw new Error("Failed to create session");
  }
  return response.json();
}

async function loadSession(sessionId) {
  const response = await fetch(`/api/sessions/${encodeURIComponent(sessionId)}`);
  if (!response.ok) {
    throw new Error(`Session not found: ${sessionId}`);
  }
  return response.json();
}

async function openNewSession() {
  const session = await createSession();
  window.open(sessionUrl(session.sessionId), "_blank", "noopener");
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

async function init() {
  const toolsResp = await fetch("/api/tools");
  const toolsPayload = await toolsResp.json();
  state.tools = toolsPayload.tools;

  const sessionId = currentSessionIdFromUrl();
  const session = sessionId ? await loadSession(sessionId) : await createSession();
  setSession(session);

  if (!sessionId) {
    window.history.replaceState({}, "", sessionUrl(session.sessionId));
  }

  els.tools.addEventListener("change", (event) => {
    if (event.target.type === "checkbox") {
      syncToolSelection();
    }
  });
}

els.promptForm.addEventListener("submit", (event) => {
  event.preventDefault();
  const prompt = els.promptInput.value.trim();
  if (!prompt) return;
  els.promptInput.value = "";
  sendPrompt(prompt);
});

els.promptInput.addEventListener("keydown", (event) => {
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    els.promptForm.requestSubmit();
  }
});

els.messages.addEventListener(
  "scroll",
  () => {
    state.autoScrollEnabled = isMessagesNearBottom();
  },
  { passive: true },
);

els.newSessionButton.addEventListener("click", () => {
  openNewSession().catch((error) => {
    setStatus("Error", "error");
    appendMessage("assistant", `Error: ${error.message}`);
  });
});

els.stopButton.addEventListener("click", stopTurn);

init().catch((error) => {
  els.sessionLabel.textContent = "Failed to start";
  appendMessage("assistant", `Error: ${error.message}`);
});
