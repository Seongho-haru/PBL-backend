#!/usr/bin/env python3
"""
백준 문제를 HTTP API로 전송하는 스크립트 (수학 기호 포함 문제 전용)
수학 기호($, \\)가 있는 문제만 HTTP API로 전송
"""

import json
import sys
import time
import requests
from datetime import datetime
from typing import Dict, List, Any


def has_math_symbols(problem: Dict[str, Any]) -> bool:
    """
    문제에 LaTeX 수학 기호가 있는지 확인
    $...$ 또는 \\ 패턴이 있으면 True 반환
    """
    text_fields = [
        problem.get('title', ''),
        problem.get('description', ''),
        problem.get('inputDescription', ''),
        problem.get('outputDescription', '')
    ]

    # 테스트 케이스 텍스트도 확인
    for tc in problem.get('testCases', []):
        text_fields.append(tc.get('input', ''))
        text_fields.append(tc.get('expectedOutput', ''))

    combined = ' '.join(str(field) for field in text_fields)
    return '$' in combined or '\\\\' in combined


def build_lecture_request(problem: Dict[str, Any]) -> Dict[str, Any]:
    """
    백준 문제를 Lecture API 요청 형식으로 변환
    """
    # content: 문제 설명 전체를 마크다운 형식으로
    content_parts = []
    if problem.get('description'):
        content_parts.append(f"# {problem.get('title', 'Untitled')}\n\n{problem.get('description')}")
    if problem.get('inputDescription'):
        content_parts.append(f"\n\n## 입력\n{problem.get('inputDescription')}")
    if problem.get('outputDescription'):
        content_parts.append(f"\n\n## 출력\n{problem.get('outputDescription')}")

    content = '\n'.join(content_parts) if content_parts else ''

    # testCases 변환
    test_cases = []
    for tc in problem.get('testCases', []):
        test_cases.append({
            "input": tc.get('input', ''),
            "expectedOutput": tc.get('expectedOutput', '')
        })

    # API 요청 바디 구성
    request_body = {
        "title": problem.get('title', 'Untitled'),
        "description": problem.get('description', ''),
        "content": content,
        "input_content": problem.get('inputDescription', ''),
        "output_content": problem.get('outputDescription', ''),
        "type": "PROBLEM",
        "category": problem.get('category', '알고리즘'),
        "difficulty": problem.get('difficulty', '브론즈'),
        "isPublic": False,  # 기본적으로 비공개
        "testCases": test_cases
    }

    return request_body


def send_lecture_to_api(
    problem: Dict[str, Any],
    api_url: str,
    user_id: int,
    max_retries: int = 3
) -> Dict[str, Any]:
    """
    문제를 API로 전송

    Returns:
        {"success": True/False, "message": "...", "response": {...}}
    """
    request_body = build_lecture_request(problem)
    headers = {
        "Content-Type": "application/json",
        "X-User-Id": str(user_id)
    }

    for attempt in range(1, max_retries + 1):
        try:
            response = requests.post(
                api_url,
                json=request_body,
                headers=headers,
                timeout=30
            )

            if response.status_code == 201:
                return {
                    "success": True,
                    "message": "생성 성공",
                    "response": response.json()
                }
            else:
                error_msg = f"HTTP {response.status_code}"
                try:
                    error_detail = response.json()
                    error_msg += f": {error_detail}"
                except:
                    error_msg += f": {response.text[:200]}"

                if attempt < max_retries:
                    time.sleep(1)  # 재시도 전 1초 대기
                    continue
                else:
                    return {
                        "success": False,
                        "message": error_msg,
                        "response": None
                    }

        except requests.exceptions.Timeout:
            if attempt < max_retries:
                time.sleep(2)
                continue
            else:
                return {
                    "success": False,
                    "message": "타임아웃 (30초 초과)",
                    "response": None
                }
        except requests.exceptions.ConnectionError:
            return {
                "success": False,
                "message": "연결 실패 - 서버가 실행 중인지 확인하세요",
                "response": None
            }
        except Exception as e:
            return {
                "success": False,
                "message": f"예외 발생: {str(e)}",
                "response": None
            }

    return {
        "success": False,
        "message": f"{max_retries}회 재시도 후 실패",
        "response": None
    }


def import_problems_via_api(
    json_file: str,
    api_url: str,
    user_id: int,
    failed_output: str = "failed_problems.json",
    max_retries: int = 3
):
    """
    수학 기호가 있는 문제만 HTTP API로 전송

    Args:
        json_file: 입력 JSON 파일 경로
        api_url: Lecture API 엔드포인트 URL
        user_id: X-User-Id 헤더에 사용할 사용자 ID
        failed_output: 실패한 문제를 저장할 JSON 파일
        max_retries: 실패 시 최대 재시도 횟수
    """
    print(f"📖 JSON 파일 읽기: {json_file}")

    with open(json_file, 'r', encoding='utf-8') as f:
        all_problems = json.load(f)

    print(f"✅ 총 {len(all_problems)}개의 문제를 읽었습니다.")

    # 수학 기호가 있는 문제만 필터링
    print(f"🔍 수학 기호 필터링 중...")
    problems = [p for p in all_problems if has_math_symbols(p)]
    problems_without_math = len(all_problems) - len(problems)

    print(f"✅ 필터링 완료:")
    print(f"   - 수학 기호 있음 (HTTP API 전송): {len(problems)}개 ({len(problems)/len(all_problems)*100:.1f}%)")
    print(f"   - 수학 기호 없음 (건너뜀): {problems_without_math}개 ({problems_without_math/len(all_problems)*100:.1f}%)")

    if len(problems) == 0:
        print("⚠️  HTTP API로 전송할 문제가 없습니다.")
        return

    print(f"\n🚀 HTTP API로 {len(problems)}개 문제 전송 시작...")
    print(f"   - API URL: {api_url}")
    print(f"   - User ID: {user_id}")
    print(f"   - 최대 재시도: {max_retries}회")
    print(f"=" * 60)

    # 통계
    success_count = 0
    failed_count = 0
    failed_problems = []
    start_time = time.time()

    for idx, problem in enumerate(problems, start=1):
        title = problem.get('title', 'Untitled')

        # 진행률 표시
        if idx % 10 == 0 or idx == 1:
            elapsed = time.time() - start_time
            rate = idx / elapsed if elapsed > 0 else 0
            eta = (len(problems) - idx) / rate if rate > 0 else 0
            print(f"⏳ [{idx}/{len(problems)}] 진행 중... (성공: {success_count}, 실패: {failed_count}, 속도: {rate:.1f}개/초, 예상 남은 시간: {eta/60:.1f}분)")

        # API 전송
        result = send_lecture_to_api(problem, api_url, user_id, max_retries)

        if result["success"]:
            success_count += 1
            if idx % 100 == 0:
                print(f"✅ [{idx}/{len(problems)}] '{title}' 생성 성공")
        else:
            failed_count += 1
            print(f"❌ [{idx}/{len(problems)}] '{title}' 실패: {result['message']}")
            failed_problems.append({
                "problem": problem,
                "error": result["message"],
                "index": idx
            })

        # API 서버 부하 방지를 위한 짧은 대기
        time.sleep(0.1)

    # 통계 출력
    elapsed_total = time.time() - start_time
    print("\n" + "=" * 60)
    print("📊 전송 완료 통계")
    print("=" * 60)
    print(f"총 문제 수: {len(problems)}")
    print(f"성공: {success_count} ({success_count/len(problems)*100:.1f}%)")
    print(f"실패: {failed_count} ({failed_count/len(problems)*100:.1f}%)")
    print(f"소요 시간: {elapsed_total/60:.1f}분")
    print(f"평균 속도: {len(problems)/elapsed_total:.1f}개/초")

    # 실패한 문제 저장
    if failed_problems:
        with open(failed_output, 'w', encoding='utf-8') as f:
            json.dump(failed_problems, f, ensure_ascii=False, indent=2)
        print(f"\n⚠️  실패한 {failed_count}개 문제가 {failed_output}에 저장되었습니다.")
    else:
        print(f"\n✅ 모든 문제가 성공적으로 전송되었습니다!")


def main():
    """메인 함수"""
    if len(sys.argv) < 2:
        print("사용법: python json_to_api.py <json_file> [api_url] [user_id]")
        print("\n예제:")
        print("  python json_to_api.py baekjoon_problems.json")
        print("  python json_to_api.py baekjoon_problems.json http://localhost:2358/api/lectures")
        print("  python json_to_api.py baekjoon_problems.json http://localhost:2358/api/lectures 5")
        print("\n기본값:")
        print("  api_url: http://localhost:2358/api/lectures")
        print("  user_id: 5 (kim.yuhee)")
        sys.exit(1)

    json_file = sys.argv[1]
    api_url = sys.argv[2] if len(sys.argv) > 2 else "http://localhost:2358/api/lectures"
    user_id = int(sys.argv[3]) if len(sys.argv) > 3 else 5

    print("=" * 60)
    print("🚀 백준 문제 JSON → HTTP API 전송 스크립트 (수학 기호 포함)")
    print("=" * 60)
    print(f"입력 파일: {json_file}")
    print(f"API URL: {api_url}")
    print(f"User ID: {user_id}")
    print(f"시작 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 60 + "\n")

    try:
        import_problems_via_api(json_file, api_url, user_id)
        print("\n" + "=" * 60)
        print("✨ 전송 완료!")
        print(f"종료 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("=" * 60)
    except FileNotFoundError:
        print(f"❌ 오류: 파일을 찾을 수 없습니다 - {json_file}")
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"❌ 오류: JSON 파싱 실패 - {e}")
        sys.exit(1)
    except KeyboardInterrupt:
        print(f"\n\n⚠️  사용자가 중단했습니다. (Ctrl+C)")
        sys.exit(1)
    except Exception as e:
        print(f"❌ 오류: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()
