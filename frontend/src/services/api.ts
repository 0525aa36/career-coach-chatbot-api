import axios from 'axios';
import {
  CreateResumeRequest,
  ResumeDto,
  InterviewQuestionsResponse,
  LearningPathResponse
} from '../types';

// API 기본 설정
const API_BASE_URL = 'http://localhost:8081/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    console.log('API 요청:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => {
    console.log('API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API 오류:', error.response?.status, error.response?.data);
    return Promise.reject(error);
  }
);

// 이력서 관련 API
export const resumeApi = {
  // 이력서 생성
  createResume: async (request: CreateResumeRequest): Promise<ResumeDto> => {
    const response = await apiClient.post<ResumeDto>('/resumes', request);
    return response.data;
  },

  // 이력서 조회
  getResume: async (id: number): Promise<ResumeDto> => {
    const response = await apiClient.get<ResumeDto>(`/resumes/${id}`);
    return response.data;
  },

  // 이력서 수정
  updateResume: async (id: number, request: CreateResumeRequest): Promise<ResumeDto> => {
    const response = await apiClient.put<ResumeDto>(`/resumes/${id}`, request);
    return response.data;
  },

  // 이력서 삭제
  deleteResume: async (id: number): Promise<void> => {
    await apiClient.delete(`/resumes/${id}`);
  },

  // 모든 이력서 조회
  getAllResumes: async (): Promise<ResumeDto[]> => {
    const response = await apiClient.get<ResumeDto[]>('/resumes');
    return response.data;
  },

  // 직무별 이력서 조회
  getResumesByJobRole: async (jobRole: string): Promise<ResumeDto[]> => {
    const response = await apiClient.get<ResumeDto[]>(`/resumes/job-role/${jobRole}`);
    return response.data;
  },

  // 경력 연수별 이력서 조회
  getResumesByExperienceRange: async (minYears: number, maxYears: number): Promise<ResumeDto[]> => {
    const response = await apiClient.get<ResumeDto[]>('/resumes/experience-range', {
      params: { minYears, maxYears }
    });
    return response.data;
  },

  // 기술 스택별 이력서 검색
  getResumesByTechSkill: async (techSkill: string): Promise<ResumeDto[]> => {
    const response = await apiClient.get<ResumeDto[]>(`/resumes/tech-skill/${techSkill}`);
    return response.data;
  }
};

// 인터뷰 관련 API
export const interviewApi = {
  // 인터뷰 질문 생성
  generateInterviewQuestions: async (resumeId: number): Promise<InterviewQuestionsResponse> => {
    const response = await apiClient.post<InterviewQuestionsResponse>(`/resumes/${resumeId}/interview-questions`);
    return response.data;
  }
};

// 학습 경로 관련 API
export const learningPathApi = {
  // 학습 경로 생성
  generateLearningPath: async (resumeId: number): Promise<LearningPathResponse> => {
    const response = await apiClient.post<LearningPathResponse>(`/resumes/${resumeId}/learning-path`);
    return response.data;
  }
};

export default apiClient; 