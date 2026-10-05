import type { OaContent } from '../api/sessions'
import { LabeledNote } from './LabeledNote'

export function OaResultView({ content }: { content: OaContent }) {
  return (
    <div>
      <p className="rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-800">
        {content.disclaimer}
      </p>

      {content.researchSummary && (
        <p className="mt-3 text-sm text-slate-600">{content.researchSummary}</p>
      )}

      <ol className="mt-4 space-y-4">
        {content.problems.map((p, i) => (
          <li key={i} className="rounded-lg border border-slate-200 p-4">
            <p className="font-medium text-slate-900">
              {p.name} <span className="text-slate-400">({p.platform}, {p.difficulty})</span>
            </p>
            {p.topics.length > 0 && (
              <p className="mt-1 text-xs text-slate-500">{p.topics.join(', ')}</p>
            )}
            <p className="mt-2 text-sm text-slate-600">{p.relevance}</p>
            <LabeledNote label="Approach hint">{p.approachHint}</LabeledNote>
          </li>
        ))}
      </ol>
    </div>
  )
}
