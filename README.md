# 🎯 이력서 기반 맞춤형 커리어 코치 챗봇 API

이력서 정보를 바탕으로 개인화된 인터뷰 질문과 학습 경로를 생성하는 AI 기반 커리어 코치 애플리케이션입니다.

## 🚀 주요 기능

### ✨ AI 기반 맞춤형 서비스
- **인터뷰 질문 생성**: 이력서 정보를 바탕으로 한 개인화된 면접 질문
- **학습 경로 추천**: 경력과 기술 스택에 맞는 맞춤형 학습 로드맵
- **실시간 AI 분석**: Google Gemini AI를 활용한 지능형 분석

### 🛠 기술 스택
- **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA
- **Frontend**: React 18, TypeScript, Material-UI
- **AI**: Google Gemini AI API
- **Database**: H2 (개발), MySQL (운영)
- **Architecture**: DDD (Domain-Driven Design), Layered Architecture

## 📋 프로젝트 구조

```
SeokJaemin/
├── src/main/java/com/careercoach/api/
│   ├── config/          # 설정 클래스들
│   ├── controller/      # REST API 컨트롤러
│   ├── domain/          # 도메인 모델 (DDD)
│   ├── dto/            # 데이터 전송 객체
│   ├── service/        # 비즈니스 로직
│   │   └── ai/         # AI 서비스 구현체
│   └── util/           # 유틸리티 클래스
├── frontend/           # React 프론트엔드
│   ├── src/
│   │   ├── components/ # 재사용 가능한 컴포넌트
│   │   ├── pages/      # 페이지 컴포넌트
│   │   ├── services/   # API 서비스
│   │   └── types/      # TypeScript 타입 정의
└── docs/              # 프로젝트 문서
```

## 🚀 시작하기

### Prerequisites
- Java 17+
- Node.js 18+
- npm 또는 yarn

### Backend 실행

1. **프로젝트 클론**
```bash
git clone <repository-url>
cd SeokJaemin
```

2. **환경 변수 설정**
```bash
# Windows
set GEMINI_API_KEY=your-gemini-api-key

# Linux/Mac
export GEMINI_API_KEY=your-gemini-api-key
```

3. **백엔드 서버 실행**
```bash
./gradlew bootRun
```

백엔드 서버는 `http://localhost:8081`에서 실행됩니다.

### Frontend 실행

1. **프론트엔드 디렉토리로 이동**
```bash
cd frontend
```

2. **의존성 설치**
```bash
npm install
```

3. **개발 서버 실행**
```bash
npm start
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

## 📖 API 문서

### 이력서 관리
- `GET /api/resumes` - 모든 이력서 조회
- `POST /api/resumes` - 새 이력서 생성
- `GET /api/resumes/{id}` - 특정 이력서 조회
- `PUT /api/resumes/{id}` - 이력서 수정
- `DELETE /api/resumes/{id}` - 이력서 삭제

### AI 서비스
- `POST /api/resumes/{id}/interview-questions` - 맞춤형 인터뷰 질문 생성
- `POST /api/resumes/{id}/learning-path` - 맞춤형 학습 경로 생성

## 🔧 설정

### application.properties
```properties
# Gemini AI Configuration
gemini.api.key=${GEMINI_API_KEY:your-gemini-api-key}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent
gemini.model.name=gemini-1.5-flash

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## 🎯 주요 기능 설명

### 1. 이력서 기반 맞춤형 인터뷰 질문
- 지원자의 경력, 기술 스택, 프로젝트 경험을 분석
- 직무별, 경력 수준별 맞춤형 질문 생성
- 기술적 깊이와 실무 경험을 모두 검증하는 질문 구성

### 2. 개인화된 학습 경로
- 현재 기술 수준과 목표 직무를 고려한 학습 로드맵
- 단계별 학습 목표와 예상 소요 시간 제공
- 실무 중심의 학습 리소스 추천

### 3. AI 기반 지능형 분석
- Google Gemini AI를 활용한 자연어 처리
- Chain of Thought와 Few-shot Learning 적용
- 지속적인 학습을 통한 응답 품질 향상

## 🏗 아키텍처

### Backend Architecture
- **Controller Layer**: REST API 엔드포인트 제공
- **Service Layer**: 비즈니스 로직 처리
- **Repository Layer**: 데이터 접근 계층
- **Domain Layer**: 도메인 모델 및 비즈니스 규칙

### AI Service Architecture
- **AIService Interface**: AI 서비스 추상화
- **GeminiAIService**: Google Gemini AI 구현체
- **AIInterviewService**: 인터뷰 질문 생성 서비스
- **AILearningPathService**: 학습 경로 생성 서비스

## 🔒 보안

- API 키는 환경 변수로 관리
- CORS 설정으로 프론트엔드 접근 제한
- 입력 데이터 검증 및 예외 처리

## 📝 개발 가이드

### 코드 컨벤션
- Java: Google Java Style Guide 준수
- TypeScript: ESLint + Prettier 설정
- 커밋 메시지: Conventional Commits 형식

### 테스트
```bash
# Backend 테스트
./gradlew test

# Frontend 테스트
cd frontend
npm test
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**개발자**: SeokJaemin  
**프로젝트**: 이력서 기반 맞춤형 커리어 코치 챗봇 API  
**버전**: 1.0.0 