package io.sunbit.app.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.sunbit.app.entity.Position;

@Repository
public interface IPositionDao extends JpaRepository<Position, Long> {

	Optional<Position> findByNameIgnoreCase(String positionName);
}
