import type { ArtifactType, GenerateArtifactResponse } from '../api/sessions'
import { QuestionList } from './QuestionList'
import { OaResultView } from './OaResultView'

const MODE_HEADINGS: Record<ArtifactType, string> = {
  BEHAVIORAL: 'Behavioral questions',
  TECHNICAL: 'Technical questions',
  OA: 'OA practice problems',
}

export function ArtifactResultView({ artifact }: { artifact: GenerateArtifactResponse }) {
  return (
    <div className="mt-8">
      <h2 className="text-lg font-semibold text-slate-900">{MODE_HEADINGS[artifact.type]}</h2>
      <div className="mt-3">
        {artifact.type === 'OA' ? (
          <OaResultView content={artifact.content} />
        ) : (
          <QuestionList questions={artifact.content} />
        )}
      </div>
    </div>
  )
}
