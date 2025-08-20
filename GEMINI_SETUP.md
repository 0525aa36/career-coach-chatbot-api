# Gemini API 설정 가이드

## 1. Gemini API 키 발급

1. [Google AI Studio](https://makersuite.google.com/app/apikey)에 접속합니다.
2. Google 계정으로 로그인합니다.
3. "Create API Key" 버튼을 클릭하여 새로운 API 키를 생성합니다.
4. 생성된 API 키를 안전한 곳에 복사해둡니다.

## 2. 환경 변수 설정

### Windows (PowerShell)
```powershell
$env:GEMINI_API_KEY="your-actual-api-key-here"
```

### Windows (Command Prompt)
```cmd
set GEMINI_API_KEY=your-actual-api-key-here
```

### Linux/Mac
```bash
export GEMINI_API_KEY="your-actual-api-key-here"
```

## 3. application.properties 설정

`src/main/resources/application.properties` 파일에서 다음 설정을 확인하세요:

```properties
# Gemini AI Configuration
gemini.api.key=${GEMINI_API_KEY:your-gemini-api-key-here}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent
gemini.model.name=gemini-1.5-flash
```

## 4. 애플리케이션 실행

환경 변수를 설정한 후 애플리케이션을 실행하세요:

```bash
./gradlew bootRun
```

## 5. 기능 테스트

1. 이력서를 생성하거나 기존 이력서를 선택합니다.
2. "인터뷰 질문 생성" 버튼을 클릭하여 AI가 생성한 맞춤형 질문을 확인합니다.
3. "학습 경로 생성" 버튼을 클릭하여 개인화된 학습 경로를 확인합니다.

## 주의사항

- API 키는 절대 공개 저장소에 커밋하지 마세요.
- 환경 변수로 설정하는 것을 권장합니다.
- Gemini API는 무료 할당량이 있으므로 사용량을 모니터링하세요.

## 문제 해결

### API 키 오류
- API 키가 올바르게 설정되었는지 확인하세요.
- 환경 변수가 제대로 로드되었는지 확인하세요.

### 네트워크 오류
- 인터넷 연결을 확인하세요.
- 방화벽 설정을 확인하세요.

### 응답 파싱 오류
- AI 응답이 JSON 형식이 아닌 경우 목업 데이터가 반환됩니다.
- 로그를 확인하여 AI 응답을 검토하세요. 