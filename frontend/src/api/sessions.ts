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
}

export interface GenerateArtifactResponse {
  id: number
  sessionId: number
  type: 'BEHAVIORAL' | 'TECHNICAL' | 'OA'
  content: BehavioralQuestion[]
  createdAt: string
}

export function createSession(payload: CreateSessionRequest): Promise<SessionResponse> {
  return request<SessionResponse>('/sessions', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function generateBehavioral(sessionId: number): Promise<GenerateArtifactResponse> {
  return request<GenerateArtifactResponse>(`/sessions/${sessionId}/generate`, {
    method: 'POST',
    body: JSON.stringify({ type: 'BEHAVIORAL' }),
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
