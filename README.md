# 서비스 개요
> **사용자의 미래 설계를 위한 재무 목표를 설정하고, 개인의 소비 데이터 및 금융 상품 정보를 결합하여 AI(LLM)가 최적의 금융 상품을 추천하는 RAG 기반 분석 엔진입니다.**
---
# 목차
1. [서비스 개요](#서비스-개요)
2. [기술 스택](#기술-스택)
3. [담당 기능](#담당-기능)
    - [사용자 재무 목표 관리 (CRUD)](#1-사용자-재무-목표-관리-crud)
    - [AI 데이터 분석 및 추천 파이프라인 (RAG)](#2-ai-데이터-분석-및-추천-파이프라인-rag)
4. [ERD](#erd)
5. [기술적 의사결정 & 트러블슈팅](#기술적 의사결정-&-트러블슈팅)
    - [Spring Cloud OpenFeign](#1-spring-cloud-openfeign)
    - [Vector Database & RAG](#2-vector-database--rag)
6. [API Endpoints](#api-endpoints)

## 기술 스택
- **Language**: Java 21 / Spring Boot 3.x
- **Communication**: Spring Cloud OpenFeign (RAG 서버 통신)
- **Database**: PostgreSQL / JPA
- **AI Architecture**: RAG (Retrieval-Augmented Generation)

---

## 담당 기능

### 1. 사용자 재무 목표 관리 (CRUD)
* **맞춤형 데이터 수집**: 사용자의 자본금, 월 수입, 직업, 나이, 고정 지출 등 미래 설계를 위한 핵심 재무 지표 저장 로직 구현
* **데이터 정규화**: 사용자별 재무 상태를 분석 가능한 규격으로 관리하여 AI 모델의 입력 데이터로 최적화

### 2. AI 데이터 분석 및 추천 파이프라인 (RAG)
* **임베딩**: 사용자의 재무 목표 데이터, 과거 거래 내역, 시중 금융 상품 데이터를 벡터화 수행
* **AI 유사도 분석 연동**: OpenFeign 인터페이스를 활용하여 거래내역과 금융상품 데이터를 LLM 기반 RAG 서버에 전달하는 실시간 데이터 파이프라인 형성
* **개인화된 상품 추천**: 분석 결과를 바탕으로 사용자의 목표 달성 가능성을 진단하고, 가장 적합한 예/적금 상품을 매칭하여 제공

---
# ERD
---
# 기술적 의사결정 & 트러블슈팅
### [기술적 의사결정]

#### 1. Spring Cloud OpenFeign
* **선언적 코드 작성**: RAG 서버와의 복잡한 통신 과정을 인터페이스 선언만으로 처리하여 비즈니스 로직의 가독성을 높임
* **관심사 분리**: 외부 분석 엔진과의 통신 규격이 변경되더라도 내부 비즈니스 로직의 수정을 최소화할 수 있는 구조를 설계

#### 2. Vector Database & RAG 채택 이유
* **데이터 최신성 유지**: 학습된 LLM 모델만으로는 알 수 없는 '최신 금융 상품 정보'와 '개인 거래 내역'을 검색하여 참조함으로써 분석 결과의 정확도를 높였습니다.
* **유사도 기반 매칭**: 사용자의 정성적인 재무 목표(예: "내집 마련", "노후 준비")와 가장 유사한 혜택을 가진 금융 상품을 벡터 공간에서 계산하여 최적의 추천을 수행하기 위해 도입

### [트러블슈팅]

#### RAG 파이프라인 최적화 및 비용 절감
##### 1. 문제 상황 (Problem)

- 비효율적인 API 호출: 데이터 존재 여부와 상관없이 모든 요청에서 similaritySearch를 실행하여 외부 임베딩 API 토큰 소모 및 비용 과다 발생

- 분석 근거 부족: 낮은 topK 설정으로 인해 LLM이 참고할 컨텍스트(Context)가 부족하여 추천 정확도 저하

##### 2. 해결 방안 (Solution)

- Selective Similarity Search: 벡터 계산 전, JdbcClient를 이용해 메타데이터(userId 등)를 DB에서 먼저 조회하여 데이터가 존재하는 경우에만 벡터 검색을 수행하도록 로직 개선

- Context 최적화: topK를 3에서 5로 상향 조정하고, TokenTextSplitter를 적용하여 긴 문서를 효율적으로 분할함으로써 분석 정확도 향상시킴

##### 3. 개선 결과 (Result)

- 비용 절감: 불필요한 벡터 계산을 사전에 차단하여 API 과부하 방지 및 인프라 비용 감소

- 품질 향상: 충분한 분석 근거 확보를 통해 GPT 기반 금융 상품 추천의 신뢰도와 정확도 높임

---

## API Endpoints

### 사용자 재무 목표
| Category | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Goal** | POST | `/v1/usergoal` | 사용자 재무 목표 정보 저장 |
| **Goal** | GET | `/v1/usergoal` | 저장된 재무 목표 및 정보 조회 |
| **Goal** | PATCH | `/v1/usergoal` | 재무 목표 정보 수정 |
| **Goal** | DELETE | `/v1/goals/{userId}` | 재무 목표 정보 삭제 |

### 분석 결과
| Category | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Analysis** | GET | `/v1/usergoal/report/create` | 사용자의 맞는 분석 결과 생성 |
| **Analysis** | GET | `/v1/usergoal/report/all` | 사용자의 분석결과 전체조회 |
| **Analysis** | POST | `/v1/usergoal/report` | 사용자의 분석결과 단건조회 |
| **Analysis** | DELETE | `/v1/usergoal/report` | 사용자의 분석결과 단건 삭제 |
