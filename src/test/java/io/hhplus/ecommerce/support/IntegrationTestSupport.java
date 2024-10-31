package io.hhplus.ecommerce.support;

import io.hhplus.ecommerce.application.facade.CartFacade;
import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.application.facade.ProductFacade;
import io.hhplus.ecommerce.application.service.OrderLockService;
import io.hhplus.ecommerce.domain.service.cart.FindCartService;
import io.hhplus.ecommerce.domain.service.cart.UpdateCartService;
import io.hhplus.ecommerce.domain.service.order.OrderService;
import io.hhplus.ecommerce.domain.service.product.FindProductService;
import io.hhplus.ecommerce.domain.service.product.StockDeductionService;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.domain.service.user.PriceDeductionService;
import io.hhplus.ecommerce.infra.cart.CartJpaRepository;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0"))
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void setUpProperties(DynamicPropertyRegistry registry) {
        // MySQL 설정을 Testcontainers로 덮어쓰기
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        // Redis 설정을 Testcontainers로 덮어쓰기
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    public CartFacade cartFacade;

    @Autowired
    public PaymentFacade paymentFacade;

    @Autowired
    public ProductFacade productFacade;

    @Autowired
    public OrderLockService orderLockService;

    @Autowired
    public FindCartService findCartService;

    @Autowired
    public UpdateCartService updateCartService;

    @Autowired
    public OrderService orderService;

    @Autowired
    public FindProductService findProductService;

    @Autowired
    public StockDeductionService stockDeductionService;

    @Autowired
    public ChargeUserService chargeUserService;

    @Autowired
    public FindUserService findUserService;

    @Autowired
    public PriceDeductionService priceDeductionService;

    @Autowired
    public ProductJpaRepository productJpaRepository;

    @Autowired
    public OrderJpaRepository orderJpaRepository;

    @Autowired
    public CartJpaRepository cartJpaRepository;

    @Autowired
    public UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        productJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        cartJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }
}