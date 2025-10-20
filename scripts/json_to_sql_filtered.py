#!/usr/bin/env python3
"""
백준 문제 JSON을 SQL INSERT 구문으로 변환하는 스크립트 (수학 기호 필터링)
수학 기호($, \\)가 없는 문제만 SQL로 생성
V102__sample_lectures.sql 스키마에 맞춤
"""

import json
import sys
from datetime import datetime


def has_math_symbols(problem):
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


def escape_sql_string(value):
    """SQL 문자열 이스케이프 처리"""
    if value is None:
        return "NULL"
    return "'" + str(value).replace("'", "''").replace("\\", "\\\\") + "'"


def convert_to_sql(json_file, output_file, author_id=1):
    """
    JSON 파일을 SQL INSERT 구문으로 변환 (수학 기호 없는 문제만)

    Args:
        json_file: 입력 JSON 파일 경로
        output_file: 출력 SQL 파일 경로
        author_id: 강의 생성자 author_id (기본값: 1)
    """
    print(f"📖 JSON 파일 읽기: {json_file}")

    with open(json_file, 'r', encoding='utf-8') as f:
        all_problems = json.load(f)

    print(f"✅ 총 {len(all_problems)}개의 문제를 읽었습니다.")

    # 수학 기호가 없는 문제만 필터링
    print(f"🔍 수학 기호 필터링 중...")
    problems = [p for p in all_problems if not has_math_symbols(p)]
    problems_with_math = len(all_problems) - len(problems)

    print(f"✅ 필터링 완료:")
    print(f"   - 수학 기호 없음 (SQL 생성): {len(problems)}개 ({len(problems)/len(all_problems)*100:.1f}%)")
    print(f"   - 수학 기호 있음 (HTTP API 필요): {problems_with_math}개 ({problems_with_math/len(all_problems)*100:.1f}%)")

    with open(output_file, 'w', encoding='utf-8') as f:
        # SQL 파일 헤더
        f.write("-- 백준 문제 데이터 INSERT 스크립트 (수학 기호 없는 문제만)\n")
        f.write(f"-- 생성 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        f.write(f"-- 총 문제 수: {len(problems)}\n")
        f.write(f"-- 제외된 문제 수 (수학 기호 포함): {problems_with_math}\n")
        f.write(f"-- V102__sample_lectures.sql 스키마 호환\n\n")

        # admin 사용자가 없으면 생성 (안전장치)
        f.write("-- admin 사용자 생성 (없으면)\n")
        f.write("INSERT INTO users (username, login_id, password) VALUES\n")
        f.write("('관리자', 'admin', 'admin123')\n")
        f.write("ON CONFLICT (login_id) DO NOTHING;\n\n")

        # Lectures 테이블 INSERT 시작
        f.write("-- Lectures 테이블에 데이터 삽입\n\n")

        for idx, problem in enumerate(problems, start=1):
            # 기본 정보
            title = escape_sql_string(problem.get('title', 'Untitled'))
            description = escape_sql_string(problem.get('description', ''))

            # content: 문제 설명 전체를 마크다운 형식으로
            content_parts = []
            if problem.get('description'):
                content_parts.append(f"# {problem.get('title', 'Untitled')}\n\n{problem.get('description')}")
            if problem.get('inputDescription'):
                content_parts.append(f"\n\n## 입력\n{problem.get('inputDescription')}")
            if problem.get('outputDescription'):
                content_parts.append(f"\n\n## 출력\n{problem.get('outputDescription')}")

            content = escape_sql_string('\n'.join(content_parts) if content_parts else '')

            # input/output content
            input_content = escape_sql_string(problem.get('inputDescription', ''))
            output_content = escape_sql_string(problem.get('outputDescription', ''))

            # 분류 정보
            lecture_type = problem.get('type', 'PROBLEM')
            category = escape_sql_string(problem.get('category', '알고리즘'))
            difficulty = escape_sql_string(problem.get('difficulty', '브론즈'))

            # 제한 정보 (NULL 허용)
            time_limit = problem.get('timeLimit')
            memory_limit = problem.get('memoryLimit')

            # 공개 여부
            is_public = 'true' if problem.get('isPublic', False) else 'false'

            # duration_minutes (NULL 허용)
            duration_minutes = 'NULL'

            # thumbnail (NULL 허용)
            thumbnail = 'NULL'

            # constraints_id (테스트 케이스가 있으면 1, 없으면 NULL)
            test_cases = problem.get('testCases', [])
            constraints_id = '1' if test_cases else 'NULL'

            # Lectures INSERT 구문 (V102 스키마에 맞춤)
            f.write(f"-- 문제 {idx}: {problem.get('title', 'Untitled')}\n")
            f.write("INSERT INTO lectures (\n")
            f.write("  title, description, content,\n")
            f.write("  input_content, output_content,\n")
            f.write("  type, category, difficulty,\n")
            f.write("  is_public, thumbnail_image_url, duration_minutes,\n")
            f.write("  author_id, constraints_id,\n")
            f.write("  created_at, updated_at\n")
            f.write(") VALUES (\n")
            f.write(f"  {title},\n")
            f.write(f"  {description},\n")
            f.write(f"  {content},\n")
            f.write(f"  {input_content},\n")
            f.write(f"  {output_content},\n")
            f.write(f"  '{lecture_type}',\n")
            f.write(f"  {category},\n")
            f.write(f"  {difficulty},\n")
            f.write(f"  {is_public},\n")
            f.write(f"  {thumbnail},\n")
            f.write(f"  {duration_minutes},\n")
            f.write(f"  (SELECT id FROM users WHERE login_id = ")

            # 모든 강의를 admin 사용자(id=1)로 설정
            f.write("'admin'")

            f.write("),\n")
            f.write(f"  {constraints_id},\n")
            f.write(f"  NOW(),\n")
            f.write(f"  NOW()\n")
            f.write(");\n\n")

            # 테스트케이스 INSERT 구문
            if test_cases:
                f.write(f"-- 테스트케이스 (문제 {idx})\n")
                f.write("DO $$\n")
                f.write("DECLARE\n")
                f.write("  lecture_id_var INTEGER;\n")
                f.write("BEGIN\n")
                f.write(f"  -- 방금 삽입한 lecture의 ID 찾기\n")
                f.write(f"  SELECT id INTO lecture_id_var FROM lectures\n")
                f.write(f"  WHERE title = {title}\n")
                f.write(f"  ORDER BY created_at DESC LIMIT 1;\n\n")

                for tc_idx, test_case in enumerate(test_cases, start=1):
                    tc_input = escape_sql_string(test_case.get('input', ''))
                    tc_output = escape_sql_string(test_case.get('expectedOutput', ''))
                    order_index = test_case.get('orderIndex', tc_idx)

                    f.write(f"  -- 테스트케이스 {tc_idx}\n")
                    f.write(f"  INSERT INTO test_cases (lecture_id, input, expected_output, order_index)\n")
                    f.write(f"  VALUES (lecture_id_var, {tc_input}, {tc_output}, {order_index});\n\n")

                f.write("END $$;\n\n")

            # 진행상황 출력
            if idx % 100 == 0:
                print(f"⏳ {idx}/{len(problems)} 문제 변환 중...")

        f.write("-- 완료!\n")

    print(f"\n✅ SQL 파일 생성 완료: {output_file}")
    print(f"📊 총 {len(problems)}개의 문제가 SQL로 변환되었습니다.")
    print(f"⚠️  {problems_with_math}개의 문제는 수학 기호를 포함하므로 HTTP API로 입력해야 합니다.")


def main():
    """메인 함수"""
    if len(sys.argv) < 2:
        print("사용법: python json_to_sql_filtered.py <json_file> [output_file] [author_id]")
        print("\n예제:")
        print("  python json_to_sql_filtered.py baekjoon_problems.json")
        print("  python json_to_sql_filtered.py baekjoon_problems.json output.sql")
        print("  python json_to_sql_filtered.py baekjoon_problems.json output.sql 1")
        print("\nauthor_id:")
        print("  1 = kim.yuhee")
        print("  2 = lee.seojun")
        print("  3 = park.gaeun")
        sys.exit(1)

    json_file = sys.argv[1]
    output_file = sys.argv[2] if len(sys.argv) > 2 else "V103__baekjoon_problems_no_math.sql"
    author_id = int(sys.argv[3]) if len(sys.argv) > 3 else 1

    print("=" * 60)
    print("🚀 백준 문제 JSON → SQL 변환 스크립트 (수학 기호 필터링)")
    print("=" * 60)
    print(f"입력 파일: {json_file}")
    print(f"출력 파일: {output_file}")
    print(f"Author ID: {author_id}")
    print("=" * 60 + "\n")

    try:
        convert_to_sql(json_file, output_file, author_id)
        print("\n" + "=" * 60)
        print("✨ 변환 완료!")
        print("=" * 60)
    except FileNotFoundError:
        print(f"❌ 오류: 파일을 찾을 수 없습니다 - {json_file}")
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"❌ 오류: JSON 파싱 실패 - {e}")
        sys.exit(1)
    except Exception as e:
        print(f"❌ 오류: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()
