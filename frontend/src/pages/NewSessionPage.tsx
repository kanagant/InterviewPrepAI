import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { createSession, generateArtifact, type ArtifactType, type CreateSessionRequest } from '../api/sessions'
import { ArtifactResultView } from '../components/ArtifactResultView'

const MODES: { value: ArtifactType; label: string }[] = [
  { value: 'BEHAVIORAL', label: 'Behavioral' },
  { value: 'TECHNICAL', label: 'Technical' },
  { value: 'OA', label: 'OA' },
]

export function NewSessionPage() {
  const [jobDescription, setJobDescription] = useState('')
  const [resumeText, setResumeText] = useState('')
  const [company, setCompany] = useState('')
  const [role, setRole] = useState('')
  const [mode, setMode] = useState<ArtifactType>('BEHAVIORAL')

  const generateFlow = useMutation({
    mutationFn: async (payload: CreateSessionRequest) => {
      const session = await createSession(payload)
      return generateArtifact(session.id, mode)
    },
  })

  const companyRequired = mode === 'OA'
  const canSubmit =
    jobDescription.trim().length > 0 &&
    resumeText.trim().length > 0 &&
    (!companyRequired || company.trim().length > 0)

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="text-2xl font-semibold text-slate-900">InterviewPrepAI</h1>
      <p className="mt-1 text-slate-500">
        Paste a job description and your resume to get tailored interview prep.
      </p>

      <div className="mt-6 space-y-4">
        <div>
          <label className="block text-sm font-medium text-slate-700">Mode</label>
          <div className="mt-1 flex gap-2">
            {MODES.map((m) => (
              <button
                key={m.value}
                type="button"
                aria-pressed={mode === m.value}
                onClick={() => setMode(m.value)}
                className={`rounded-md border px-3 py-1.5 text-sm font-medium ${
                  mode === m.value
                    ? 'border-slate-900 bg-slate-900 text-white'
                    : 'border-slate-300 bg-white text-slate-700'
                }`}
              >
                {m.label}
              </button>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700">
              Company {companyRequired && <span className="text-red-600">*</span>}
            </label>
            <input
              type="text"
              className="mt-1 w-full rounded-md border border-slate-300 p-2 text-sm"
              value={company}
              onChange={(e) => setCompany(e.target.value)}
              placeholder="e.g. Google"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700">Role</label>
            <input
              type="text"
              className="mt-1 w-full rounded-md border border-slate-300 p-2 text-sm"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              placeholder="e.g. Software Engineer Intern"
            />
          </div>
        </div>
        {companyRequired && !company.trim() && (
          <p className="text-xs text-amber-700">Company is required for OA mode.</p>
        )}

        <div>
          <label className="block text-sm font-medium text-slate-700">Job description</label>
          <textarea
            className="mt-1 w-full rounded-md border border-slate-300 p-3 text-sm"
            rows={6}
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            placeholder="Paste the job description here..."
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700">Your resume</label>
          <textarea
            className="mt-1 w-full rounded-md border border-slate-300 p-3 text-sm"
            rows={6}
            value={resumeText}
            onChange={(e) => setResumeText(e.target.value)}
            placeholder="Paste your resume text here..."
          />
        </div>

        <button
          className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
          disabled={!canSubmit || generateFlow.isPending}
          onClick={() => generateFlow.mutate({ jobDescription, resumeText, company: company || undefined, role: role || undefined })}
        >
          {generateFlow.isPending ? 'Generating...' : 'Generate'}
        </button>

        {generateFlow.isError && (
          <p className="text-sm text-red-600">{(generateFlow.error as Error).message}</p>
        )}
      </div>

      {generateFlow.data && <ArtifactResultView artifact={generateFlow.data} />}
    </div>
  )
}
