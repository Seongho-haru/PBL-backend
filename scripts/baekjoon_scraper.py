import requests
from bs4 import BeautifulSoup
import json
import time
import sys
import re
from typing import Dict, List, Optional


class BaekjoonScraper:
    """백준 온라인 저지 문제를 크롤링하여 Lecture DTO 형식의 JSON으로 변환하는 클래스"""

    BASE_URL = "https://www.acmicpc.net"
    SOLVED_AC_API = "https://solved.ac/api/v3"

    def __init__(self, use_solved_ac: bool = True):
        """
        Args:
            use_solved_ac: solved.ac API를 사용하여 난이도 정보를 가져올지 여부
        """
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        })
        self.use_solved_ac = use_solved_ac

    def parse_time_limit(self, time_str: str) -> Optional[float]:
        """
        시간 제한 문자열을 초 단위로 변환 (소수점 지원)
        예: "2 초 " -> 2.0
        예: "0.5 초 " -> 0.5
        """
        if not time_str:
            return None
        match = re.search(r'(\d+\.?\d*)', time_str)
        return float(match.group(1)) if match else None

    def parse_memory_limit(self, memory_str: str) -> Optional[int]:
        """
        메모리 제한 문자열을 KB 단위로 변환
        예: "128 MB" -> 131072 (128 * 1024)
        예: "256 MB" -> 262144 (256 * 1024)
        """
        if not memory_str:
            return None
        match = re.search(r'(\d+)', memory_str)
        if match:
            mb_value = int(match.group(1))
            return mb_value * 1024  # MB를 KB로 변환
        return None

    def extract_text_with_math(self, element) -> str:
        """
        HTML 요소에서 텍스트를 추출합니다.
        MathJax 수식 ($...$, $$...$$)은 보존합니다.

        Args:
            element: BeautifulSoup element

        Returns:
            MathJax 수식이 포함된 텍스트 (마크다운 형식)
        """
        if not element:
            return ""

        # BeautifulSoup의 get_text()는 수식도 같이 추출하므로 안전
        # 줄바꿈과 공백 정리
        text = element.get_text(separator='\n', strip=False)

        # 연속된 빈 줄을 하나로 정리
        text = re.sub(r'\n\s*\n\s*\n+', '\n\n', text)

        return text.strip()

    def get_problem_info_from_solved_ac(self, problem_id: int) -> Dict[str, any]:
        """
        solved.ac API를 통해 문제 난이도와 태그를 가져옵니다.

        Args:
            problem_id: 백준 문제 번호

        Returns:
            딕셔너리 {'difficulty': str, 'tags': List[str]} 또는 {'difficulty': None, 'tags': []}
        """
        if not self.use_solved_ac:
            return {'difficulty': None, 'tags': []}

        try:
            # solved.ac API v3 엔드포인트: GET /problem/show?problemId={problemId}
            url = f"{self.SOLVED_AC_API}/problem/show"
            params = {"problemId": problem_id}
            response = self.session.get(url, params=params, timeout=5)

            if response.status_code == 200:
                data = response.json()
                level = data.get('level', 0)

                # 난이도 매핑
                if level == 0:
                    difficulty = "미등급"
                elif 1 <= level <= 5:
                    difficulty = "브론즈"
                elif 6 <= level <= 10:
                    difficulty = "실버"
                elif 11 <= level <= 15:
                    difficulty = "골드"
                elif 16 <= level <= 20:
                    difficulty = "플래티넘"
                elif 21 <= level <= 25:
                    difficulty = "다이아몬드"
                elif 26 <= level <= 30:
                    difficulty = "챌린저"
                else:
                    difficulty = "미등급"

                # 태그 추출 (한국어 이름)
                tags = []
                for tag_obj in data.get('tags', []):
                    # displayNames 배열에서 한국어 이름 찾기
                    for display_name in tag_obj.get('displayNames', []):
                        if display_name.get('language') == 'ko':
                            tags.append(display_name.get('name', ''))
                            break

                return {'difficulty': difficulty, 'tags': tags}
            else:
                return {'difficulty': None, 'tags': []}

        except Exception as e:
            print(f"  ⚠ solved.ac 정보 조회 실패: {e}")
            return {'difficulty': None, 'tags': []}

    def get_problem_info(self, problem_id: int) -> Optional[Dict]:
        """
        특정 문제 번호의 상세 정보를 가져옵니다.

        Args:
            problem_id: 백준 문제 번호

        Returns:
            Lecture DTO 형식의 문제 정보 딕셔너리 또는 None
        """
        url = f"{self.BASE_URL}/problem/{problem_id}"

        try:
            response = self.session.get(url, timeout=10)

            if response.status_code == 404:
                print(f"문제 {problem_id}을(를) 찾을 수 없습니다.")
                return None

            response.raise_for_status()
            soup = BeautifulSoup(response.text, 'html.parser')

            # 문제 제목
            title_elem = soup.select_one('#problem_title')
            if not title_elem:
                print(f"문제 {problem_id}의 제목을 찾을 수 없습니다.")
                return None
            title = f"{title_elem.text.strip()}"

            # 문제 설명 (텍스트 형식으로 추출 - MathJax 수식 보존)
            description_elem = soup.select_one('#problem_description')
            description = self.extract_text_with_math(description_elem) if description_elem else ""

            # 입력 설명 (텍스트 형식으로 추출 - MathJax 수식 보존)
            input_elem = soup.select_one('#problem_input')
            input_desc = self.extract_text_with_math(input_elem) if input_elem else ""

            # 출력 설명 (텍스트 형식으로 추출 - MathJax 수식 보존)
            output_elem = soup.select_one('#problem_output')
            output_desc = self.extract_text_with_math(output_elem) if output_elem else ""

            # 난이도 및 태그 가져오기 (solved.ac API)
            solved_info = self.get_problem_info_from_solved_ac(problem_id)
            difficulty = solved_info.get('difficulty')
            tags = solved_info.get('tags', [])

            if difficulty:
                print(f"  난이도: {difficulty}")
            if tags:
                print(f"  태그: {', '.join(tags)}")

            # 시간 제한, 메모리 제한
            time_limit = None
            memory_limit = None

            info_table = soup.select_one('#problem-info')
            if info_table:
                rows = info_table.select('tr')
                for row in rows:
                    cells = row.select('td')
                    if len(cells) >= 2:
                        time_limit = self.parse_time_limit(cells[0].text)
                        memory_limit = self.parse_memory_limit(cells[1].text)
                        break

            # 예제 입출력 (TestCase 형식)
            test_cases = []
            sample_inputs = soup.select('.sampledata[id^="sample-input-"]')
            sample_outputs = soup.select('.sampledata[id^="sample-output-"]')

            for idx, (input_elem, output_elem) in enumerate(zip(sample_inputs, sample_outputs), 1):
                input_text = input_elem.get_text()
                output_text = output_elem.get_text()

                # 끝 공백/개행 제거 (백준 저지 시스템과 동일하게)
                input_text = input_text.rstrip()
                output_text = output_text.rstrip()

                test_cases.append({
                    "input": input_text,
                    "expectedOutput": output_text,
                    "orderIndex": idx
                })

            # Lecture DTO 형식으로 생성
            lecture_data = {
                "title": title,
                "description": description,
                "inputDescription": input_desc,
                "outputDescription": output_desc,
                "type": "PROBLEM",
                "category": "알고리즘",  # 기본값
                "difficulty": difficulty,  # solved.ac에서 가져온 난이도
                "timeLimit": time_limit,
                "memoryLimit": memory_limit,
                "isPublic": False,  # 기본값
                "testCases": test_cases,
                "tags": tags,  # solved.ac에서 가져온 태그 (알고리즘 분류)
                "metadata": {
                    "source": "백준 온라인 저지",
                    "problemId": problem_id,
                    "url": url
                }
            }

            print(f"✓ 문제 {problem_id}: {title}")
            return lecture_data

        except requests.exceptions.RequestException as e:
            print(f"✗ 문제 {problem_id} 크롤링 실패: {e}")
            return None
        except Exception as e:
            print(f"✗ 문제 {problem_id} 파싱 오류: {e}")
            import traceback
            traceback.print_exc()
            return None

    def get_multiple_problems(self, problem_ids: List[int], output_file: str, delay: float = 1.0) -> List[Dict]:
        """
        여러 문제의 정보를 가져오고 실시간으로 저장합니다.

        Args:
            problem_ids: 문제 번호 리스트
            output_file: 출력 파일 경로
            delay: 요청 간 대기 시간(초) - 기본 1초

        Returns:
            Lecture DTO 형식의 문제 정보 리스트
        """
        problems = []
        total = len(problem_ids)

        for idx, problem_id in enumerate(problem_ids, 1):
            print(f"\n[{idx}/{total}] 문제 {problem_id} 크롤링 중...")
            problem_data = self.get_problem_info(problem_id)

            if problem_data:
                problems.append(problem_data)
                # 실시간으로 JSON 파일에 추가 저장
                self.append_to_json(problem_data, output_file)
                print(f"  → 저장됨 (총 {len(self.load_existing_problems(output_file))}개)")

            # 서버 부하를 줄이기 위한 딜레이 (마지막 요청 이후에는 대기 불필요)
            if idx < total:
                time.sleep(delay)

        return problems

    def load_existing_problems(self, output_file: str) -> List[Dict]:
        """
        기존 JSON 파일에서 문제 목록을 불러옵니다.

        Args:
            output_file: JSON 파일 경로

        Returns:
            기존 문제 리스트
        """
        import os
        if os.path.exists(output_file):
            try:
                with open(output_file, 'r', encoding='utf-8') as f:
                    return json.load(f)
            except Exception as e:
                print(f"⚠ 기존 파일 로드 실패: {e}")
                return []
        return []

    def save_to_json(self, problems: List[Dict], output_file: str):
        """
        문제 정보를 JSON 파일로 저장합니다.

        Args:
            problems: Lecture DTO 형식의 문제 정보 리스트
            output_file: 출력 파일 경로
        """
        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(problems, f, ensure_ascii=False, indent=2)
        except Exception as e:
            print(f"\n✗ JSON 저장 실패: {e}")

    def append_to_json(self, problem: Dict, output_file: str):
        """
        문제 하나를 JSON 파일에 추가합니다 (실시간 저장).

        Args:
            problem: 추가할 문제 정보
            output_file: 출력 파일 경로
        """
        try:
            # 기존 파일 읽기
            problems = self.load_existing_problems(output_file)

            # 중복 체크 (같은 문제 번호가 있으면 덮어쓰기)
            problem_id = problem['metadata']['problemId']
            problems = [p for p in problems if p['metadata']['problemId'] != problem_id]

            # 새 문제 추가
            problems.append(problem)

            # 파일에 저장
            self.save_to_json(problems, output_file)

        except Exception as e:
            print(f"✗ 문제 {problem_id} 저장 실패: {e}")


def main():
    """메인 실행 함수"""

    print("=" * 60)
    print("백준 문제 크롤러 (Lecture DTO 형식)")
    print("=" * 60)

    # 명령줄 인자 처리
    if len(sys.argv) > 1:
        try:
            # 연속된 문제 번호 범위 지원 (예: 1000-1010)
            problem_ids = []
            for arg in sys.argv[1:]:
                if '-' in arg and not arg.startswith('-'):
                    # 범위 형식 (예: 1000-1010)
                    start, end = map(int, arg.split('-'))
                    problem_ids.extend(range(start, end + 1))
                else:
                    # 개별 문제 번호
                    problem_ids.append(int(arg))
        except ValueError:
            print("오류: 문제 번호는 정수이거나 범위(예: 1000-1010) 형식이어야 합니다.")
            sys.exit(1)
    else:
        # 기본 예시 문제들
        print("\n사용법:")
        print("  python baekjoon_scraper.py 1000 1001 1008    # 개별 문제")
        print("  python baekjoon_scraper.py 1000-1010         # 범위 지정")
        print("  python baekjoon_scraper.py 1000 1008-1010    # 혼합\n")
        print("예시로 몇 가지 문제를 크롤링합니다...")
        problem_ids = [1000, 1001, 1008]

    scraper = BaekjoonScraper()
    output_file = "baekjoon_problems.json"

    # 기존 파일 확인
    existing_problems = scraper.load_existing_problems(output_file)
    if existing_problems:
        print(f"\n📂 기존 파일 발견: {len(existing_problems)}개 문제 저장됨")
        print("   → 새로운 문제를 이어서 추가합니다.")

    # 문제 정보 수집 (1초 간격, 실시간 저장)
    problems = scraper.get_multiple_problems(problem_ids, output_file, delay=1.0)

    # 최종 결과
    total_problems = scraper.load_existing_problems(output_file)
    print("\n" + "=" * 60)
    print(f"✓ 크롤링 완료!")
    print(f"✓ 이번 세션: {len(problems)}개 수집")
    print(f"✓ 전체 문제: {len(total_problems)}개 저장됨")
    print(f"✓ 파일 경로: {output_file}")
    print("=" * 60)
    print("\n💡 다음 실행 시 자동으로 이어서 크롤링됩니다.")
    print("   예: python baekjoon_scraper.py 2001-3000")


if __name__ == "__main__":
    main()
