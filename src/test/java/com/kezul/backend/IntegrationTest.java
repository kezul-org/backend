package com.kezul.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 통합 테스트용 공통 어노테이션.
 *
 * <p>
 * 이 어노테이션을 테스트 클래스에 붙이면:
 * <ul>
 * <li>Spring 전체 컨텍스트를 로드합니다.</li>
 * <li>"test" 프로파일을 활성화합니다.</li>
 * <li>TestContainers MySQL을 자동으로 시작하고 DataSource에 주입합니다.</li>
 * </ul>
 *
 * <p>
 * 사용 예시:
 * 
 * <pre>
 * {@code
 * @IntegrationTest
 * class UserServiceIntegrationTest {
 *     // ...
 * }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public @interface IntegrationTest {
}
