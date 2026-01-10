export interface SensitivityPoint {
  criterionWeight: number;
  alternativeScores: { [key: number]: number };
  ranking: number[];
}

export interface CriticalPoint {
  weightThreshold: number;
  beforeWinnerId: number;
  beforeWinnerName: string;
  afterWinnerId: number;
  afterWinnerName: string;
  description: string;
}

export enum RiskLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export interface StabilityMetrics {
  stabilityScore: number;
  riskLevel: RiskLevel;
  toleranceRange: number;
  rankingChangeCount: number;
}

export interface SensitivityResult {
  criterionId: number;
  criterionName: string;
  currentWeight: number;
  dataPoints: SensitivityPoint[];
  criticalPoints: CriticalPoint[];
  stability: StabilityMetrics;
  alternativeNames: string[];
}
