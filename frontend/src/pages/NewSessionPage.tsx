import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { createSession, generateBehavioral, type CreateSessionRequest } from '../api/sessions'
import { QuestionList } from '../components/QuestionList'

export function NewSessionPage() {
  const [jobDescription, setJobDescription] = useState('')
  const [resumeText, setResumeText] = useState('')

  const generateFlow = useMutation({
    mutationFn: async (payload: CreateSessionRequest) => {
      const session = await createSession(payload)
      return generateBehavioral(session.id)
    },
  })

  const canSubmit = jobDescription.trim().length > 0 && resumeText.trim().length > 0

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="text-2xl font-semibold text-slate-900">InterviewPrepAI</h1>
      <p className="mt-1 text-slate-500">
        Paste a job description and your resume to get tailored behavioral interview questions.
      </p>

      <div className="mt-6 space-y-4">
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
          onClick={() => generateFlow.mutate({ jobDescription, resumeText })}
        >
          {generateFlow.isPending ? 'Generating...' : 'Generate'}
        </button>

        {generateFlow.isError && (
          <p className="text-sm text-red-600">{(generateFlow.error as Error).message}</p>
        )}
      </div>

      {generateFlow.data && (
        <div className="mt-8">
          <h2 className="text-lg font-semibold text-slate-900">Behavioral questions</h2>
          <div className="mt-3">
            <QuestionList questions={generateFlow.data.content} />
          </div>
        </div>
      )}
    </div>
  )
}
