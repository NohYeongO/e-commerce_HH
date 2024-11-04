package io.hhplus.ecommerce.support;

import io.hhplus.ecommerce.application.facade.CartFacade;
import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.application.facade.ProductFacade;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.domain.service.cart.FindCartService;
import io.hhplus.ecommerce.domain.service.cart.UpdateCartService;
import io.hhplus.ecommerce.domain.service.order.OrderService;
import io.hhplus.ecommerce.domain.service.product.FindProductService;
import io.hhplus.ecommerce.domain.service.product.ProductStockService;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.domain.service.user.PriceDeductionService;
import io.hhplus.ecommerce.infra.cart.CartJpaRepository;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestSupport.class);

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
    protected CartFacade cartFacade;

    @Autowired
    protected PaymentFacade paymentFacade;

    @Autowired
    protected ProductFacade productFacade;

    @Autowired
    protected FindCartService findCartService;

    @Autowired
    protected UpdateCartService updateCartService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected FindProductService findProductService;

    @Autowired
    protected ProductStockService productStockService;

    @Autowired
    protected ChargeUserService chargeUserService;

    @Autowired
    protected FindUserService findUserService;

    @Autowired
    protected PriceDeductionService priceDeductionService;

    @Autowired
    protected ProductJpaRepository productJpaRepository;

    @Autowired
    protected OrderJpaRepository orderJpaRepository;

    @Autowired
    protected CartJpaRepository cartJpaRepository;

    @Autowired
    protected UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        orderJpaRepository.deleteAll();
        cartJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        orderJpaRepository.deleteAll();
        cartJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }
}