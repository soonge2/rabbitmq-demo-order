

### RabbitMQ 실행 (Docker)
- 직접 설치 대신 도커로 RabbitMQ 컨테이너를 띄워서 서버로 사용합니다. RabbitMQ는 공식 Docker 이미지가 있어 바로 띄울 수 있습니다.
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3-management
# 관리 콘솔: http://localhost:15672  (guest/guest)
```

### 실행 & 테스트
1. 애플리케이션 실행
2. 발행 요청 (아무 터미널에서): `curl -X POST "http://localhost:8080/api/orders/create?orderId=101&userId=alice&amount=3"`
3. 애플리케이션 로그에 수신 메시지 출력 확인
4. 관리 콘솔(http://localhost:15672)에서 Exchanges/Queues 상태도 확인

