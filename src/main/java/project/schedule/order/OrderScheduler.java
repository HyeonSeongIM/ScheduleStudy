package project.schedule.order;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;

    // cormaker 사이트 들어가서 하기
    @Scheduled(cron = "* * * * * *")
    public void processPendingOrders() {
        System.out.println("Processing pending orders...");
        List<Order> orders = orderRepository.findByStatus("PENDING");
        orders.forEach(order -> {
            order.setStatus("COMPLETED");
            orderRepository.save(order);
        });
        System.out.println("Pending orders processed " + orders.size());
    }



}
