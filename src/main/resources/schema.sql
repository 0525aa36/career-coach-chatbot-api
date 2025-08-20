-- =====================================================
-- 이력서 기반 개인 맞춤형 커리어 코치 챗봇 API
-- 데이터베이스 스키마 (성능 최적화 버전)
-- =====================================================

-- resumes 테이블: 직무와 경력 연수에 대한 복합 인덱스 생성
CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    career_summary VARCHAR(255) NOT NULL,
    job_role VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL,
    project_experience TEXT,
    tech_skills JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 성능 최적화를 위한 인덱스
    INDEX idx_resume_job_role_experience (job_role, experience_years),
    INDEX idx_resume_created_at (created_at),
    INDEX idx_resume_experience_years (experience_years)
);

-- interview_questions 테이블: 생성 날짜 기준으로 연도별 파티셔닝 적용
CREATE TABLE IF NOT EXISTS interview_questions (
    id BIGINT AUTO_INCREMENT,
    resume_id BIGINT NOT NULL,
    questions JSON NOT NULL,
    difficulty_level ENUM('JUNIOR', 'MIDDLE', 'SENIOR'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at),
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    
    -- 성능 최적화를 위한 인덱스
    INDEX idx_interview_resume_id (resume_id),
    INDEX idx_interview_difficulty (difficulty_level),
    INDEX idx_interview_created_at (created_at)
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p_max VALUES LESS THAN MAXVALUE
);

-- learning_paths 테이블: 학습 경로 정보 저장
CREATE TABLE IF NOT EXISTS learning_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    job_role VARCHAR(50) NOT NULL,
    experience_level VARCHAR(20) NOT NULL,
    learning_steps JSON NOT NULL,
    overall_strategy TEXT,
    estimated_duration VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    
    -- 성능 최적화를 위한 인덱스
    INDEX idx_learning_resume_id (resume_id),
    INDEX idx_learning_job_role (job_role),
    INDEX idx_learning_experience_level (experience_level),
    INDEX idx_learning_created_at (created_at)
);

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
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE SET NULL,
    
    -- 성능 최적화를 위한 인덱스
    INDEX idx_ai_service_name (service_name),
    INDEX idx_ai_resume_id (resume_id),
    INDEX idx_ai_status (status),
    INDEX idx_ai_created_at (created_at),
    INDEX idx_ai_duration (duration_ms)
);

-- career_events 테이블: 이벤트 기반 비동기 처리 로그
CREATE TABLE IF NOT EXISTS career_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    resume_id BIGINT,
    event_data JSON,
    processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    
    -- 외래키 제약조건
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    
    -- 성능 최적화를 위한 인덱스
    INDEX idx_event_type (event_type),
    INDEX idx_event_resume_id (resume_id),
    INDEX idx_event_processed (processed),
    INDEX idx_event_created_at (created_at)
);

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

-- =====================================================
-- 저장 프로시저: 복잡한 비즈니스 로직을 위한 저장 프로시저
-- =====================================================

DELIMITER //

-- 이력서 난이도 자동 계산 저장 프로시저
CREATE PROCEDURE CalculateResumeDifficulty(IN resume_id_param BIGINT)
BEGIN
    DECLARE exp_years INT;
    DECLARE difficulty_level VARCHAR(20);
    
    -- 경력 연수 조회
    SELECT experience_years INTO exp_years 
    FROM resumes 
    WHERE id = resume_id_param;
    
    -- 난이도 계산
    IF exp_years >= 5 THEN
        SET difficulty_level = 'SENIOR';
    ELSEIF exp_years >= 2 THEN
        SET difficulty_level = 'MIDDLE';
    ELSE
        SET difficulty_level = 'JUNIOR';
    END IF;
    
    -- 결과 반환
    SELECT difficulty_level as calculated_difficulty;
END //

-- 오래된 데이터 정리 저장 프로시저
CREATE PROCEDURE CleanupOldData(IN days_to_keep INT)
BEGIN
    DECLARE cutoff_date TIMESTAMP;
    SET cutoff_date = DATE_SUB(NOW(), INTERVAL days_to_keep DAY);
    
    -- 오래된 AI 서비스 호출 로그 삭제
    DELETE FROM ai_service_calls WHERE created_at < cutoff_date;
    
    -- 오래된 이벤트 로그 삭제
    DELETE FROM career_events WHERE created_at < cutoff_date AND processed = TRUE;
    
    SELECT ROW_COUNT() as deleted_records;
END //

DELIMITER ;

-- =====================================================
-- 트리거: 데이터 무결성 보장을 위한 트리거
-- =====================================================

DELIMITER //

-- 이력서 생성 시 자동 이벤트 생성 트리거
CREATE TRIGGER after_resume_insert
AFTER INSERT ON resumes
FOR EACH ROW
BEGIN
    INSERT INTO career_events (event_type, resume_id, event_data)
    VALUES ('RESUME_CREATED', NEW.id, JSON_OBJECT('job_role', NEW.job_role, 'experience_years', NEW.experience_years));
END //

-- 이력서 업데이트 시 자동 이벤트 생성 트리거
CREATE TRIGGER after_resume_update
AFTER UPDATE ON resumes
FOR EACH ROW
BEGIN
    INSERT INTO career_events (event_type, resume_id, event_data)
    VALUES ('RESUME_UPDATED', NEW.id, JSON_OBJECT('job_role', NEW.job_role, 'experience_years', NEW.experience_years));
END //

DELIMITER ; 