import { useState, type ChangeEvent } from 'react'
import { useMutation } from '@tanstack/react-query'
import { createSession, generateArtifact, uploadSession, type ArtifactType } from '../api/sessions'
import { ArtifactResultView } from '../components/ArtifactResultView'

const MODES: { value: ArtifactType; label: string }[] = [
  { value: 'BEHAVIORAL', label: 'Behavioral' },
  { value: 'TECHNICAL', label: 'Technical' },
  { value: 'OA', label: 'OA' },
]

type ResumeMode = 'paste' | 'upload'

const RESUME_MODES: { value: ResumeMode; label: string }[] = [
  { value: 'paste', label: 'Paste text' },
  { value: 'upload', label: 'Upload file' },
]

// Client-side pre-check only, mirroring the backend's default cap (RESUME_MAX_FILE_SIZE)
// - the real limit is enforced server-side regardless of this check.
const MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024

const inputClasses =
  'mt-1 w-full rounded-md border border-slate-300 p-2 text-sm transition-colors focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'

export function NewSessionPage() {
  const [jobDescription, setJobDescription] = useState('')
  const [resumeText, setResumeText] = useState('')
  const [company, setCompany] = useState('')
  const [role, setRole] = useState('')
  const [mode, setMode] = useState<ArtifactType>('BEHAVIORAL')
  const [resumeMode, setResumeMode] = useState<ResumeMode>('paste')
  const [resumeFile, setResumeFile] = useState<File | null>(null)
  const [fileError, setFileError] = useState<string | null>(null)

  const generateFlow = useMutation({
    mutationFn: async () => {
      const session =
        resumeMode === 'upload' && resumeFile
          ? await uploadSession(buildFormData(resumeFile, jobDescription, company, role))
          : await createSession({ jobDescription, resumeText, company: company || undefined, role: role || undefined })
      return generateArtifact(session.id, mode)
    },
  })

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null
    if (!file) {
      setResumeFile(null)
      setFileError(null)
      return
    }

    const lower = file.name.toLowerCase()
    if (!lower.endsWith('.pdf') && !lower.endsWith('.docx')) {
      setResumeFile(null)
      setFileError('Only .pdf and .docx files are supported.')
      return
    }
    if (file.size > MAX_FILE_SIZE_BYTES) {
      setResumeFile(null)
      setFileError('File is too large (max 2MB).')
      return
    }

    setResumeFile(file)
    setFileError(null)
  }

  const companyRequired = mode === 'OA'
  const resumeProvided = resumeMode === 'paste' ? resumeText.trim().length > 0 : resumeFile !== null
  const canSubmit =
    jobDescription.trim().length > 0 &&
    resumeProvided &&
    !fileError &&
    (!companyRequired || company.trim().length > 0)

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="text-2xl font-semibold text-slate-900">Start a new session</h1>
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
                className={`rounded-md border px-3 py-1.5 text-sm font-medium transition-colors ${
                  mode === m.value
                    ? 'border-blue-600 bg-blue-600 text-white'
                    : 'border-slate-300 bg-white text-slate-700 hover:border-blue-300 hover:text-blue-600'
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
              className={inputClasses}
              value={company}
              onChange={(e) => setCompany(e.target.value)}
              placeholder="e.g. Google"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700">Role</label>
            <input
              type="text"
              className={inputClasses}
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
            className={inputClasses}
            rows={6}
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            placeholder="Paste the job description here..."
          />
        </div>

        <div>
          <div className="flex items-center justify-between">
            <label className="block text-sm font-medium text-slate-700">Your resume</label>
            <div className="flex gap-2">
              {RESUME_MODES.map((m) => (
                <button
                  key={m.value}
                  type="button"
                  aria-pressed={resumeMode === m.value}
                  onClick={() => setResumeMode(m.value)}
                  className={`rounded-md border px-2.5 py-1 text-xs font-medium transition-colors ${
                    resumeMode === m.value
                      ? 'border-blue-600 bg-blue-600 text-white'
                      : 'border-slate-300 bg-white text-slate-700 hover:border-blue-300 hover:text-blue-600'
                  }`}
                >
                  {m.label}
                </button>
              ))}
            </div>
          </div>

          {resumeMode === 'paste' ? (
            <textarea
              className={inputClasses}
              rows={6}
              value={resumeText}
              onChange={(e) => setResumeText(e.target.value)}
              placeholder="Paste your resume text here..."
            />
          ) : (
            <div className={inputClasses}>
              <input type="file" accept=".pdf,.docx" onChange={handleFileChange} className="text-sm" />
              {resumeFile && <p className="mt-1 text-xs text-slate-500">{resumeFile.name}</p>}
            </div>
          )}
          {fileError && <p className="mt-1 text-xs text-amber-700">{fileError}</p>}
        </div>

        <button
          className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blue-700 disabled:opacity-40 disabled:hover:bg-blue-600"
          disabled={!canSubmit || generateFlow.isPending}
          onClick={() => generateFlow.mutate()}
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

function buildFormData(file: File, jobDescription: string, company: string, role: string): FormData {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('jobDescription', jobDescription)
  if (company) formData.append('company', company)
  if (role) formData.append('role', role)
  return formData
}
