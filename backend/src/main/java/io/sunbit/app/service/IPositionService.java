package io.sunbit.app.service;

import java.util.List;

import io.sunbit.app.entity.Position;

public interface IPositionService {

	public List<Position> findAll() throws Exception;

	public Position findById(Long id) throws Exception;

	public Position save(Position employeeType) throws Exception;

	public Position update(Long id, Position employeeType) throws Exception;

	public Boolean delete(Long id) throws Exception;

	public Position findByName(String name);
}
