-- 사용자 프로필 이미지 URL 필드 추가
-- V36__Add_profile_image_url_to_users.sql

ALTER TABLE users
ADD COLUMN profile_image_url VARCHAR(500);

COMMENT ON COLUMN users.profile_image_url IS '프로필 이미지 URL (MinIO objectKey 또는 전체 URL)';

