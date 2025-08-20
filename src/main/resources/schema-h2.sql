-- =====================================================
-- 이력서 기반 개인 맞춤형 커리어 코치 챗봇 API
-- H2 데이터베이스 스키마 (개발/테스트용)
-- =====================================================

-- resumes 테이블: 직무와 경력 연수에 대한 복합 인덱스 생성
CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    career_summary VARCHAR(255) NOT NULL,
    job_role VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL,
    project_experience TEXT,
    tech_skills VARCHAR(4000), -- H2에서는 JSON 타입 대신 VARCHAR 사용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 성능 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_resume_job_role_experience ON resumes(job_role, experience_years);
CREATE INDEX IF NOT EXISTS idx_resume_created_at ON resumes(created_at);
CREATE INDEX IF NOT EXISTS idx_resume_experience_years ON resumes(experience_years);

-- interview_questions 테이블
CREATE TABLE IF NOT EXISTS interview_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    questions VARCHAR(4000) NOT NULL, -- H2에서는 JSON 타입 대신 VARCHAR 사용
    difficulty_level VARCHAR(20) NOT NULL CHECK (difficulty_level IN ('JUNIOR', 'MIDDLE', 'SENIOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 성능 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_interview_resume_id ON interview_questions(resume_id);
CREATE INDEX IF NOT EXISTS idx_interview_difficulty ON interview_questions(difficulty_level);
CREATE INDEX IF NOT EXISTS idx_interview_created_at ON interview_questions(created_at);

-- learning_paths 테이블: 학습 경로 정보 저장
CREATE TABLE IF NOT EXISTS learning_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    job_role VARCHAR(50) NOT NULL,
    experience_level VARCHAR(20) NOT NULL,
    learning_steps VARCHAR(4000) NOT NULL, -- H2에서는 JSON 타입 대신 VARCHAR 사용
    overall_strategy TEXT,
    estimated_duration VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 성능 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_learning_resume_id ON learning_paths(resume_id);
CREATE INDEX IF NOT EXISTS idx_learning_job_role ON learning_paths(job_role);
CREATE INDEX IF NOT EXISTS idx_learning_experience_level ON learning_paths(experience_level);
CREATE INDEX IF NOT EXISTS idx_learning_created_at ON learning_paths(created_at);

-- ai_service_calls 테이블: AI 서비스 호출 모니터링
CREATE TABLE IF NOT EXISTS ai_service_calls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    resume_id BIGINT,
    duration_ms BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE SET NULL
);

-- 성능 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_ai_service_name ON ai_service_calls(service_name);
CREATE INDEX IF NOT EXISTS idx_ai_resume_id ON ai_service_calls(resume_id);
CREATE INDEX IF NOT EXISTS idx_ai_status ON ai_service_calls(status);
CREATE INDEX IF NOT EXISTS idx_ai_created_at ON ai_service_calls(created_at);
CREATE INDEX IF NOT EXISTS idx_ai_duration ON ai_service_calls(duration_ms);

-- career_events 테이블: 이벤트 기반 비동기 처리 로그
CREATE TABLE IF NOT EXISTS career_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    resume_id BIGINT,
    event_data VARCHAR(4000), -- H2에서는 JSON 타입 대신 VARCHAR 사용
    processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 성능 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_event_type ON career_events(event_type);
CREATE INDEX IF NOT EXISTS idx_event_resume_id ON career_events(resume_id);
CREATE INDEX IF NOT EXISTS idx_event_processed ON career_events(processed);
CREATE INDEX IF NOT EXISTS idx_event_created_at ON career_events(created_at);

-- =====================================================
-- 뷰 생성: 자주 사용되는 복잡한 쿼리를 위한 뷰
-- =====================================================

-- 이력서 통계 뷰
CREATE OR REPLACE VIEW resume_statistics AS
SELECT 
    job_role,
    COUNT(*) as total_count,
    AVG(experience_years) as avg_experience,
    MIN(experience_years) as min_experience,
    MAX(experience_years) as max_experience,
    COUNT(CASE WHEN experience_years >= 5 THEN 1 END) as senior_count,
    COUNT(CASE WHEN experience_years >= 2 AND experience_years < 5 THEN 1 END) as middle_count,
    COUNT(CASE WHEN experience_years < 2 THEN 1 END) as junior_count
FROM resumes 
GROUP BY job_role;

-- AI 서비스 성능 통계 뷰
CREATE OR REPLACE VIEW ai_performance_stats AS
SELECT 
    service_name,
    COUNT(*) as total_calls,
    AVG(duration_ms) as avg_duration,
    MIN(duration_ms) as min_duration,
    MAX(duration_ms) as max_duration,
    COUNT(CASE WHEN status = 'SUCCESS' THEN 1 END) as success_count,
    COUNT(CASE WHEN status = 'ERROR' THEN 1 END) as error_count,
    (COUNT(CASE WHEN status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(*)) as success_rate
FROM ai_service_calls 
GROUP BY service_name; 