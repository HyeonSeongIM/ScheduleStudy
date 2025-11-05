# ScheduleStudy
<배치 사용 시>

스프링 배치에서 주요 사항은 트랜잭션과 청크다.

### 실행단계
Step 실행되면, 새로운 트랜잭션을 시작
ItemReader가 DB에서 데이터를 읽어옴. 이 데이터들은 메모리에 리스트로 쌓이게 됨
메모리에 있는 100개의 아이템을 하나씩 ItemProcessor에 보냄 (데이터 가공 및 변환)
ItemWriter는 가공된 100개의 아이템 리스트를 단 한번에 DB에 저장
ItemWirter가 성공하면 아직 트랜잭션이 끝나기 전에 JobRepo (배치 메타데이터 테이블)에 "이 Step은 작업을 성공적으로 마쳤다." 라고 기록
비즈니스 데이터와 메타데이터 작업이 하나의 트랜잭션으로 묶여서 동시에 커밋

### 핵심 컴포넌트 JobRepository
이건 추상적인 개념이 아니라, 실제 DB 테이블

--- Job 시작 시 --- 
JobInstance 확인
JobLauncher가 Job을 실행하라는 요청을 받으면, JobRepository에게 "이 Job 이름과 JobParameters(작업 매개변수)를 가진 JobInstance(작업 이력)가 DB에 있나요?" 라고 물어봄

"새 작업 vs 재시작"
처음 보는 작업이면 (New): JobRepository는 새 JobInstance를 만들고(DB INSERT), 이 JobInstance에 연결된 JobExecution(작업 실행)을 "STARTING" 상태로 새로 INSERT합니다.
실패했던 작업이면 (Restart): JobRepository는 기존 JobInstance를 찾고, JobExecution을 "STARTING" 상태로 새로 INSERT합니다.

--- Step 실행 및 청크 커밋 시 ---
Step 시작 JobExecution에 연결된 StepExecution을 STARTING 상태로 INSERT
청크 처리 Reader 100회 Processor 100회 Writer 1회
JobRep 업데이트 Writer가 성공적으로 끝나면 트랜잭션이 커밋되기 전에 JobRepo는 같은 트랜잭션 안에서 BATCH_STEP_EXECUTION 테이블을 업데이트


