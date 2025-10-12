-- 이미지 메타데이터 테이블 생성
CREATE TABLE image_metadata (
    id BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE,
    bucket_name VARCHAR(100) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    width INTEGER,
    height INTEGER,
    uploaded_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_image_metadata_uploaded_by ON image_metadata(uploaded_by);
CREATE INDEX idx_image_metadata_content_type ON image_metadata(content_type);
CREATE INDEX idx_image_metadata_created_at ON image_metadata(created_at);
CREATE INDEX idx_image_metadata_object_key ON image_metadata(object_key);

-- 업데이트 시간 자동 갱신을 위한 트리거 함수
CREATE OR REPLACE FUNCTION update_image_metadata_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER trigger_update_image_metadata_updated_at
    BEFORE UPDATE ON image_metadata
    FOR EACH ROW
    EXECUTE FUNCTION update_image_metadata_updated_at();

-- 코멘트 추가
COMMENT ON TABLE image_metadata IS 'S3에 저장된 이미지의 메타데이터를 관리하는 테이블';
COMMENT ON COLUMN image_metadata.original_filename IS '원본 파일명';
COMMENT ON COLUMN image_metadata.stored_filename IS 'S3에 저장된 파일명 (고유)';
COMMENT ON COLUMN image_metadata.bucket_name IS 'S3 버킷명';
COMMENT ON COLUMN image_metadata.object_key IS 'S3 오브젝트 키';
COMMENT ON COLUMN image_metadata.content_type IS '파일 MIME 타입';
COMMENT ON COLUMN image_metadata.file_size IS '파일 크기 (바이트)';
COMMENT ON COLUMN image_metadata.width IS '이미지 너비 (픽셀)';
COMMENT ON COLUMN image_metadata.height IS '이미지 높이 (픽셀)';
COMMENT ON COLUMN image_metadata.uploaded_by IS '업로드한 사용자 ID';
