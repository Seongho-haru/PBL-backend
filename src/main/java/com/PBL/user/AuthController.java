package com.PBL.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 * 회원가입, 로그인, 중복 검사 등
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "사용자 인증 관련 API")
public class AuthController {

    @Autowired
    private UserService userService;

    // === 회원가입 ===

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성합니다.")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserDTOs.SignUpRequest request) {
        try {
            UserDTOs.UserResponse user = userService.signUp(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원가입이 완료되었습니다.",
                    "user", user
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "회원가입 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // === 로그인 ===

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 수행합니다.")
    public ResponseEntity<UserDTOs.LoginResponse> login(@RequestBody UserDTOs.LoginRequest request) {
        try {
            UserDTOs.LoginResponse response = userService.login(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    UserDTOs.LoginResponse.failure(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    UserDTOs.LoginResponse.failure("로그인 중 오류가 발생했습니다: " + e.getMessage())
            );
        }
    }

    // === 중복 검사 ===

    @GetMapping("/check/loginId")
    @Operation(summary = "아이디 중복 검사", description = "로그인 아이디의 중복 여부를 확인합니다.")
    public ResponseEntity<Map<String, Object>> checkLoginId(
            @Parameter(description = "검사할 로그인 아이디") @RequestParam String loginId) {
        try {
            boolean available = userService.isLoginIdAvailable(loginId);
            return ResponseEntity.ok(Map.of(
                    "available", available,
                    "message", available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "available", false,
                    "message", "중복 검사 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/check/username")
    @Operation(summary = "사용자명 중복 검사", description = "사용자명의 중복 여부를 확인합니다.")
    public ResponseEntity<Map<String, Object>> checkUsername(
            @Parameter(description = "검사할 사용자명") @RequestParam String username) {
        try {
            boolean available = userService.isUsernameAvailable(username);
            return ResponseEntity.ok(Map.of(
                    "available", available,
                    "message", available ? "사용 가능한 사용자명입니다." : "이미 사용 중인 사용자명입니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "available", false,
                    "message", "중복 검사 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // === 사용자 정보 조회 ===

    @GetMapping("/user/{id}")
    @Operation(summary = "사용자 정보 조회", description = "ID로 사용자 정보를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUserById(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        try {
            UserDTOs.UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "user", user
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/user/loginId/{loginId}")
    @Operation(summary = "로그인 ID로 사용자 조회", description = "로그인 ID로 사용자 정보를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUserByLoginId(
            @Parameter(description = "로그인 ID") @PathVariable String loginId) {
        try {
            UserDTOs.UserResponse user = userService.getUserByLoginId(loginId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "user", user
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // === 제재 관리 ===

    @GetMapping("/users/muted")
    @Operation(summary = "제재된 사용자 목록 조회", description = "현재 제재 중인 모든 사용자를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getMutedUsers(
            @RequestHeader(value = "X-User-Id", required = false) Long adminUserId) {
        try {
            // 관리자 권한 체크 (임시: user ID가 1인 경우)
            if (adminUserId == null || !adminUserId.equals(1L)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "관리자 권한이 필요합니다."
                ));
            }

            var mutedUsers = userService.getMutedUsers();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "users", mutedUsers,
                    "count", mutedUsers.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "제재된 사용자 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/users/{userId}/unmute")
    @Operation(summary = "사용자 제재 해제", description = "제재된 사용자의 제재를 해제합니다.")
    public ResponseEntity<Map<String, Object>> unmuteUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long adminUserId) {
        try {
            // 관리자 권한 체크 (임시: user ID가 1인 경우)
            if (adminUserId == null || !adminUserId.equals(1L)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "관리자 권한이 필요합니다."
                ));
            }

            userService.unmuteUser(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "사용자 제재가 해제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "제재 해제 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}
