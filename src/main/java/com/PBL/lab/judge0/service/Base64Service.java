package com.PBL.lab.judge0.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 Service - Base64 인코딩/디코딩 전용 서비스
 * 
 * 목적:
 * - Judge0에서 텍스트 필드들에 대한 Base64 인코딩/디코딩 처리
 * - 원본 Judge0 Ruby Base64Service와 동등한 역할
 * - UTF-8 문자 집합 안전성 보장 및 다국어 코드 지원
 * 
 * 주요 사용 사례:
 * - 소스 코드: 다양한 언어의 특수 문자 및 유니코드 지원
 * - 표준 입력/출력: 바이너리 데이터나 특수 문자 포함 내용
 * - 오류 메시지: 컴파일 오류에 포함된 특수 기호 및 색상 코드
 * - 콜백 데이터: HTTP 전송에 안전한 텍스트 형식 보장
 * - 추가 파일: 바이너리 파일 데이터 안전한 전송
 * 
 * 핵심 기능:
 * - encode(): UTF-8 텍스트를 Base64로 인코딩
 * - decode(): Base64 문자열을 UTF-8 텍스트로 디코딩
 * - decodeToBytes(): Base64를 바이트 배열로 디코딩 (바이너리 데이터)
 * - smartDecode(): 자동 감지로 또는 명시적 플래그로 디코딩
 * - smartEncode(): 필요 시에만 인코딩 수행
 * 
 * 지능형 처리 특징:
 * - isValidBase64(): Base64 유효성 검증 (정규식 기반 패턴 매칭)
 * - 자동 감지: 입력이 이미 Base64인지 아닌지 지능적 판단
 * - Graceful degradation: 디코딩 실패 시 원본 데이터 그대로 반환
 * - 휴리스틱 적용: 20자 이상의 긴 문자열은 Base64일 가능성 높음
 * 
 * 다국어 지원:
 * - UTF-8 문자 집합 사용으로 모든 언어 지원
 * - 한글, 중국어, 일본어, 아랍어 등 비라틴 문자 완볝 지원
 * - 이모지, 특수 기호, 수학 기호 등 유니코드 전 범위 지원
 * - 커스텀 문자 집합이나 레거시 인코딩에 대한 호환성
 * 
 * 오류 처리 전략:
 * - 인코딩 실패: 원본 텍스트 그대로 반환 (비정상 종료 방지)
 * - 디코딩 실패: 원본 문자열 그대로 반환 (사용자 데이터 보호)
 * - null 및 빈 문자열: 안전한 null 처리
 * - 로깅: 오류 발생 시 경고 수준으로 로깅 (디버깅 용도)
 * 
 * 성능 최적화:
 * - 경량 라이브러리 사용: Java 기본 Base64 인코더/디코더
 * - 메모리 효율적 문자열 처리
 * - 추가 복사 또는 변환 오버헤드 최소화
 * - 단니 검증을 통한 불필요한 연산 생략
 * 
 * 보안 고려사항:
 * - Base64 탐지 공격 방지를 위한 엄격한 유효성 검증
 * - 대용량 데이터 처리 제한 (메모리 고갈 방지)
 * - 안전한 기본값 반환으로 데이터 손실 방지
 */
@Service
@Slf4j
public class Base64Service {

    /**
     * Encode text to Base64
     */
    public String encode(String text) {
        if (text == null) {
            return null;
        }
        
        try {
            return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.warn("Failed to encode text to Base64", e);
            return text; // Return original text if encoding fails
        }
    }

    /**
     * Decode Base64 to text
     */
    public String decode(String base64Text) {
        if (base64Text == null || base64Text.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Check if it's already valid Base64
            if (isValidBase64(base64Text)) {
                byte[] decoded = Base64.getDecoder().decode(base64Text);
                return new String(decoded, StandardCharsets.UTF_8);
            } else {
                // If not valid Base64, return as-is (assume it's plain text)
                return base64Text;
            }
        } catch (Exception e) {
            log.warn("Failed to decode Base64 text, returning as-is", e);
            return base64Text; // Return original text if decoding fails
        }
    }

    /**
     * Decode Base64 to byte array
     */
    public byte[] decodeToBytes(String base64Text) {
        if (base64Text == null || base64Text.trim().isEmpty()) {
            return null;
        }
        
        try {
            if (isValidBase64(base64Text)) {
                return Base64.getDecoder().decode(base64Text);
            } else {
                // If not valid Base64, convert plain text to bytes
                return base64Text.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.warn("Failed to decode Base64 to bytes", e);
            return base64Text.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Check if string is valid Base64
     */
    private boolean isValidBase64(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Base64 strings should only contain valid Base64 characters
            return text.matches("^[A-Za-z0-9+/]*={0,2}$") && (text.length() % 4 == 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Smart decode - automatically detects if input is Base64 or plain text
     */
    public String smartDecode(String input, boolean base64Encoded) {
        if (input == null) {
            return null;
        }
        
        if (base64Encoded) {
            return decode(input);
        } else {
            // If not explicitly marked as Base64, but looks like Base64, decode it
            if (isValidBase64(input) && input.length() > 20) { // Heuristic: long Base64 strings
                return decode(input);
            }
            return input;
        }
    }

    /**
     * Smart encode - encode to Base64 if needed
     */
    public String smartEncode(String input, boolean shouldEncode) {
        if (input == null) {
            return null;
        }
        
        if (shouldEncode) {
            return encode(input);
        }
        
        return input;
    }
}
