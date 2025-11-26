package io.sunbit.app.service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Position;

@Service
public class PositionServiceImpl implements IPositionService {

	@Autowired
	private IPositionDao positionDao;

	@Override
	public List<Position> findAll() throws Exception {
		try {
			return positionDao.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Position findById(Long id) throws Exception {
		try {
			Optional<Position> optionalPosition = positionDao.findById(id);
			return optionalPosition.get();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Position save(Position position) throws Exception {
		try {
			Position newPosition = positionDao.save(position);
			return newPosition;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Position update(Long id, Position position) throws Exception {
		Position updatedPosition = null;
		try {
			Optional<Position> optionalPosition = positionDao.findById(id);
			Position oldPosition = optionalPosition.get();
			if (oldPosition != null) {
				updatedPosition = positionDao.save(position);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return updatedPosition;
	}

	@Override
	@Transactional
	public Boolean delete(Long id) throws Exception {
		boolean isDeleted = false;
		try {
			if (positionDao.existsById(id)) {
				positionDao.deleteById(id);
				isDeleted = true;
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return isDeleted;
	}

	@Override
	public Position findByName(String name) {
		Optional<Position> optPosition = positionDao.findByNameIgnoreCase(name);
		return optPosition.get();
	}
}
