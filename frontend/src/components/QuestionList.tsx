import type { BehavioralQuestion } from '../api/sessions'
import { LabeledNote } from './LabeledNote'

export function QuestionList({ questions }: { questions: BehavioralQuestion[] }) {
  return (
    <ol className="space-y-4">
      {questions.map((q, i) => (
        <li key={i} className="rounded-lg border border-slate-200 p-4">
          <p className="font-medium text-slate-900">{q.question}</p>
          <p className="mt-1 text-sm text-slate-500">{q.rationale}</p>
          <LabeledNote label="Sample answer">{q.answer}</LabeledNote>
        </li>
      ))}
    </ol>
  )
}
