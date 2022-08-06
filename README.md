# android-accountbook-08
조대현의 가계부.^^~ 부자될래요~

## 데모 및 PT 영상 👉 [시청해주세요!](https://youtu.be/ilgce70zScA)

# 수입, 지출 Toggle 버튼 상태에 따른 상태 관리법
스크린샷 | 설명 | bit 값
-- | -- | --
<img width="380" height="130" alt="전체 선택 상태" src="https://user-images.githubusercontent.com/68371979/181859856-00ff0225-75d5-4e4d-a92a-c7e11b76c950.png"> | 전체 선택 상태 | 11
<img width="380" height="130" alt="수입 선택 상태" src="https://user-images.githubusercontent.com/68371979/181859848-f30a5d6c-18b1-4a0d-ab94-32196ea27d49.png"> | 수입 선택 상태 | 10
<img width="380" height="130" alt="image" src="https://user-images.githubusercontent.com/68371979/181861061-442e8e5a-6c57-4aeb-8abf-f53cfbbb1e74.png"> | 지출 선택 상태 | 01
<img width="380" height="130" alt="아무것도 선택되지 않은 상태" src="https://user-images.githubusercontent.com/68371979/181860328-1aa5656e-f404-47bc-89a2-1268e433d3cf.png"> | 미선택 상태 | 00

## bit 값을 계산한 이유

<aside>
💡 Query시 Table에서 is_expense 값을 filtering하기 위하여

</aside>

## bit값 산정 방법

- ViewModel에서 각 각 Toggle Button이 선택되어있는지 유무를 Boolean값으로 관리
- Booelan값을 1 or 0으로 매핑 가능
- 수출 Boolean값의 경우

```kotlin
incomeBit shl 1
```

- 이렇게 진행한다면 1 → 10, 0 → 00 으로 생각할 수 있다
- 그리고 expenseBit와 or 연산을 진행하면 위 표와 같은 bit값을 얻을 수 있게 된다.

```kotlin
(incomeBit shl 1) or expenseBit
```

- 참고로 Boolean값을 Int값으로 mapping해주기 위하여 아래와 같은 Extenstion함수를 만들었다.

```kotlin
fun Boolean.toInt() = if (this) 1 else 0
```

```kotlin
val isExpenseLiveData = combine(historyIncomeChecked.asFlow(), historyExpenseChecked.asFlow()){
        income, expense ->
        val incomeBit = income.toInt()
        val expenseBit = expense.toInt()
        (incomeBit shl 1) or expenseBit
    }.asLiveData()
```

# Database ER Diagram

<img width="1000" alt="image" src="https://user-images.githubusercontent.com/68371979/180950487-cc7cd753-ffd6-41f5-88ba-42dd2986333f.png">

# histories table
* PK: id
* FK: payments table의 payments_id, categories table의 category_id
* Index: date DESC
* FK선정 이유: payments or categories의 색상 또는 이름이 update 되는 경우, 해당 payments, categories를 갖고 있는 history 내역도 update 되어야 하기 때문이다.

# payments table
* PK, Index: payments_id
* Unique Column: payment 

# categories table
* PK, Index: category_id
* Unique Column: category 

## histories table의 date값 Long Type인 이유
* 일정 날짜 이내의 내역들을 갖고와야 할 시점들이 있다.
* SQLite의 Index는 B-tree 구조다.
* Range Query에 특화된 자료구조 이므로, Long Type으로 저장하였다.
* String 비교속도보다 Integer 비교 속도가 빠르기 때문이다.
* 하나의 Entry에 들어갈 수 있는 node가 적어지게 되면, 같은 데이터를 저장하는 Tree의 경우 Height가 증가한다
* 이건은 Select Query의 성능을 낮추는 치명적인 요인이다.

## payments, categories table의 PK가 Auto Increase Int인 이유
* payment, category 모두 해당 table을 설계할때 중복 불가능하게 설계했다.
* 그렇다면 해당 두 컬럼은 PK로 하면 되지 않느냐? 라는 의문을 가질 수 있다.
* 하지만 Index 자료구조 설계시 해당 PK를 Text Type으로 두게 되면 메모리 낭비가 상대적으로 더 된다는 점이 있다.
* 그리고 Int비교와 String 비교를 하게되면 Int를 비교하는것이 빠르기 때문이다.

# Branch List
### main
* 금요일 코드 프리징을 위한 branch 입니다.

### feat
* 기능 단위 개발 branch 입니다.

### dev
* feat branch의 merge용 branch 입니다. 각 기능별로 완성시 merge 합니다.
