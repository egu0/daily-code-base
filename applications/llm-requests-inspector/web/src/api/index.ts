import axios from "axios";

const api = axios.create({
  baseURL: "/api",
});

export interface RequestItem {
  name: string;
  model: string;
}

export interface Usage {
  prompt_tokens: number;
  completion_tokens: number;
  cost: number;
}

export interface MessageContent {
  mode: "markdown" | "json";
  data: string;
}

export interface Message {
  role: string;
  content: MessageContent[];
}

export interface RequestDetail {
  name: string;
  model: string;
  messages: Message[];
  resp_messages: Message[];
  usage: Usage;
}

export interface ContentResponse {
  content: string;
}

export function fetchRequests() {
  return api.get<RequestItem[]>("/requests");
}

export function fetchRequestDetail(reqId: string) {
  return api.get<RequestDetail>(`/requests/${encodeURIComponent(reqId)}`);
}
