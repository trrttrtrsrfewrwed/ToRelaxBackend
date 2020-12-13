package backend.repository;

import backend.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    @Before
    public void setup() {
        User testUser = new User();
        testUser.setName("test");
        testUser.setEmail("email");
        testUser.setPassword("qwerty");
        testUser.setData("[]".getBytes());
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void getByEmail() {
        User actualUser = userRepository.getByEmail("email");
        assertEquals("email", actualUser.getEmail());

        User nullUser = userRepository.getByEmail("notexist");
        assertNull(nullUser);
    }

    @Test
    public void countByEmail() {
        int actualCnt = userRepository.countByEmail("email");
        assertEquals(1, actualCnt);

        actualCnt = userRepository.countByEmail("notexist");
        assertEquals(0, actualCnt);
    }

}
