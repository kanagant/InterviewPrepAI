import { request } from './client'

export interface CreateSessionRequest {
  jobDescription: string
  resumeText: string
  company?: string
  role?: string
}

export interface SessionResponse {
  id: number
  jobDescription: string
  resumeText: string
  company?: string
  role?: string
  createdAt: string
}

export interface BehavioralQuestion {
  question: string
  rationale: string
  answer: string
}

export interface PracticeProblem {
  name: string
  platform: string
  difficulty: string
  topics: string[]
  relevance: string
  approachHint: string
}

export interface OaContent {
  disclaimer: string
  researchSummary?: string | null
  problems: PracticeProblem[]
}

export type ArtifactType = 'BEHAVIORAL' | 'TECHNICAL' | 'OA'

export type GenerateArtifactResponse =
  | { id: number; sessionId: number; type: 'BEHAVIORAL' | 'TECHNICAL'; content: BehavioralQuestion[]; createdAt: string }
  | { id: number; sessionId: number; type: 'OA'; content: OaContent; createdAt: string }

export function createSession(payload: CreateSessionRequest): Promise<SessionResponse> {
  return request<SessionResponse>('/sessions', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function generateArtifact(sessionId: number, type: ArtifactType): Promise<GenerateArtifactResponse> {
  return request<GenerateArtifactResponse>(`/sessions/${sessionId}/generate`, {
    method: 'POST',
    body: JSON.stringify({ type }),
  })
}

export function listSessions(): Promise<SessionResponse[]> {
  return request<SessionResponse[]>('/sessions')
}

export function getSession(sessionId: number): Promise<SessionResponse> {
  return request<SessionResponse>(`/sessions/${sessionId}`)
}

export function listArtifacts(sessionId: number): Promise<GenerateArtifactResponse[]> {
  return request<GenerateArtifactResponse[]>(`/sessions/${sessionId}/artifacts`)
}
