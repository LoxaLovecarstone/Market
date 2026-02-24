package com.example.market.ui.common

/**
 * [가이드] 앱 전체에서 공통으로 사용할 "화면 상태 설계도"
 * * 1. <out T>: "공변성" 적용
 * - T를 생산(return)만 하겠다는 약속입니다.
 * - UiState<Nothing>을 UiState<String> 위치에 넣을 수 있습니다.
 * - Nothing이 String의 자식이므로 UiState<Nothing> 역시 UiState<String>가 되도록 out을 해줍니다.
 * * 2. Nothing: "최하위 타입"
 * - 모든 타입(String, Int, User 등)의 자식입니다.
 * - 데이터가 없는 Loading/Error를 모든 화면에서 돌려쓰기 위한 핵심 장치입니다.
 */
sealed interface UiState<out T> {

    /**
     * [Loading] : "데이터를 가져오는 중"
     * - 왜 'object'인가? : 로딩 신호는 데이터가 필요 없으므로 메모리 절약을 위해 딱 하나만 생성(싱글톤).
     * - 왜 'Nothing'인가? : Nothing은 모든 T의 자식이고, 'out' 덕분에 UiState<Nothing>은
     * 모든 UiState<T>의 자식이 되어 어디든 대입 가능합니다.
     */
    data object Loading : UiState<Nothing>

    /**
     * [Error] : "서버 통신 실패 혹은 예외 발생"
     * - 로딩과 마찬가지로 추가 데이터가 없는 신호 전달용이기에 object와 Nothing을 사용합니다.
     */
    data object Error : UiState<Nothing>

    /**
     * [Success] : "데이터 로드 성공"
     * - 왜 'class'인가? : 화면마다 가져오는 실제 데이터(T)가 다르므로, 매번 새로운 인스턴스를 생성해 데이터를 담아야 합니다.
     * - 왜 'T'인가? : 제네릭(<>)을 사용하여 어떤 타입의 데이터(상품 리스트, 유저 정보 등)든 유연하게 담기 위함입니다.
     * * @param data 실제로 UI에 그려줄 결과물 ($T$ 타입)
     */
    data class Success<T>(val data: T) : UiState<T>
    // 코틀린은 data class의 생성자를 그대로 멤버로 활용 가능
}

/* --------------------------------------------------------------------------
 * 💡 나중에 다시 볼 때 핵심 논리 (Logic Flow)
 * --------------------------------------------------------------------------
 * 1. [상속 제한] sealed interface는 자식이 Loading, Error, Success뿐임을 보장합니다.
 * -> UI에서 'when' 사용 시 else 문 없이 모든 케이스를 안전하게 처리 가능합니다.
 * * 2. [타입 마법]
 * - Nothing은 모든 타입의 자식이다. (Nothing < String, Nothing < Int ...)
 * - out은 껍데기의 자식 관계를 유지한다. (UiState<Nothing> < UiState<String>)
 * - 따라서 Loading(UiState<Nothing>) 하나만 만들면 모든 화면의 로딩 상태로 사용 가능하다!
 * * 3. [메모리/데이터]
 * - 데이터가 없으면 'object' (메모리 아끼기)
 * - 데이터가 있으면 'class' (데이터 실어 나르기)
 * -------------------------------------------------------------------------- */