## 주문 시 재고 차감 로직의 비관적 락으로 인한 데드락 문제 분석 및 해결

---

### 1. 문제 배경
주문 과정에서 **비관적 락**을 사용하고 있어, 여러 스레드가 동시에 주문을 처리할 때 데드락이 발생할 가능성이 있습니다.
이 문제를 파악하고 해결하기 위해 동시성 테스트를 진행하였습니다.

###  2. 동시 주문 시 데드락 발생 테스트 및 분석
아래는 데드락 발생 여부를 테스트하기 위해 작성한 코드입니다.
```
    @Test
    @DisplayName("주문 동시 요청 시 데드락 발생 하는지 테스트")
    void deadLockTest() throws ExecutionException, InterruptedException {
        int concurrentRequests = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);

        OrderDetailDto detail1 = OrderDetailDto.builder().productId(1L).quantity(1).build();
        OrderDetailDto detail2 = OrderDetailDto.builder().productId(2L).quantity(1).build();

        // 서로 다른 OrderDto로 다른 스레드에서 접근하도록 설정
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 1; i <= concurrentRequests; i++) {
            final int threadIndex = i;
            // 홀수 스레드 detail1 -> detail2 순서로 접근
            List<OrderDetailDto> orderDetails = (i % 2 == 0) ? List.of(detail1, detail2) : List.of(detail2, detail1);
            OrderDto orderDto = OrderDto.builder().userId((long) i).orderDetails(orderDetails).build();

            Callable<Void> task = () -> {
                try {
                    paymentFacade.orderPayment(orderDto);
                } catch (OrderFailedException e) {
                    System.err.println("예외 발생 (스레드 " + threadIndex + "): " + e.getMessage());
                    throw e;
                }
                return null;
            };
            tasks.add(task);
        }
        // 동시 실행 요청을 invokeAll로 실행
        List<Future<Void>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();

        // 예외가 발생하지 않았는지 확인
        assertDoesNotThrow(() -> {
            for (Future<Void> future : futures) {
                future.get(); 
            }
        });

    }
```
###  3. 테스트 결과 분석
테스트 결과, 아래와 같은 로그가 출력되며 데드락이 발생했음을 확인했습니다.
```
2024-11-03T18:22:08.516+09:00 ERROR 6884 --- [ecommerce] [pool-3-thread-2] i.h.e.d.s.product.ProductStockService    : 예외발생 userId: 2, 무슨 예외: JDBC exception executing SQL [select p1_0.product_id,p1_0.name,p1_0.price,p1_0.stock from product p1_0 where p1_0.product_id=? for update] [Deadlock found when trying to get lock; try restarting transaction] [n/a]; SQL [n/a]
2024-11-03T18:22:08.516+09:00 ERROR 6884 --- [ecommerce] [pool-3-thread-2] i.h.e.application.facade.PaymentFacade   : 무슨 예외: JDBC exception executing SQL [select p1_0.product_id,p1_0.name,p1_0.price,p1_0.stock from product p1_0 where p1_0.product_id=? for update] [Deadlock found when trying to get lock; try restarting transaction] [n/a]; SQL [n/a]
```
###  4. 데드락 발생 원인
두 스레드가 서로 다른 상품에 접근하는 과정에서 데드락이 발생했습니다.
###  **발생과정**
1. 스레드 1: productId=1에 락을 설정
2. 스레드 2: productId=2에 락을 설정
3. 스레드 1: productId=2에 접근하려다 대기 상태로 진입
4. 스레드 2: productId=1에 접근하려다 대기 상태로 진입

###  5. 의문점: 스레드 2는 예외가 발생했지만 스레드 1은 정상 실행됨

테스트 로그에서 데드락이 발생한 것으로 확인되었으나, 스레드 1은 정상적으로 실행을 완료하고 예외가 발생하지 않은 반면, 스레드 2만 예외가 발생하여 실패했습니다. 일반적인 데드락 상황에서 두 스레드 모두가 실행되지 않아야 한다는 기존 지식과 달라 의아했습니다.

**`확인 결과`**, MySQL의 데드락 감지 알고리즘이 이러한 상황을 처리한다는 것을 알게 되었습니다. MySQL은 데드락이 감지되면 두 스레드 중 하나의 트랜잭션을 강제로 롤백하여 나머지 하나의 스레드가 정상적으로 실행될 수 있도록 합니다. 이번 경우 스레드 2가 롤백되고 스레드 1은 정상적으로 실행되었던 것이었습니다.

###  6. 해결 방안: productId 기준 정렬 적용
데드락 문제를 해결하기 위해 재고 차감 로직에 **`Comparator.comparingLong`**을 사용해서 `productId` 기준으로 오름차순 정렬을 적용했습니다.  모든 스레드가 정렬된 순서로 `product`에 접근하게 되면서 방지할 수 있게 코드를 수정했습니다.
```
List<OrderDetailDto> orderedDetails = ordersDto.getOrderDetails().stream()
.sorted(Comparator.comparingLong(OrderDetailDto::getProductId))
.toList();
```