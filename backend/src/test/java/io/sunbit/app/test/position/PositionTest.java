
package io.sunbit.app.test.position;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Position;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class PositionTest {

    @Autowired
    IPositionDao positionDao;

    @Test
    @DisplayName("Test position saving")
    public void testPositionSaving() {
        Position position = new Position();
        position.setName("QA Engineer");
        Position savedPosition = positionDao.save(position);
        assertThat(savedPosition).isNotNull();
        assertThat(savedPosition.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test position updating")
    public void testPositionUpdating() {
        Position position = positionDao.findByNameIgnoreCase("QA Engineer").orElse(null);
        assertThat(position).isNotNull();
        position.setName("QA Lead");
        Position updatedPosition = positionDao.save(position);
        assertThat(updatedPosition.getName()).isEqualTo("QA Lead");
    }

    @Test
    @DisplayName("Test position deleting")
    public void testPositionDeleting() {
        Position position = positionDao.findByNameIgnoreCase("QA Lead").orElse(null);
        assertThat(position).isNotNull();
        Long id = position.getId();
        positionDao.delete(position);
        assertThat(positionDao.findById(java.util.Objects.requireNonNull(id))).isEmpty();
    }

    @Test
    @DisplayName("Test position finding by id")
    public void testPositionFindingById() {
        Position position = new Position();
        position.setName("Backend Developer");
        Position savedPosition = positionDao.save(position);
        Position foundPosition = positionDao.findById(java.util.Objects.requireNonNull(savedPosition.getId())).orElse(null);
        assertThat(foundPosition).isNotNull();
        assertThat(foundPosition.getName()).isEqualTo("Backend Developer");
    }

    @Test
    @DisplayName("Test list all positions")
    public void testListAllPositions() {
        List<Position> positions = positionDao.findAll();
        assertThat(positions).isNotEmpty();
    }
}
