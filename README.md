# android-accountbook-08
조대현의 가계부.^^~ 부자될래요~

## 데모 및 PT 영상 👉 [시청해주세요!](https://youtu.be/ilgce70zScA)

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
* ㄴㅅString 비교속도보다 Integer 비교 속도가 빠르기 때문이다.
* 하나의 Entry에 들어갈 수 있는 node가 적어지게 되면, 같은 데이터를 저장하는 Tree의 경우 Height가 증가한다
* 이건은 Select Query의 성능을 낮추는 치명적인 요인이다.

## payments, categories table의 PK가 Auto Increase Int인 이유
* payment, category 모두 해당 table을 설계할때 중복 불가능하게 설계했다.
* 그렇다면 해당 두 컬럼은 PK로 하면 되지 않느냐? 라는 의문을 가질 수 있다.
* 하지만 Index 자료구조 설계시 해당 PK를 Text Type으로 두게 되면 메모리 낭비가 상대적으로 더 된다는 점이 있다.
* 그리고 Int비교와 String 비교를 하게되면 Int를 비교하는것이 빠르기 때문이다.

## 수입, 지출 Toggle 상태에 따른 query 관리 👉 [구경해보기](https://github.com/woowa-techcamp-2022/android-accountbook-08/wiki/%EC%88%98%EC%9E%85,-%EC%A7%80%EC%B6%9C-Toggle-Button-%EC%84%A0%ED%83%9D-%EC%83%81%ED%83%9C%EC%97%90-%EB%94%B0%EB%A5%B8-Query-%EA%B4%80%EB%A6%AC)

## Branch List
### main
* 금요일 코드 프리징을 위한 branch 입니다.

### feat
* 기능 단위 개발 branch 입니다.

### dev
* feat branch의 merge용 branch 입니다. 각 기능별로 완성시 merge 합니다.
