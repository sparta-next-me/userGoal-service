-- userservice 스키마 없으면 생성
-- 1) 스키마 생성
CREATE SCHEMA IF NOT EXISTS usergoalservice;

-- -- 2) 현재 세션의 기본 스키마를 usergoalservice로 지정
-- SET search_path TO usergoalservice;
--
-- -- 3) pgvector 확장 생성 (스키마를 명시해서 생성)
-- CREATE EXTENSION IF NOT EXISTS vector SCHEMA usergoalservice;
-- -- 4) 테이블 생성
-- CREATE TABLE IF NOT EXISTS vector_store (
--     id TEXT PRIMARY KEY,
--     content    TEXT,
--     metadata   JSONB,
--     embedding  usergoalservice.vector(1536),
--     updated_at TIMESTAMP DEFAULT NOW()
-- );