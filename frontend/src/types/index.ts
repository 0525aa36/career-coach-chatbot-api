// 이력서 관련 타입 정의
export interface CreateResumeRequest {
  careerSummary: string;
  jobRole: JobRole;
  experienceYears: number;
  projectExperience?: string;
  techSkills?: string[];
}

export interface ResumeDto {
  id: number;
  careerSummary: string;
  jobRole: JobRole;
  experienceYears: number;
  projectExperience?: string;
  techSkills?: string[];
  createdAt: string;
  interviewDifficulty: InterviewDifficulty;
  experienceLevel: string;
}

// 직무 역할 열거형
export enum JobRole {
  BACKEND_DEVELOPER = 'BACKEND_DEVELOPER',
  FRONTEND_DEVELOPER = 'FRONTEND_DEVELOPER',
  FULLSTACK_DEVELOPER = 'FULLSTACK_DEVELOPER',
  DEVOPS_ENGINEER = 'DEVOPS_ENGINEER',
  DATA_SCIENTIST = 'DATA_SCIENTIST',
  DATA_ENGINEER = 'DATA_ENGINEER',
  ML_ENGINEER = 'ML_ENGINEER',
  AI_ENGINEER = 'AI_ENGINEER',
  PRODUCT_MANAGER = 'PRODUCT_MANAGER',
  QA_ENGINEER = 'QA_ENGINEER',
  SECURITY_ENGINEER = 'SECURITY_ENGINEER',
  SYSTEM_ARCHITECT = 'SYSTEM_ARCHITECT'
}

// 인터뷰 난이도 열거형
export enum InterviewDifficulty {
  JUNIOR = 'JUNIOR',
  MIDDLE = 'MIDDLE',
  SENIOR = 'SENIOR'
}

// 인터뷰 질문 응답 타입
export interface InterviewQuestionsResponse {
  resumeId: number;
  difficulty: InterviewDifficulty;
  questions: string[];
  analysis: string;
  generatedAt: string;
  promptUsed: string;
  questionCount: number;
  difficultyDescription: string;
}

// 학습 경로 응답 타입
export interface LearningPathResponse {
  resumeId: number;
  jobRole: string;
  experienceLevel: string;
  learningSteps: LearningStep[];
  overallStrategy: string;
  estimatedDuration: string;
  generatedAt: string;
  totalSteps: number;
}

export interface LearningStep {
  title: string;
  description: string;
  difficulty: string;
  estimatedTime: string;
  resources: string[];
  learningObjective: string;
}

// API 응답 타입
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

// 오류 응답 타입
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  field?: string;
  reason?: string;
} 